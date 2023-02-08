package com.example.nfcdemoandroid.ui.dashboard

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.TNF_MIME_MEDIA
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nfcdemoandroid.MainActivity
import com.example.nfcdemoandroid.databinding.FragmentDashboardBinding
import java.nio.charset.Charset
import java.util.*


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var tag: Tag

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        _binding?.btnSend?.setOnClickListener {
            val text = _binding?.textDashboard?.text


            writeTag((requireActivity() as MainActivity).getNFCtag(), text.toString())
            /**
             * simple text/plain NDEF Message
             */
            //     (requireActivity() as MainActivity).getNFCAdapter()?.setNdefPushMessage(createTextRecord(text.toString()), requireActivity())
            /**
             * simple beam NDEF Message
             * - need to add same mimeType in Manifest as well
             */
            //   (requireActivity() as MainActivity).getNFCAdapter()?.setNdefPushMessage(createNdefBeamMessage(),requireActivity())

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun writeTag(tag: Tag?, tagText: String) {
//        val typeBytes = mimeType.toByteArray()
//        val payload = tagData.toByteArray()
//        val r1 = NdefRecord(TNF_MIME_MEDIA, typeBytes, null, payload)
//        val r2 = NdefRecord.createApplicationRecord(context?.packageName)
//        var ndefMessage = NdefMessage(arrayOf(r1, r2))

        NfcA.get(tag)?.use { ultralight ->
            ultralight.connect()
            Charset.forName("US-ASCII").also { usAscii ->
//                ultralight.writePage(4, "abcd".toByteArray(usAscii))
//                ultralight.writePage(5, "efgh".toByteArray(usAscii))
//                ultralight.writePage(6, "ijkl".toByteArray(usAscii))
//                ultralight.writePage(7, "mnop".toByteArray(usAscii))
            }
        }
    }

//    fun createTextRecord(
//        payload: String,
//        locale: Locale = Locale.getDefault(),
//        encodeInUtf8: Boolean = true
//    ): NdefMessage {
//        val langBytes = locale.language.toByteArray(Charset.forName("US-ASCII"))
//        val utfEncoding = if (encodeInUtf8) Charset.forName("UTF-8") else Charset.forName("UTF-16")
//        val textBytes = payload.toByteArray(utfEncoding)
//        val utfBit: Int = if (encodeInUtf8) 0 else 1 shl 7
//        val status = (utfBit + langBytes.size).toChar()
//        val data = ByteArray(1 + langBytes.size + textBytes.size)
//        data[0] = status.toByte()
//        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
//        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
//        return NdefMessage(
//            arrayOf(
//                NdefRecord(
//                    NdefRecord.TNF_WELL_KNOWN,
//                    NdefRecord.RTD_TEXT,
//                    ByteArray(0),
//                    data
//                )
//            )
//        )
//    }

//    fun createNdefBeamMessage(): NdefMessage {
//        val text = "Beam me up, Android!\n\n" +
//                "Beam Time: " + System.currentTimeMillis()
//        return NdefMessage(
//            arrayOf(
//                createMime("application/vnd.com.example.android.beam", text.toByteArray())
//            )
//        )
//    }
}