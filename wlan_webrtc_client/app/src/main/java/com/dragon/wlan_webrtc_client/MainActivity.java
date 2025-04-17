package com.dragon.wlan_webrtc_client;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.QuickContactBadge;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private SignalClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","onCreate");
        /** ---------开始连接信令服务----------- */
        mClient = SignalClient.INSTANCE(this);
        mClient.connect();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Log.d("MainActivity","呼叫老师");
            ChatSingleActivity.openActivity(this, true, SignalClient.clentID, "laoshi", null);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.close();
    }
}