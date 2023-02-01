package com.example.nfcdemoandroid.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.nfcdemoandroid.R
import java.io.IOException

class NFCWrite : Activity() {
    private var evTagMessage: TextView? = null
    private var ed_text: EditText? = null
    private var btn_send: Button? = null
    private var mNfcAdapter: NfcAdapter? = null
    private val isWrite = true
   // private var ivBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nfc_write)
        initViews()
      //  ivBack!!.setOnClickListener { finish() }
    }

    private fun initViews() {
    //    ivBack = findViewById(R.id.ivBack)
        evTagMessage = findViewById(R.id.text_dashboard)
        ed_text = findViewById(R.id.ed_text)
        btn_send = findViewById(R.id.btn_send)
        btn_send?.setOnClickListener {
            evTagMessage?.text=ed_text?.text
        }
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    fun writeTag(tag: Tag, message: NdefMessage): Boolean {
        val size = message.toByteArray().size
        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    Log.d(TAG, "writeTag: ndef isWritable ")
                    return false
                }
                if (ndef.maxSize < size) {
                    Log.d(TAG, "writeTag: size size $size")
                    return false
                }
                ndef.writeNdefMessage(message)
                true
            } else {
                val format = NdefFormatable.get(tag)
                if (format != null) {
                    try {
                        format.connect()
                        format.format(message)
                        true
                    } catch (e: IOException) {
                        Log.d(TAG, "writeTag: format e $e")
                        false
                    }
                } else {
                    Log.d(TAG, "writeTag: format null $tag")
                    Log.d(TAG, "writeTag: format null ")
                    false
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "writeTag: crash $e")
            false
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
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            if (isWrite) {
                val messageToWrite = evTagMessage!!.text.toString()
                if (messageToWrite != null && !TextUtils.equals(messageToWrite,
                        "null"
                    ) && !TextUtils.isEmpty(messageToWrite)
                ) {
                    val record = NdefRecord.createMime(messageToWrite, messageToWrite.toByteArray())
                    val message = NdefMessage(arrayOf(record))
                    if (writeTag(tag, message)) {
                        Toast.makeText(
                            this,
                            ("message_write_success"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            ("message_write_error"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    evTagMessage!!.error = "Please enter the text to write"
                    /*Toast.makeText(this,"Please enter the text to write",Toast.LENGTH_LONG).show();*/
                }
            }
        }
    }

    companion object {
        val TAG = NFCWrite::class.java.simpleName
    }
}