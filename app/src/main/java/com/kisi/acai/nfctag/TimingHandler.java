package com.kisi.acai.nfctag;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by firta on 7/13/2017.
 */

public class TimingHandler extends Handler {
    public static final int TICK = 1;
    public static final int STOP_TIMING = 0;


    public static final int TICK_MILLIS = 3000;

    private boolean stop = false;

    private WeakReference<NFCTagActivity> actWR = new WeakReference<NFCTagActivity>(null);

    public void setActivity(NFCTagActivity act){
        actWR = new WeakReference<NFCTagActivity>(act);
    }



    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if ( msg.what == TICK ){
            /*if a tick has passed then announce the activity and resend message */
            NFCTagActivity act = actWR.get();
            if ( act != null && !stop){
                act.tick();
                this.sendMessageDelayed(obtainMessage(TICK),TICK_MILLIS);
            }
        }else if ( msg.what == STOP_TIMING){
            /*if stop message is received then save a flag and do nothing*/
            stop = true;
        }

    }

    public void start() {
        sendMessageDelayed(obtainMessage(TimingHandler.TICK), TimingHandler.TICK_MILLIS);
    }
}
