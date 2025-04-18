package com.dragon.wlan_webrtc_client;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SignalClientManager implements ISignalCallBack{
    private static final String TAG = "SignalClientManager";

    /**
     * ---------和信令服务相关-----------
     */
    public static String clentID = "学生1";
    private static String address = "ws://192.168.115.120";
    private static int port = 8887;
    private URI mSignalServer;

    private Context mContext;
    //ims回调
    private final List<ImsCallBack> mImsCallback = new ArrayList<>();

    private SignalClient mSignalClient;
    private IMSRetryHandler imsRetryHandler;

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
        HandlerThread imsRetry = new HandlerThread("ims_retry");
        imsRetry.start();
        imsRetryHandler = new IMSRetryHandler(imsRetry.getLooper(), this);
        mSignalClient = new SignalClient(mSignalServer, this);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("SignalClientManager", "=== SignalClientManager onOpen()");
        //连接成功发送注册成功消息
        JSONObject message = new JSONObject();
        try {
            message.put("type", MessageType.REGISTER.getId());
            message.put("id", clentID);
            this.send(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        Logger.d("=== SignalClientManager onMessage(): message=" + message);
        try {
            JSONObject jsonMessage = new JSONObject(message);

            String type = jsonMessage.getString("type");
            if (type.equals(MessageType.OFFER.getId())) {
                onRemoteOfferReceived(jsonMessage);
            } else if (type.equals(MessageType.ANSWER.getId())) {
                onRemoteAnswerReceived(jsonMessage);
            } else if (type.equals(MessageType.ICE_CANDIDATE.getId())) {
                onRemoteCandidateReceived(jsonMessage);
            } else if (type.equals(MessageType.HANGUP.getId())) {
                String reason = jsonMessage.getString("reason");
                onHangup(reason);
            }  else {
                Logger.e("the type is invalid: " + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Logger.d("=== SignalClientManager onClose(): reason=" + reason + ", remote=" + remote);
        Message message = imsRetryHandler.obtainMessage();
        imsRetryHandler.sendMessageDelayed(message, 3000);
    }

    @Override
    public void onError(Exception ex) {
        Logger.d("=== SignalClientManager onMessage() ex=" + ex.getMessage());
        ex.printStackTrace();
    }

    public void connect() {
        if(mSignalClient != null) {
            if (!mSignalClient.isOpen()) {
                if (mSignalClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                    try {
                        mSignalClient.connect();
                    } catch (IllegalStateException e) {
                    }
                } else if (mSignalClient.getReadyState().equals(ReadyState.CLOSING) || mSignalClient.getReadyState().equals(ReadyState.CLOSED)) {
                    mSignalClient.reconnect();
                }
            } else {
                Logger.d("=== SignalClientManager mSignalClient is open");
            }
        }
    }

    public void close() {
        if(mSignalClient != null) {
            mSignalClient.close();
        }
    }

    public void send( String text ) {
        if(mSignalClient != null) {
            mSignalClient.send(text);
        }
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


    /**
     * 接收到老师端的视频通话请求
     * @param message
     */
    private void onRemoteOfferReceived(JSONObject message) {
        try {
            String description = message.getString("sdp");
            ChatSingleActivity.openActivity(mContext, false, "laoshi", null, description);
        } catch (JSONException e) {
            Logger.d("=== SignalClientManager onRemoteOfferReceived() e=" + e.getMessage());
        }
    }
    private void onRemoteAnswerReceived(JSONObject message) {
        synchronized (mImsCallback) {
            for(ImsCallBack callBack : mImsCallback) {
                callBack.onRemoteAnswerReceived(message);
            }
        }
    }

    private void onRemoteCandidateReceived(JSONObject message) {
        synchronized (mImsCallback) {
            for(ImsCallBack callBack : mImsCallback) {
                callBack.onRemoteCandidateReceived(message);
            }
        }
    }

    private void onHangup(String reason) {
        synchronized (mImsCallback) {
            for(ImsCallBack callBack : mImsCallback) {
                callBack.onHangup(reason);
            }
        }
    }

    /**
     * ims 重连handler
     */
    class IMSRetryHandler extends Handler {
        private final SignalClientManager mSignalClientManager;
        public IMSRetryHandler(Looper looper, SignalClientManager SignalClientManager) {
            super(looper);
            mSignalClientManager = SignalClientManager;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.w(TAG, "retry connect ims");
            mSignalClientManager.connect();
        }
    }
}
