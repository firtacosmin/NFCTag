package com.kisi.acai.nfctag

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

/**
 * Created by firta on 7/11/2017.
 * the service that will respond to the tag reader
 */
class NFCTagHostApduService : HostApduService() {

    private val TAG = "NFCTagHostApduService";

    /**
     * the messages to send
     */
    private val UNLOCK_MSJ = "unlock"
    private val NOTHING_MSJ = "nothing"

    override fun onDeactivated(reason: Int) {
        Log.d(TAG,"::onDeactivated");
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d(TAG,"::processCommandApdu");
        return UNLOCK_MSJ.toByteArray()
    }
}