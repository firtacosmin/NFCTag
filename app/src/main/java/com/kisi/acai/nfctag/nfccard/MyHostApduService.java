package com.kisi.acai.nfctag.nfccard;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by firta on 7/14/2017.
 * The service that will emulate the NFC CARD
 */
public class MyHostApduService extends HostApduService {


    public static final String RECEIVE_HELLO = "HELLO";
    public static final String RECEIVE_START = "KNOCK KNOCK";
    public static final String SEND_1 = "WHO'S THERE?";
    public static final String RECEIVE_1 = "I AM GROOT!";
    public static final String NOTHING = "nothing";
    public static final String UNLOCK = "unlock";

    public static final String MESSAGE_EXTRA_NAME = "message";

    private static final String TAG = "MyHostApduService";

    private String messageToSend = NOTHING;



    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (selectAidApdu(apdu)) {
            Log.i(TAG, "Application selected");
            return getWelcomeMessage();
        }
        else {
            return getNextMessage(apdu);
        }
    }

    private byte[] getWelcomeMessage() {
        return RECEIVE_HELLO.getBytes();
    }

    private byte[] getNextMessage(byte[] apdu) {
        String textEncoding = "UTF-8";
        byte[] ret = {};
        try {
            String message = new String(apdu, 0, apdu.length, textEncoding);
            Log.d(TAG,"::getNextMessage received text:"+message);
            switch (message) {
                case RECEIVE_START:
                    ret = SEND_1.getBytes(textEncoding);
                    break;
                case RECEIVE_1:
                    ret = messageToSend.getBytes(textEncoding);
                    break;
                default:
                    ret = NOTHING.getBytes(textEncoding);
                    break;
            }

            String retMsj = new String(ret, 0, ret.length, textEncoding);
            Log.d(TAG,"::getNextMessage will return :"+retMsj);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG,"::getNextMessage caught error :(");
            e.printStackTrace();
        }

        return  ret;
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.i(TAG, "Deactivated: " + reason);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"::onStartCommand");
        String newMessage = intent.getStringExtra(MESSAGE_EXTRA_NAME);
        if ( newMessage != null && !newMessage.isEmpty() ){
            messageToSend = newMessage;
            Log.d(TAG,"::onStartCommand new message: "+messageToSend);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}