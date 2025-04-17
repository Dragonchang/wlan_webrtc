package com.dragon.wlan_webrtc_server;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ImsCallback {

    private SignalServer mServer;
    private final int port = 8887;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "=== onCreate*********************");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        startImsServices();

        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_list_item, // 使用自定义布局
                R.id.textView, // 布局中用于显示文本的 TextView 的 ID
                dataList);
        listView.setAdapter(adapter);
        // 设置点击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取点击的 item 的数据
                String client = (String) parent.getItemAtPosition(position);
                Log.d("MainActivity", "=== onItemClick client: "+client);
                ChatSingleActivity.openActivity(MainActivity.this, true, "master", client, null);
            }
        });
        Log.d("MainActivity", "=== onCreate*********************11111111");
    }

    // 注意这里退出时的销毁动作
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SignalServer.INSTANCE(this).unRegisterImsConnectCallBack(this);
        try {
            Log.d("MainActivity", "=== onDestroy*********************SignalServer stop" + this);
            SignalServer.INSTANCE(this).stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
    /**
     * 启动信令服务
     */
    private void startImsServices() {
        SignalServer.INSTANCE(this).registerImsCallback(this);
        SignalServer.INSTANCE(this).start();
        Log.d("MainActivity", "=== startImsServices*********************SignalServer start" + this);
    }

    @Override
    public void refeshClent() {
        Log.d("MainActivity", "=== refeshClent");
        List<String> clients = SignalServer.INSTANCE(this).getClientList();
        dataList.clear();
        dataList.addAll(clients);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRemoteAnswerReceived(JSONObject message) {

    }

    @Override
    public void onRemoteCandidateReceived(JSONObject message) {

    }

    @Override
    public void onHangup() {

    }
}