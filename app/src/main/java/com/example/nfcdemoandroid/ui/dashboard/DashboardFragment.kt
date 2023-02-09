package com.example.nfcdemoandroid.ui.dashboard

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
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
            val text = _binding?.ed?.text

            /**
             * simple text/plain NDEF Message
             */
            (requireActivity() as MainActivity).getNFCAdapter()?.setNdefPushMessage(createNdefBeamMessage(text), requireActivity())
            /**
             * simple beam NDEF Message
             * - need to add same mimeType in Manifest as well
             */
         //   (requireActivity() as MainActivity).getNFCAdapter()?.setNdefPushMessage(createNdefBeamMessage(),requireActivity())

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).getNFCAdapter()?.disableReaderMode(requireActivity())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createTextRecord(
        payload: String,
        locale: Locale = Locale.getDefault(),
        encodeInUtf8: Boolean = true
    ): NdefMessage {
        val langBytes = locale.language.toByteArray(Charset.forName("US-ASCII"))
        val utfEncoding = if (encodeInUtf8) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val textBytes = payload.toByteArray(utfEncoding)
        val utfBit: Int = if (encodeInUtf8) 0 else 1 shl 7
        val status = (utfBit + langBytes.size).toChar()
        val data = ByteArray(1 + langBytes.size + textBytes.size)
        data[0] = status.toByte()
        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
        return NdefMessage(
            arrayOf(
                NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    ByteArray(0),
                    data
                )
            )
        )
    }

    fun createNdefBeamMessage(text: CharSequence?): NdefMessage {
        val textneded = "${text}"
        return NdefMessage(
            arrayOf(
                createMime("text/plain", textneded.toByteArray())
            )
        )
    }
}