package com.example.nfcdemoandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

     val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private var stringMutableLiveData: MutableLiveData<String>? = null

    fun init() {
        stringMutableLiveData = MutableLiveData()
    }

    fun sendData(msg: String) {
        stringMutableLiveData?.value = msg
    }

    fun getMessage(): LiveData<String>? {
        return stringMutableLiveData
    }
}