package com.example.nfcdemoandroid

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcdemoandroid.databinding.ActivityMainBinding
import com.example.nfcdemoandroid.ui.NFCRead.TAG
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var nfcAdapter: NfcAdapter? = null
    lateinit var mPendingIntent: PendingIntent
    var tag: Tag? = null

    var intentFiltersArray = arrayOf<IntentFilter>()
    var techListsArray = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        intentFiltersArray = arrayOf(ndef)

        techListsArray = arrayOf<String>(
            NfcA::class.java.name,
            NfcB::class.java.name,
            NfcF::class.java.name,
            NfcV::class.java.name,
            Ndef::class.java.name,
            NdefFormatable::class.java.name,
            MifareClassic::class.java.name,
            MifareUltralight::class.java.name,
            IsoDep::class.java.name,


            )
        mPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                javaClass
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
        )

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
//        nfcAdapter?.enableForegroundDispatch(
//            this, 0, intentFiltersArray,
//            0
//        )
        enableNfcForegroundDispatch()
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


    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            getNFCAdapter()?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("error", "Error enabling NFC foreground dispatch", ex)
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
        }
        Log.e("NDEF Messages intent", msgs.toString())
        Toast.makeText(this, msgs.toString(), Toast.LENGTH_LONG).show()
//        _binding?.textHome?.text = msgs.toString()

        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        Log.e("tag intent", tag?.id.toString())

    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }


    fun getNFCAdapter() = nfcAdapter

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        var tags = intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG)
        Log.d(TAG, "onNewIntent: " + intent!!.action)

        if (tag != null) {
            val ndef = Ndef.get(tag)
            Toast.makeText(
                applicationContext, "${ndef.tag}",
                Toast.LENGTH_SHORT
            ).show()
            readFromNFC(ndef)
        } else {
            Toast.makeText(
                applicationContext, "Problem reading NFC tag!\nPlease try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
        //setIntent(intent)
    }

    fun getNFCtag() = tag

    private fun readFromNFC(ndef: Ndef) {
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            if (ndefMessage != null) {
                val records = ndefMessage.records

                //records will produce an array of strings stored on the tag
                //iterate through them as handle as required.
                Toast.makeText(
                    applicationContext, "Record $records",
                    Toast.LENGTH_SHORT
                ).show()
            }

            ndef.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
    }
}