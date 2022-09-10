package com.lovechat.lightgreen.white.meet.live.chat.livevideocall.util;

import org.webrtc.PeerConnection;

import java.util.LinkedList;
import java.util.Random;


public class RoomParametersFetcher {
    private static final String TAG = "RoomRTCClient";
    private static final int TURN_HTTP_TIMEOUT_MS = 5000;
    private final RoomParametersFetcherEvents events;
    private final String roomMessage;
    private final String roomUrl;


    public interface RoomParametersFetcherEvents {
        void onSignalingParametersReady(AppRTCClient.SignalingParameters signalingParameters);
    }

    public RoomParametersFetcher(String str, String str2, RoomParametersFetcherEvents roomParametersFetcherEvents) {
        this.roomUrl = str;
        this.roomMessage = str2;
        this.events = roomParametersFetcherEvents;
    }

    public void makeRequest() {
        LinkedList linkedList = new LinkedList();
        linkedList.add(new PeerConnection.IceServer(Config_Var.turn, Config_Var.user, Config_Var.pass));
        Random random2 = new Random();
        boolean z = Config_Var.initiatorcheck;
      //  Log.d(TAG, "Room ID--randomno: " + random2);
      //  Log.d(TAG, "Room ID--initiator: " + z);
        String str = Config_Var.wsUrl;
        this.events.onSignalingParametersReady(new AppRTCClient.SignalingParameters(linkedList, z, "" + (random2.nextInt(37964929) + 111476), str, "http://abSkas7a7.google7a.com", null, null));
    }
}
