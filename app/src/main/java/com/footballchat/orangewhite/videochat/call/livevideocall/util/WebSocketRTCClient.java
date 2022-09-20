package com.footballchat.orangewhite.videochat.call.livevideocall.util;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;


public class WebSocketRTCClient implements AppRTCClient, WebSocketChannelClient.WebSocketChannelEvents {
    private static final String ROOM_JOIN = "join";
    private static final String ROOM_LEAVE = "leave";
    private static final String ROOM_MESSAGE = "message";
    private static final String TAG = "WSRTCClient";
    private RoomConnectionParameters connectionParameters;
    private SignalingEvents events;
    private final Handler handler;
    private String leaveUrl;
    private String messageUrl;
    private ConnectionState roomState = ConnectionState.NEW;
    private SignalingParameters sp;
    private WebSocketChannelClient wsClient;


    public enum ConnectionState {
        NEW,
        CONNECTED,
        CLOSED,
        ERROR
    }


    private enum MessageType {
        MESSAGE,
        LEAVE
    }

    @Override
    public void onWebSocketClose() {
    }

    public WebSocketRTCClient(SignalingEvents signalingEvents) {
        this.events = signalingEvents;
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
    }

    // Put a |key|->|value| mapping in |json|.
    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    @Override // com.app.videocallrandomchat2.AppRTCClient
    public void connectToRoom(RoomConnectionParameters roomConnectionParameters) {
        this.connectionParameters = roomConnectionParameters;
        this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.1
            @Override // java.lang.Runnable
            public void run() {
                WebSocketRTCClient.this.connectToRoomInternal();
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient
    public void disconnectFromRoom() {
        this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.2
            @Override // java.lang.Runnable
            public void run() {
                WebSocketRTCClient.this.disconnectFromRoomInternal();
                WebSocketRTCClient.this.handler.getLooper().quit();
            }
        });
    }


    public void connectToRoomInternal() {
        String connectionUrl = getConnectionUrl(this.connectionParameters);
        this.roomState = ConnectionState.NEW;
        this.wsClient = new WebSocketChannelClient(this.handler, this);
        new RoomParametersFetcher(connectionUrl, null, new RoomParametersFetcher.RoomParametersFetcherEvents() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.3
            @Override // com.app.videocallrandomchat2.RoomParametersFetcher.RoomParametersFetcherEvents
            public void onSignalingParametersReady(final SignalingParameters signalingParameters) {
                WebSocketRTCClient.this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (signalingParameters != null) {
                            WebSocketRTCClient.this.signalingParametersReady(signalingParameters);
                        }
                    }
                });
            }
        }).makeRequest();
    }

    // Disconnect from room and send bye messages - runs on a local looper thread.
    private void disconnectFromRoomInternal() {
       // Log.d(TAG, "Disconnect. Room state: " + roomState);
        if (roomState == ConnectionState.CONNECTED) {
          //  Log.d(TAG, "Closing room.");
            sendPostMessage(MessageType.LEAVE, leaveUrl, null);
        }
        roomState = ConnectionState.CLOSED;
        if (wsClient != null) {
            wsClient.disconnect(true);
        }
    }

    private String getConnectionUrl(RoomConnectionParameters roomConnectionParameters) {
        return roomConnectionParameters.roomUrl + "/" + ROOM_JOIN + "/" + roomConnectionParameters.roomId + getQueryString(roomConnectionParameters);
    }

    private String getMessageUrl(RoomConnectionParameters roomConnectionParameters, SignalingParameters signalingParameters) {
        return roomConnectionParameters.roomUrl + "/" + ROOM_MESSAGE + "/" + roomConnectionParameters.roomId + "/" + signalingParameters.clientId + getQueryString(roomConnectionParameters);
    }

    private String getLeaveUrl(RoomConnectionParameters roomConnectionParameters, SignalingParameters signalingParameters) {
        return roomConnectionParameters.roomUrl + "/" + ROOM_LEAVE + "/" + roomConnectionParameters.roomId + "/" + signalingParameters.clientId + getQueryString(roomConnectionParameters);
    }

    private String getQueryString(RoomConnectionParameters roomConnectionParameters) {
        if (roomConnectionParameters.urlParameters == null) {
            return "";
        }
        return "?" + roomConnectionParameters.urlParameters;
    }


    public void signalingParametersReady(SignalingParameters signalingParameters) {
        if (!this.connectionParameters.loopback || (Config_Var.initiatorcheck && signalingParameters.offerSdp == null)) {
            if (!this.connectionParameters.loopback && !Config_Var.initiatorcheck) {
                SessionDescription sessionDescription = signalingParameters.offerSdp;
            }
            this.messageUrl = getMessageUrl(this.connectionParameters, signalingParameters);
            this.leaveUrl = getLeaveUrl(this.connectionParameters, signalingParameters);
            this.roomState = ConnectionState.CONNECTED;
            this.sp = signalingParameters;
            if (!Config_Var.video_call) {
                Config_Var.initiatorcheck = true;
                this.events.onConnectedToRoom(this.sp);
                return;
            }
            this.wsClient.connect(signalingParameters.wssUrl, signalingParameters.wssPostUrl);
            this.wsClient.register(this.connectionParameters.roomId, signalingParameters.clientId);
            return;
        }
        reportError("Loopback room is busy.");
    }

    @Override
    public void sendOfferSdp(final SessionDescription sessionDescription) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (WebSocketRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    WebSocketRTCClient.this.reportError("Sending offer SDP in non connected state.");
                    return;
                }
                JSONObject jSONObject = new JSONObject();
                WebSocketRTCClient.jsonPut(jSONObject, "sdp", sessionDescription.description);
                WebSocketRTCClient.jsonPut(jSONObject, "type", "offer");
                WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                if (WebSocketRTCClient.this.connectionParameters.loopback) {
                    WebSocketRTCClient.this.events.onRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm("answer"), sessionDescription.description));
                }
            }
        });
    }

    @Override
    public void sendAnswerSdp(final SessionDescription sessionDescription) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (!WebSocketRTCClient.this.connectionParameters.loopback) {
                    JSONObject jSONObject = new JSONObject();
                    WebSocketRTCClient.jsonPut(jSONObject, "sdp", sessionDescription.description);
                    WebSocketRTCClient.jsonPut(jSONObject, "type", "answer");
                    WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                }
            }
        });
    }

    @Override
    public void sendLocalIceCandidate(final IceCandidate iceCandidate) {
        this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.6
            @Override // java.lang.Runnable
            public void run() {
                JSONObject jSONObject = new JSONObject();
                WebSocketRTCClient.jsonPut(jSONObject, "type", "candidate");
                WebSocketRTCClient.jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
                WebSocketRTCClient.jsonPut(jSONObject, "id", iceCandidate.sdpMid);
                WebSocketRTCClient.jsonPut(jSONObject, "candidate", iceCandidate.sdp);
                if (!Config_Var.initiatorcheck) {
                    WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                } else if (WebSocketRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    WebSocketRTCClient.this.reportError("Sending ICE candidate in non connected state.");
                } else {
                    WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                    if (WebSocketRTCClient.this.connectionParameters.loopback) {
                        WebSocketRTCClient.this.events.onRemoteIceCandidate(iceCandidate);
                    }
                }
            }
        });
    }

    @Override
    public void sendLocalIceCandidateRemovals(final IceCandidate[] iceCandidateArr) {
        this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.7
            @Override // java.lang.Runnable
            public void run() {
                JSONObject jSONObject = new JSONObject();
                WebSocketRTCClient.jsonPut(jSONObject, "type", "remove-candidates");
                JSONArray jSONArray = new JSONArray();
                for (IceCandidate iceCandidate : iceCandidateArr) {
                    jSONArray.put(WebSocketRTCClient.this.toJsonCandidate(iceCandidate));
                }
                WebSocketRTCClient.jsonPut(jSONObject, "candidates", jSONArray);
                if (!Config_Var.initiatorcheck) {
                    WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                } else if (WebSocketRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    WebSocketRTCClient.this.reportError("Sending ICE candidate removals in non connected state.");
                } else {
                    WebSocketRTCClient.this.wsClient.send(jSONObject.toString());
                    if (WebSocketRTCClient.this.connectionParameters.loopback) {
                        WebSocketRTCClient.this.events.onRemoteIceCandidatesRemoved(iceCandidateArr);
                    }
                }
            }
        });
    }

    @Override
    public void onWebSocketMessage(String str) {
        if (this.wsClient.getState() == WebSocketChannelClient.WebSocketConnectionState.REGISTERED) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                String string = jSONObject.getString(NotificationCompat.CATEGORY_MESSAGE);
                String optString = jSONObject.optString("error");
                if (string.contains("bye")) {
                    this.events.onChannelClose();
                    return;
                }
                if (!string.contains("type")) {
                    if (string.contains("call_initiator")) {
                        Config_Var.initiatorcheck = true;
                        this.events.onConnectedToRoom(this.sp);
                    }
                    if (string.contains("call_receiver")) {
                        Config_Var.initiatorcheck = false;
                        this.events.onConnectedToRoom(this.sp);
                    }
                } else if (string.length() > 0) {
                    JSONObject jSONObject2 = new JSONObject(string);
                    String optString2 = jSONObject2.optString("type");
                    if (optString2.equals("candidate")) {
                        this.events.onRemoteIceCandidate(toJavaCandidate(jSONObject2));
                    } else if (optString2.equals("remove-candidates")) {
                        JSONArray jSONArray = jSONObject2.getJSONArray("candidates");
                        IceCandidate[] iceCandidateArr = new IceCandidate[jSONArray.length()];
                        for (int i = 0; i < jSONArray.length(); i++) {
                            iceCandidateArr[i] = toJavaCandidate(jSONArray.getJSONObject(i));
                        }
                        this.events.onRemoteIceCandidatesRemoved(iceCandidateArr);
                    } else if (optString2.equals("answer")) {
                        if (Config_Var.initiatorcheck) {
                            this.events.onRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString2), jSONObject2.getString("sdp")));
                            return;
                        }
                        reportError("Received answer for call initiator: " + str);
                    } else if (optString2.equals("offer")) {
                        if (!Config_Var.initiatorcheck) {
                            this.events.onRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString2), jSONObject2.getString("sdp")));
                            return;
                        }
                        reportError("Received offer for call receiver: " + str);
                    } else if (optString2.equals("bye")) {
                        this.events.onChannelClose();
                    } else {
                        reportError("Unexpected WebSocket message: " + str);
                    }
                } else if (optString == null || optString.length() <= 0) {
                    reportError("Unexpected WebSocket message: " + str);
                } else {
                    reportError("WebSocket error message: " + optString);
                }
            } catch (JSONException e) {
                reportError("WebSocket message JSON parsing error: " + e.toString());
            }
        }
    }

    @Override
    public void onWebSocketError(String str) {
        reportError("WebSocket error: " + str);
    }


    public void reportError(final String str) {
        this.handler.post(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                if (WebSocketRTCClient.this.roomState != ConnectionState.ERROR) {
                    WebSocketRTCClient.this.roomState = ConnectionState.ERROR;
                    WebSocketRTCClient.this.events.onChannelError(str);
                }
            }
        });
    }

    private void sendPostMessage(final MessageType messageType, String str, String str2) {
        if (str2 != null) {
            String str3 = str + ". Message: " + str2;
        }
        new AsyncHttpURLConnection("POST", str, str2, new AsyncHttpURLConnection.AsyncHttpEvents() { // from class: com.app.videocallrandomchat2.WebSocketRTCClient.9
            @Override
            public void onHttpError(String str4) {
                WebSocketRTCClient webSocketRTCClient = WebSocketRTCClient.this;
                webSocketRTCClient.reportError("GAE POST error: " + str4);
            }

            @Override
            public void onHttpComplete(String str4) {
                if (messageType == MessageType.MESSAGE) {
                    try {
                        String string = new JSONObject(str4).getString("result");
                        if (!string.equals("SUCCESS")) {
                            WebSocketRTCClient webSocketRTCClient = WebSocketRTCClient.this;
                            webSocketRTCClient.reportError("GAE POST error: " + string);
                        }
                    } catch (JSONException e) {
                        WebSocketRTCClient webSocketRTCClient2 = WebSocketRTCClient.this;
                        webSocketRTCClient2.reportError("GAE POST JSON error: " + e.toString());
                    }
                }
            }
        });
    }


    public JSONObject toJsonCandidate(IceCandidate iceCandidate) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
        jsonPut(jSONObject, "id", iceCandidate.sdpMid);
        jsonPut(jSONObject, "candidate", iceCandidate.sdp);
        return jSONObject;
    }

    private IceCandidate toJavaCandidate(JSONObject jSONObject) throws JSONException {
        return new IceCandidate(jSONObject.getString("id"), jSONObject.getInt("label"), jSONObject.getString("candidate"));
    }
}
