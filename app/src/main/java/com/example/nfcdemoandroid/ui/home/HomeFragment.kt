package com.example.nfcdemoandroid.ui.home

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.nfcdemoandroid.MainActivity
import com.example.nfcdemoandroid.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    var nfcAdapter: NfcAdapter? = null
    lateinit var pendingIntent: PendingIntent

    private var homeViewModel: HomeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        nfcAdapter = (requireActivity() as MainActivity).getNFCAdapter()

        val textView: TextView = binding.textHome
        homeViewModel?.text?.observe(viewLifecycleOwner) {
            textView.text = it
        }



        return root
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        homeViewModel?.getMessage()?.observe(requireActivity(),
            Observer<String?> { message ->
               // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                _binding?.textHome?.text = message
            })

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}