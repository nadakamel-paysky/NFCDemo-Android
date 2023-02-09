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
            val text = _binding?.textDashboard?.text
            /**
             * simple text/plain NDEF Message
             */
            (requireActivity() as MainActivity).getNFCAdapter()?.setNdefPushMessage(createNdefBeamMessage(text.toString()), requireActivity())

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createNdefBeamMessage(textToSend:String): NdefMessage {
        val stringOut: String = textToSend
        val bytesOut = stringOut.toByteArray()
        val ndefRecordOut = NdefRecord(
            NdefRecord.TNF_MIME_MEDIA,
            "text/plain".toByteArray(), byteArrayOf(),
            bytesOut
        )
        return NdefMessage(ndefRecordOut)
    }
}