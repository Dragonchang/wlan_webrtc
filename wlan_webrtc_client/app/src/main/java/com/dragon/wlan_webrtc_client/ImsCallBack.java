package com.dragon.wlan_webrtc_client;

import org.json.JSONObject;

public interface ImsCallBack {
    void onRemoteCandidateReceived(JSONObject message);
    void onRemoteAnswerReceived(JSONObject message);
    void onHangup(String reason);
}
