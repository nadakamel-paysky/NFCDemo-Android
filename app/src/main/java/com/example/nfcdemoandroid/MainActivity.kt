package com.example.nfcdemoandroid

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcdemoandroid.databinding.ActivityMainBinding
import com.example.nfcdemoandroid.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var nfcAdapter: NfcAdapter? = null
    lateinit var mPendingIntent: PendingIntent
    var tag: Tag? = null

    var intentFiltersArray = arrayOf<IntentFilter>()
    var techListsArray = arrayOf<String>()
    var homeViewModel: HomeViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel?.init()

        nfcAdapter?.setNdefPushMessage(null, this, this);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                 You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }



        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val nfcIntentFilter = arrayOf(techDetected, tagDetected, ndefDetected)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        if (nfcAdapter != null) nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            nfcIntentFilter,
            null
        )

        /**
         * TAG dispatch system used :
         * Action_NDEF_DISCOVERD ->> when NDEF MESSAGES are obtained and successful of
         * mapping to MimeType/URI with payload data
         * Action_TECH_DISCOVERED ->>  when NDEF MEssages fails to be mapped to Mime Type /URI
         * Action_TAG_DISCOVERD ->> when both above actions failed .
         */
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent?.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action
        ) {
            processIntent(intent)
        }


    }



    private fun processIntent(intent: Intent) {
        /**
         * for records found in messages we have : 1- TNF (Type Name Format ) as : TNF_WELL_KNOWN ,TNF_UKNOWN
         * 2- payload (data sent )
         * 3- Type
         */
        val rawmsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        val msgs = mutableListOf<NdefMessage>()
        if (rawmsgs?.isNotEmpty() == true) {
            rawmsgs.forEachIndexed { index, parcelable ->
                msgs.add(rawmsgs[index] as NdefMessage)
                msgs[0].records[0].payload
            }
            Log.e("NDEF Messages intent", msgs.toString())
            val inMsg: String = String(msgs[0].records[0].payload)
            homeViewModel?.sendData(inMsg)

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            Log.e("tag intent", tag?.id.toString())
        }

        nfcAdapter?.disableReaderMode(this)

    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }


    fun getNFCAdapter() = nfcAdapter

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tagFromIntent: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        Toast.makeText(this,tagFromIntent?.id.toString(),Toast.LENGTH_SHORT).show()
        Log.e("tags id",tagFromIntent?.id.toString())
        Log.e("tags list",tagFromIntent?.techList.toString())
        readTag(tagFromIntent)
        setIntent(intent)
    }


    fun writeTag(tag: Tag, tagText: String) {
        NfcA.get(tag)?.use { nfc ->
            nfc.connect()
            Charset.forName("US-ASCII").also { usAscii ->
//                iso.writePage(4, "abcd".toByteArray(usAscii))
//                ultralight.writePage(5, "efgh".toByteArray(usAscii))
//                ultralight.writePage(6, "ijkl".toByteArray(usAscii))
//                ultralight.writePage(7, "mnop".toByteArray(usAscii))
            }
        }
    }

    private fun readTag(tagFromIntent: Tag?) {
        val SELECT = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x0A.toByte(),  // Length
            0x63, 0x64, 0x63, 0x00, 0x00, 0x00, 0x00, 0x32, 0x32, 0x31 // AID
        )
        tag
         IsoDep.get(tagFromIntent)?.use { iso ->
             iso.connect()
             if (iso.isConnected) {
                 Log.e("result", iso.tag.techList.toString())
                 //  mifare.tag.techList.toString()
//            val payload = mifare.readPages(4)
//            String(payload, Charset.forName("US-ASCII"))
                 val result: ByteArray = (iso).transceive(SELECT)
                 //  if (!(result[0] == 0x90.toByte() && result[1] == 0x00.toByte())) throw IOException("could not select applet")
                 val str: String = bytesToHex(result)
                 Log.e("result",  str.toString())
                 iso.close()

             }


         }
    }

    protected val hexArray = "0123456789ABCDEF".toCharArray()
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
    fun getNFCtag() = tag

}