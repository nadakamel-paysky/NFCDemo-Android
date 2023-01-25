package com.example.nfcdemoandroid.ui.home

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
     var nfcAdapter: NfcAdapter?=null
    lateinit var pendingIntent:PendingIntent
    var intentFiltersArray = arrayOf<IntentFilter>()
    var techListsArray = arrayOf<String>()

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


        pendingIntent = PendingIntent.getActivity(requireActivity(), 0, requireActivity().intent,PendingIntent.FLAG_MUTABLE)
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                 You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }
         intentFiltersArray = arrayOf(ndef)

        techListsArray = arrayOf<String>(NfcF::class.java.name)


        return root
    }


    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(requireActivity(), pendingIntent, intentFiltersArray,
            arrayOf(techListsArray)
        )

        /**
         * TAG dispatch system used :
         * Action_NDEF_DISCOVERD ->> when NDEF MESSAGES are obtained and successful of
         * mapping to MimeType/URI with payload data
         * Action_TECH_DISCOVERED ->>  when NDEF MEssages fails to be mapped to Mime Type /URI
         * Action_TAG_DISCOVERD ->> when both above actions failed .
         */
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == requireActivity().intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED== requireActivity().intent?.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == requireActivity().intent?.action) {
            processIntent(requireActivity().intent)
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
        if (rawmsgs?.isNotEmpty() == true){
            rawmsgs.forEachIndexed { index, parcelable ->
                msgs.add(rawmsgs[index] as NdefMessage)
                msgs[0].records[0].payload
            }
        }
        Log.e("NDEF Messages intent",msgs.toString())
        Toast.makeText(requireContext(),msgs.toString(),Toast.LENGTH_LONG).show()

        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        Log.e("tag intent",tag?.id.toString())

    }
    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}