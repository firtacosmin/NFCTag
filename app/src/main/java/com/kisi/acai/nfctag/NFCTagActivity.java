package com.kisi.acai.nfctag;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.kisi.acai.nfctag.databinding.ActivityNfctagBinding;
import com.kisi.acai.nfctag.nfccard.MyHostApduService;

/**
 * Created by firta on 7/12/2017.
 * this is the activity that will send the "unlock" and "nothing" messages to the reader device
 */

public class NFCTagActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {


    /**
     * the messages to send
     */
    private static final String UNLOCK_MSJ = MyHostApduService.UNLOCK;
    private static final String NOTHING_MSJ = MyHostApduService.NOTHING;


    private static final int MESSAGE_SENT = 1;
    private static final String TAG = "NFCTagActivity";


    private String messageToSend = UNLOCK_MSJ;
    NfcAdapter mNfcAdapter;
    private ActivityNfctagBinding binding;

    private TimingHandler timeHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nfctag);
        binding.setActivity(this);

        // Check for available NFC Adapter
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (mNfcAdapter == null) {
////            mInfoText.setText("NFC is not available on this device.");
//            binding.textView.setText("NFC is not available on this device.");
//        } else {
//            binding.textView.setText("All Good.");
//            // Register callback to set NDEF message
//            mNfcAdapter.setNdefPushMessageCallback(this, this);
//            // Register callback to listen for message-sent success
//            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
//        }
        timeHandler = new TimingHandler();
        timeHandler.setActivity(this);
        timeHandler.start();

    }


    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = messageToSend+"\n\n";
        Log.d(TAG,"::createNdefMessage sent message: "+text);
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/com.kisi.acai.nfcreader", text.getBytes())
        );
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
       binding.textView.setText(new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If NFC is not available, we won't be needing this menu
        if (mNfcAdapter == null) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void onCheckClick(){
        if ( binding.sendNothing.isChecked() ){
            messageToSend = NOTHING_MSJ;
        }else{
            messageToSend = UNLOCK_MSJ;
        }
        updateServiceMessage();
    }

    public void tick() {

        toggleMessage();

    }

    private void toggleMessage() {

        binding.sendNothing.toggle();
        if ( binding.sendNothing.isChecked() ){
            messageToSend = NOTHING_MSJ;
        }else{
            messageToSend = UNLOCK_MSJ;
        }
        updateServiceMessage();

    }

    private void updateServiceMessage(){
        Intent intent = new Intent(this, MyHostApduService.class);
        intent.putExtra("message", messageToSend);
        startService(intent);
    }
}
