package com.dragon.wlan_webrtc_server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;



public class SignalServer extends WebSocketServer {
    private static final String TAG = "SignalServer";
    ISignalCallBack mISignalCallBack;

    public SignalServer(ISignalCallBack iSignalCallBack) {
        super(new InetSocketAddress(8887));
        mISignalCallBack = iSignalCallBack;
    }

    @Override
    public void onStart() {
        Log.d("SignalServer", "=== SignalServer onStart()");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d("SignalServer", "=== SignalServer onOpen()");
        if(mISignalCallBack != null) {
            mISignalCallBack.onOpen(conn, handshake);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d("SignalServer", "=== SignalServer onClose()" + " code: "+ code+ " reason: " + reason+ " remote: " + remote);
        if(mISignalCallBack != null) {
            mISignalCallBack.onClose(conn, code, reason, remote);
        }

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("SignalServer", "=== SignalServer onMessage() message="+message);
        if(mISignalCallBack != null) {
            mISignalCallBack.onMessage(conn, message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d("SignalServer", "=== SignalServer onError()");
        if(mISignalCallBack != null) {
            mISignalCallBack.onError(conn, ex);
        }

    }
}


