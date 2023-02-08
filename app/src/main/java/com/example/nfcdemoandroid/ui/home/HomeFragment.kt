package com.example.nfcdemoandroid.ui.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nfcdemoandroid.MainActivity
import com.example.nfcdemoandroid.databinding.FragmentHomeBinding
import java.nio.charset.Charset

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    var nfcAdapter: NfcAdapter? = null
    lateinit var pendingIntent: PendingIntent


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        nfcAdapter = (requireActivity() as MainActivity).getNFCAdapter()

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
      //  readTag((requireActivity() as MainActivity).getNFCtag())



        return root
    }

    fun readTag(tag: Tag?): String? {
        return MifareUltralight.get(tag)?.use { mifare ->
            mifare.connect()
            val payload = mifare.readPages(4)
            Log.e("nfc home fragment", "Im read moode")
            String(payload, Charset.forName("US-ASCII"))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}