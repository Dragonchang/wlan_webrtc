package com.dragon.wlan_webrtc_client;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    /**
     * ---------和信令服务相关-----------
     */
    private final String address = "ws://192.168.115.120";

    private final int port = 8887;

    private SignalClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","onCreate");
        /** ---------开始连接信令服务----------- */
        mClient = SignalClient.INSTANCE(this);
        mClient.connect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.close();
    }
}