package com.dragon.wlan_webrtc_client;

public enum MessageType {
    /**
     * 用户上线注册消息应答
     */
    REGISTER("register"),
    OFFER("offer"),
    ANSWER("answer"),
    ICE_CANDIDATE("candidate"),
    UN_KNOWN("unknown");

    private String id;

    MessageType(String id) {
        this.id = id;
    }

    public static MessageType getIdRes(String idRes) {
        for (MessageType idResponse : MessageType.values()) {
            if (idRes.equals(idResponse.getId())) {
                return idResponse;
            }
        }
        return UN_KNOWN;
    }

    public String getId() {
        return id;
    }
}
