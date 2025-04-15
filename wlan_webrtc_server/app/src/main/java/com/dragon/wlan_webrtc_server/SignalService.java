package com.dragon.wlan_webrtc_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SignalService extends Service {
    public SignalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}