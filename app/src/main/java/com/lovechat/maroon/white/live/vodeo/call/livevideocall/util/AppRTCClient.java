package com.lovechat.maroon.white.live.vodeo.call.livevideocall.util;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;


public interface AppRTCClient {


    public interface SignalingEvents {
        void onChannelClose();

        void onChannelError(String str);

        void onConnectedToRoom(SignalingParameters signalingParameters);

        void onRemoteDescription(SessionDescription sessionDescription);

        void onRemoteIceCandidate(IceCandidate iceCandidate);

        void onRemoteIceCandidatesRemoved(IceCandidate[] iceCandidateArr);
    }

    void connectToRoom(RoomConnectionParameters roomConnectionParameters);

    void disconnectFromRoom();

    void sendAnswerSdp(SessionDescription sessionDescription);

    void sendLocalIceCandidate(IceCandidate iceCandidate);

    void sendLocalIceCandidateRemovals(IceCandidate[] iceCandidateArr);

    void sendOfferSdp(SessionDescription sessionDescription);


    public static class RoomConnectionParameters {
        public final boolean loopback;
        public final String roomId;
        public final String roomUrl;
        public final String urlParameters;

        public RoomConnectionParameters(String str, String str2, boolean z, String str3) {
            this.roomUrl = str;
            this.roomId = str2;
            this.loopback = z;
            this.urlParameters = str3;
        }

        public RoomConnectionParameters(String str, String str2, boolean z) {
            this(str, str2, z, null);
        }
    }


    public static class SignalingParameters {
        public final String clientId;
        public final List<IceCandidate> iceCandidates;
        public final List<PeerConnection.IceServer> iceServers;
        public final boolean initiator;
        public final SessionDescription offerSdp;
        public final String wssPostUrl;
        public final String wssUrl;

        public SignalingParameters(List<PeerConnection.IceServer> list, boolean z, String str, String str2, String str3, SessionDescription sessionDescription, List<IceCandidate> list2) {
            this.iceServers = list;
            this.initiator = z;
            this.clientId = str;
            this.wssUrl = str2;
            this.wssPostUrl = str3;
            this.offerSdp = sessionDescription;
            this.iceCandidates = list2;
        }
    }
}
