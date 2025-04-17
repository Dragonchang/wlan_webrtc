package com.dragon.wlan_webrtc_client;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SignalClient extends WebSocketClient {

    private ISignalCallBack mISignalCallBack;

    public SignalClient(URI uri, ISignalCallBack iSignalCallBack) {
        super(uri);
        mISignalCallBack = iSignalCallBack;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("SignalClient", "=== SignalClient onOpen()");
        if(mISignalCallBack != null)
            mISignalCallBack.onOpen(handshakedata);
    }

    @Override
    public void onMessage(String message) {
        Logger.d("=== SignalClient onMessage(): message=" + message);
        if(mISignalCallBack != null)
            mISignalCallBack.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Logger.d("=== SignalClient onClose(): reason=" + reason + ", remote=" + remote);
        if(mISignalCallBack != null)
            mISignalCallBack.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        Logger.d("=== SignalClient onMessage() ex=" + ex.getMessage());
        if(mISignalCallBack != null)
            mISignalCallBack.onError(ex);
    }
}
