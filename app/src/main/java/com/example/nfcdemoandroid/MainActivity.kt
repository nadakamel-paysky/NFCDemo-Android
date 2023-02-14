package com.example.nfcdemoandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcdemoandroid.databinding.ActivityMainBinding
import com.example.nfcdemoandroid.utils.HostCardEmulatorService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = findViewById<Button>(R.id.btn_read)
        val editText = findViewById<EditText>(R.id.ed_)
        editText.doOnTextChanged { text, start, before, count ->
            HostCardEmulatorService.STATUS_SUCCESS = text.toString()

        }
        button.setOnClickListener {
            startActivity(Intent(this,ReadActivity::class.java))
        }
    }
}