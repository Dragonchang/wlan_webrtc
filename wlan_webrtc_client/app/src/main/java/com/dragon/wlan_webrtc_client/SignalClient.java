package com.dragon.wlan_webrtc_client;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SignalClient extends WebSocketClient {

    private Context mContext;
    /**
     * ---------和信令服务相关-----------
     */
    private static String address = "ws://192.168.115.120";

    private static int port = 8887;

    //ims回调
    private List<ImsCallBack> mImsCallback = new ArrayList<>();

    public static volatile SignalClient INSTANCE;

    public static SignalClient INSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (SignalClient.class) {
                if (INSTANCE == null) {
                    try {
                        URI mSignalServer = new URI(address + ":" + port);
                        INSTANCE = new SignalClient(context, mSignalServer);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return INSTANCE;
    }

    private SignalClient(Context context, URI uri) {
        super(uri);
        mContext = context;
    }

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
            if(!mImsCallback.contains(imsCallback)) {
                mImsCallback.remove(imsCallback);
            }
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("SignalClient", "=== SignalClient onOpen()");
        //连接成功发送注册成功消息
        JSONObject message = new JSONObject();
        try {
            message.put("type", MessageType.REGISTER.getId());
            message.put("id", "1111");
            this.send(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        Logger.d("=== SignalClient onMessage(): message=" + message);
        try {
            JSONObject jsonMessage = new JSONObject(message);

            String type = jsonMessage.getString("type");
            if (type.equals(MessageType.OFFER.getId())) {
                onRemoteOfferReceived(jsonMessage);
            } else if (type.equals("answer")) {
                //onRemoteAnswerReceived(jsonMessage);
            } else if (type.equals(MessageType.ICE_CANDIDATE.getId())) {
                onRemoteCandidateReceived(jsonMessage);
            } else {
                Logger.e("the type is invalid: " + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Logger.d("=== SignalClient onClose(): reason=" + reason + ", remote=" + remote);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        Logger.d("=== SignalClient onMessage() ex=" + ex.getMessage());
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
            Logger.d("=== SignalClient onRemoteOfferReceived() e=" + e.getMessage());
        }
    }

    private void onRemoteCandidateReceived(JSONObject message) {
        synchronized (mImsCallback) {
            for(ImsCallBack callBack : mImsCallback) {
                callBack.onRemoteCandidateReceived(message);
            }
        }
    }
}
