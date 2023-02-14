package com.example.nfcdemoandroid.utils

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HostCardEmulatorService: HostApduService() {
    companion object {
        val TAG = "Host Card Emulator"
        var STATUS_SUCCESS = ""
        val STATUS_FAILED = "6F00"
        val CLA_NOT_SUPPORTED = "6E00"
        val INS_NOT_SUPPORTED = "6D00"
        val AID = "A0000002471001"
        val SELECT_INS = "A4"
        val DEFAULT_CLA = "00"
        val MIN_APDU_LENGTH = 12
    }
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: " + reason)
    }

    override fun processCommandApdu(commandApdu: ByteArray?,
                                    extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return KotlinUtils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = KotlinUtils.toHex(commandApdu)
        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return KotlinUtils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return KotlinUtils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return KotlinUtils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(10, 24) == AID)  {
            return STATUS_SUCCESS.toByteArray()
          //  return KotlinUtils.hexStringToByteArray(STATUS_SUCCESS)
        } else {
            return KotlinUtils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}