package com.dragon.wlan_webrtc_server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.net.InetSocketAddress;

public class SignalServer extends WebSocketServer {

    private PeerConnection mPeerConnection;

    public SignalServer( int port ) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d("SignalServer", "=== SignalServer onOpen()");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("SignalServer", "=== SignalServer onMessage() message="+message);
        try {
            JSONObject jsonMessage = new JSONObject(message);

            String type = jsonMessage.getString("type");
            if (type.equals("offer")) {
                //onRemoteOfferReceived(jsonMessage);
            }else if(type.equals("answer")) {
                //onRemoteAnswerReceived(jsonMessage);
            }else if(type.equals("candidate")) {
                //onRemoteCandidateReceived(jsonMessage);
            }else{
                //Logger.e("the type is invalid: " + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }
}
