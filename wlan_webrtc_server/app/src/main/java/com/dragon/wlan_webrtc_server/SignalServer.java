package com.dragon.wlan_webrtc_server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SessionDescription;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignalServer extends WebSocketServer {

    public static volatile SignalServer INSTANCE;

    public static SignalServer INSTANCE() {
        if (INSTANCE == null) {
            synchronized (SignalServer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SignalServer();
                }
            }
        }
        return INSTANCE;
    }

    //保存连接成功的客户端信息connect和对应的唯一标识（目前使用ip）
    private final Map<WebSocket, String> mConnectMaps = new HashMap<>();

    //ims回调
    private List<ImsCallback> mImsCallback = new ArrayList<>();

    private SignalServer() {
        super(new InetSocketAddress(8887));

    }

    public void registerImsCallback(ImsCallback imsCallback) {
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
    public void unRegisterImsConnectCallBack(ImsCallback imsCallback) {
        synchronized (mImsCallback) {
            if(!mImsCallback.contains(imsCallback)) {
                mImsCallback.remove(imsCallback);
            }
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d("SignalServer", "=== SignalServer onOpen()");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d("SignalServer", "=== SignalServer onClose()");
        unregister(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("SignalServer", "=== SignalServer onMessage() message="+message);
        try {
            JSONObject jsonMessage = new JSONObject(message);

            String type = jsonMessage.getString("type");
            if (type.equals(MessageType.REGISTER.getId())) {
                registerUser(conn, message);
            }else if (type.equals("offer")) {
                //onRemoteOfferReceived(jsonMessage);
            }else if(type.equals("answer")) {
                onRemoteAnswerReceived(jsonMessage);
            }else if(type.equals(MessageType.ICE_CANDIDATE.getId())) {
                onRemoteCandidateReceived(jsonMessage);
            }else{
                Log.w("SignalServer", "the type is invalid: " + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d("SignalServer", "=== SignalServer onError()");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    /**
     * 获取所有当前连接的客户端列表
     * ip
     * @return
     */
    public List<String> getClientList() {
        return new ArrayList<>(mConnectMaps.values());
    }

    /**
     * 通过clientid获取连接
     * @param clientId
     * @return
     */
    public WebSocket getClientById(String clientId) {
        for (Map.Entry<WebSocket, String> entry : mConnectMaps.entrySet()) {
            if (clientId.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 发送消息给学生端
     * @param clientId
     * @param msg
     */
    public void sendMessage(String clientId, String msg) {
        WebSocket connect = getClientById(clientId);
        if(connect != null) {
            connect.send(msg);
        } else {
            Log.e("SignalServer", "sendMessage failed");
        }
    }

    // 发送方，收到学生端的answer
    private void onRemoteAnswerReceived(JSONObject message) {
        synchronized (mImsCallback) {
            for(ImsCallback callBack : mImsCallback) {
                callBack.onRemoteAnswerReceived(message);
            }
        }
    }

    void onRemoteCandidateReceived(JSONObject message){
        synchronized (mImsCallback) {
            for(ImsCallback callBack : mImsCallback) {
                callBack.onRemoteCandidateReceived(message);
            }
        }
    }
    /**
     * 用户连接上信令服务
     * @param conn
     * @param message
     * @throws JSONException
     */
    private void registerUser(WebSocket conn, String message) throws JSONException {
        if(mConnectMaps.containsKey(conn)) {
            mConnectMaps.remove(conn);
        }
        JSONObject jsonMessage = new JSONObject(message);
        String id = jsonMessage.getString("id");
        mConnectMaps.put(conn, id);
        //让ui刷新client list
        synchronized (mImsCallback) {
            for(ImsCallback callBack : mImsCallback) {
                callBack.refeshClent();
            }
        }
    }

    /**
     * client 断开与信令服务的连接
     * @param conn
     */
    private void unregister(WebSocket conn) {
        if(mConnectMaps.containsKey(conn)) {
            mConnectMaps.remove(conn);
        }
        //让ui刷新client list
        synchronized (mImsCallback) {
            for(ImsCallback callBack : mImsCallback) {
                callBack.refeshClent();
            }
        }
    }
}


