package com.dragon.wlan_webrtc_client;

import android.content.Context;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SignalClientManager implements ISignalCallBack{
    public static String clentID = "111";
    /**
     * ---------和信令服务相关-----------
     */
    private static String address = "ws://192.168.115.120";

    private static int port = 8887;
    private URI mSignalServer;
    private Context mContext;
    //ims回调
    private List<ImsCallBack> mImsCallback = new ArrayList<>();

    private SignalClient mSignalClient;

    public static volatile SignalClientManager INSTANCE;

    public static SignalClientManager INSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (SignalClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SignalClientManager(context);
                }
            }
        }
        return INSTANCE;
    }

    private SignalClientManager(Context context) {
        mContext = context;
        try {
            mSignalServer = new URI(address + ":" + port);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public void connect() {

    }

    public void close() {

    }

    public void send( String text ) {

    }
    /**
     * 注册ims连接状态callback
     * @param imsCallback
     */
    public void registerImsCallback(ImsCallBack imsCallback) {
        synchronized (mImsCallback) {
            if(!mImsCallback.contains(imsCallback)) {
                mImsCallback.add(imsCallback);
            }
        }
    }

    /**
     * 注销ims连接状态callback
     * @param imsCallback
     */
    public void unRegisterImsConnectCallBack(ImsCallBack imsCallback) {
        synchronized (mImsCallback) {
            if(mImsCallback.contains(imsCallback)) {
                mImsCallback.remove(imsCallback);
            }
        }
    }
}
