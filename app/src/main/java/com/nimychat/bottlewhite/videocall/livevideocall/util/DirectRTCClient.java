package com.nimychat.bottlewhite.videocall.livevideocall.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class DirectRTCClient implements AppRTCClient, TCPChannelClient.TCPChannelEvents {
    private static final int DEFAULT_PORT = 8888;
    public static final Pattern IP_PATTERN = Pattern.compile("(((\\d+\\.){3}\\d+)|\\[((([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?)\\]|\\[(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4})\\]|((([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?)|(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4})|localhost)(:(\\d+))?");
    private static final String TAG = "DirectRTCClient";
    private RoomConnectionParameters connectionParameters;
    private final SignalingEvents events;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ConnectionState roomState = ConnectionState.NEW;
    @Nullable
    private TCPChannelClient tcpClient;

        public enum ConnectionState {
        NEW,
        CONNECTED,
        CLOSED,
        ERROR
    }

    public DirectRTCClient(SignalingEvents signalingEvents) {
        this.events = signalingEvents;
    }

    @Override
    public void connectToRoom(RoomConnectionParameters roomConnectionParameters) {
        this.connectionParameters = roomConnectionParameters;
        if (roomConnectionParameters.loopback) {
            reportError("Loopback connections aren't supported by DirectRTCClient.");
        }
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                DirectRTCClient.this.connectToRoomInternal();
            }
        });
    }

    @Override
    public void disconnectFromRoom() {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                DirectRTCClient.this.disconnectFromRoomInternal();
            }
        });
    }

    public void connectToRoomInternal() {
        int parseInt;
        this.roomState = ConnectionState.NEW;
        Matcher matcher = IP_PATTERN.matcher(this.connectionParameters.roomId);
        if (!matcher.matches()) {
            reportError("roomId must match IP_PATTERN for DirectRTCClient.");
            return;
        }
        String group = matcher.group(1);
        String group2 = matcher.group(matcher.groupCount());
        if (group2 != null) {
            try {
                parseInt = Integer.parseInt(group2);
            } catch (NumberFormatException unused) {
                reportError("Invalid port number: " + group2);
                return;
            }
        } else {
            parseInt = DEFAULT_PORT;
        }
        this.tcpClient = new TCPChannelClient(this.executor, this, group, parseInt);
    }

    public void disconnectFromRoomInternal() {
        this.roomState = ConnectionState.CLOSED;
        if (this.tcpClient != null) {
            this.tcpClient.disconnect();
            this.tcpClient = null;
        }
        this.executor.shutdown();
    }

    @Override
    public void sendOfferSdp(final SessionDescription sessionDescription) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                if (DirectRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    DirectRTCClient.this.reportError("Sending offer SDP in non connected state.");
                    return;
                }
                JSONObject jSONObject = new JSONObject();
                DirectRTCClient.jsonPut(jSONObject, "sdp", sessionDescription.description);
                DirectRTCClient.jsonPut(jSONObject, "type", "offer");
                DirectRTCClient.this.sendMessage(jSONObject.toString());
            }
        });
    }

    @Override
    public void sendAnswerSdp(final SessionDescription sessionDescription) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jSONObject = new JSONObject();
                DirectRTCClient.jsonPut(jSONObject, "sdp", sessionDescription.description);
                DirectRTCClient.jsonPut(jSONObject, "type", "answer");
                DirectRTCClient.this.sendMessage(jSONObject.toString());
            }
        });
    }

    @Override
    public void sendLocalIceCandidate(final IceCandidate iceCandidate) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jSONObject = new JSONObject();
                DirectRTCClient.jsonPut(jSONObject, "type", "candidate");
                DirectRTCClient.jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
                DirectRTCClient.jsonPut(jSONObject, "id", iceCandidate.sdpMid);
                DirectRTCClient.jsonPut(jSONObject, "candidate", iceCandidate.sdp);
                if (DirectRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    DirectRTCClient.this.reportError("Sending ICE candidate in non connected state.");
                } else {
                    DirectRTCClient.this.sendMessage(jSONObject.toString());
                }
            }
        });
    }

    @Override
    public void sendLocalIceCandidateRemovals(final IceCandidate[] iceCandidateArr) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jSONObject = new JSONObject();
                DirectRTCClient.jsonPut(jSONObject, "type", "remove-candidates");
                JSONArray jSONArray = new JSONArray();
                for (IceCandidate iceCandidate : iceCandidateArr) {
                    jSONArray.put(DirectRTCClient.toJsonCandidate(iceCandidate));
                }
                DirectRTCClient.jsonPut(jSONObject, "candidates", jSONArray);
                if (DirectRTCClient.this.roomState != ConnectionState.CONNECTED) {
                    DirectRTCClient.this.reportError("Sending ICE candidate removals in non connected state.");
                } else {
                    DirectRTCClient.this.sendMessage(jSONObject.toString());
                }
            }
        });
    }

    @Override
    public void onTCPConnected(boolean z) {
        if (z) {
            this.roomState = ConnectionState.CONNECTED;
            this.events.onConnectedToRoom(new SignalingParameters(new ArrayList(), z, null, null, null, null, null));
        }
    }

    @Override
    public void onTCPMessage(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String optString = jSONObject.optString("type");
            if (optString.equals("candidate")) {
                this.events.onRemoteIceCandidate(toJavaCandidate(jSONObject));
            } else if (optString.equals("remove-candidates")) {
                JSONArray jSONArray = jSONObject.getJSONArray("candidates");
                IceCandidate[] iceCandidateArr = new IceCandidate[jSONArray.length()];
                for (int i = 0; i < jSONArray.length(); i++) {
                    iceCandidateArr[i] = toJavaCandidate(jSONArray.getJSONObject(i));
                }
                this.events.onRemoteIceCandidatesRemoved(iceCandidateArr);
            } else if (optString.equals("answer")) {
                this.events.onRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString), jSONObject.getString("sdp")));
            } else if (optString.equals("offer")) {
                SignalingParameters signalingParameters = new SignalingParameters(new ArrayList(), false, null, null, null, new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString), jSONObject.getString("sdp")), null);
                this.roomState = ConnectionState.CONNECTED;
                this.events.onConnectedToRoom(signalingParameters);
            } else {
                reportError("Unexpected TCP message: " + str);
            }
        } catch (JSONException e) {
            reportError("TCP message JSON parsing error: " + e.toString());
        }
    }

    @Override
    public void onTCPError(String str) {
        reportError("TCP connection error: " + str);
    }

    @Override
    public void onTCPClose() {
        this.events.onChannelClose();
    }


    public void reportError(final String str) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                if (DirectRTCClient.this.roomState != ConnectionState.ERROR) {
                    DirectRTCClient.this.roomState = ConnectionState.ERROR;
                    DirectRTCClient.this.events.onChannelError(str);
                }
            }
        });
    }

    public void sendMessage(final String str) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                DirectRTCClient.this.tcpClient.send(str);
            }
        });
    }

    public static void jsonPut(JSONObject jSONObject, String str, Object obj) {
        try {
            jSONObject.put(str, obj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject toJsonCandidate(IceCandidate iceCandidate) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
        jsonPut(jSONObject, "id", iceCandidate.sdpMid);
        jsonPut(jSONObject, "candidate", iceCandidate.sdp);
        return jSONObject;
    }

    private static IceCandidate toJavaCandidate(JSONObject jSONObject) throws JSONException {
        return new IceCandidate(jSONObject.getString("id"), jSONObject.getInt("label"), jSONObject.getString("candidate"));
    }
}
