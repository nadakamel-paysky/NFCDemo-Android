package com.example.nfcdemoandroid

import android.R
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcdemoandroid.databinding.ActivityMainBinding
import com.example.nfcdemoandroid.ui.NFCRead
import com.example.nfcdemoandroid.ui.NFCWrite
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val TAG = NFCRead::class.java.simpleName
    private var tvNFCMessage: TextView? = null
    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var binding: ActivityMainBinding
  //  var  read=2131231033
    var  write=2131231031
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        initViews()
        navView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

//                read -> {
//                    val intent = Intent(this@MainActivity, NFCRead::class.java)
//                    startActivity(intent)
//                    return@OnNavigationItemSelectedListener true
//               }
                write-> {
                    val intent = Intent(this@MainActivity, NFCWrite::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

            }

            true
        })


    }

    private fun initViews() {
        tvNFCMessage=binding.txtRead
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
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
        if (mNfcAdapter != null) mNfcAdapter!!.enableForegroundDispatch(
            this,
            pendingIntent,
            nfcIntentFilter,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) mNfcAdapter!!.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        patchTag(tag)
        tag?.let { readFromNFC(it, intent) }
    }


    fun patchTag(oTag: Tag?): Tag? {
        if (oTag == null) return null
        val sTechList = oTag.techList
        val oParcel: Parcel
        val nParcel: Parcel
        oParcel = Parcel.obtain()
        oTag.writeToParcel(oParcel, 0)
        oParcel.setDataPosition(0)
        val len = oParcel.readInt()
        var id: ByteArray? = null
        if (len >= 0) {
            id = ByteArray(len)
            oParcel.readByteArray(id)
        }
        val oTechList = IntArray(oParcel.readInt())
        oParcel.readIntArray(oTechList)
        val oTechExtras = oParcel.createTypedArray(Bundle.CREATOR)
        val serviceHandle = oParcel.readInt()
        val isMock = oParcel.readInt()
        val tagService: IBinder?
        tagService = if (isMock == 0) {
            oParcel.readStrongBinder()
        } else {
            null
        }
        oParcel.recycle()
        var nfca_idx = -1
        var mc_idx = -1
        for (idx in sTechList.indices) {
            if (sTechList[idx] === NfcA::class.java.name) {
                nfca_idx = idx
            } else if (sTechList[idx] === MifareClassic::class.java.name) {
                mc_idx = idx
            }
        }
        if (nfca_idx >= 0 && mc_idx >= 0 && oTechExtras!![mc_idx] == null) {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx]
        } else {
            return oTag
        }
        nParcel = Parcel.obtain()
        nParcel.writeInt(id!!.size)
        nParcel.writeByteArray(id)
        nParcel.writeInt(oTechList.size)
        nParcel.writeIntArray(oTechList)
        nParcel.writeTypedArray(oTechExtras, 0)
        nParcel.writeInt(serviceHandle)
        nParcel.writeInt(isMock)
        if (isMock == 0) {
            nParcel.writeStrongBinder(tagService)
        }
        nParcel.setDataPosition(0)
        val nTag = Tag.CREATOR.createFromParcel(nParcel)
        nParcel.recycle()
        return nTag
    }

    private fun readFromNFC(tag: Tag, intent: Intent) {
        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                if (ndefMessage != null) {
                    /*String message = new String(ndefMessage.getRecords()[0].getPayload());
                    Log.d(TAG, "NFC found.. "+"readFromNFC: "+message );
                    tvNFCMessage.setText(message);*/
                    val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    if (messages != null) {
                        val ndefMessages = arrayOfNulls<NdefMessage>(messages.size)
                        for (i in messages.indices) {
                            ndefMessages[i] = messages[i] as NdefMessage
                        }
                        val record = ndefMessages[0]!!.records[0]
                        val payload = record.payload
                        val text = String(payload)
                        tvNFCMessage!!.setText(text)
                        Log.e("tag", "vahid  -->  $text")
                        ndef.close()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Not able to read from NFC, Please try again...",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                val format = NdefFormatable.get(tag)

                if (format != null) {
                    try {

                        format.connect()
                        val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                        val ndefMessage: NdefMessage? =null
                        with(parcelables){
                            val ndefMessage = this?.get(0) as NdefMessage
                        }


                        if (ndefMessage != null) {
                            val message: String = String(ndefMessage.records.get(0).payload)
                            Log.d(NFCRead.TAG, "NFC found.. readFromNFC: $message")
                            tvNFCMessage!!.setText(message)
                            if (ndef != null) {
                                ndef.close()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Not able to read from NFC, Please try again...",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "NFC is not readable", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}