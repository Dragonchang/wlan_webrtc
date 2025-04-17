package com.dragon.wlan_webrtc_server;

import org.json.JSONObject;

public interface ImsCallback {
    void refeshClent();
    void onRemoteAnswerReceived(JSONObject message);
    void onRemoteCandidateReceived(JSONObject message);
}
