package com.playerstudio.redpink.videocall.chat.livevideocall.util;

import android.os.Handler;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;


public class WebSocketChannelClient {
    private static final int CLOSE_TIMEOUT = 1000;
    private static final String TAG = "WSChannelRTCClient";
    private boolean closeEvent;
    private final WebSocketChannelEvents events;
    private final Handler handler;
    private String postServerUrl;
    private WebSocketConnection ws;
    private WebSocketObserver wsObserver;
    private String wsServerUrl;
    private final Object closeEventLock = new Object();
    private String roomID = null;
    private String clientID = null;
    private final LinkedList<String> wsSendQueue = new LinkedList<>();
    private WebSocketConnectionState state = WebSocketConnectionState.NEW;


    public interface WebSocketChannelEvents {
        void onWebSocketClose();

        void onWebSocketError(String str);

        void onWebSocketMessage(String str);
    }


    public enum WebSocketConnectionState {
        NEW,
        CONNECTED,
        REGISTERED,
        CLOSED,
        ERROR
    }

    private void sendWSSMessage(String str, String str2) {
    }

    public WebSocketChannelClient(Handler handler, WebSocketChannelEvents webSocketChannelEvents) {
        this.handler = handler;
        this.events = webSocketChannelEvents;
    }

    public WebSocketConnectionState getState() {
        return this.state;
    }

    public void connect(String str, String str2) {
        if (this.state != WebSocketConnectionState.NEW) {
            return;
        }
        this.wsServerUrl = str;
        this.postServerUrl = str2;
        this.closeEvent = false;
        this.ws = new WebSocketConnection();
        this.wsObserver = new WebSocketObserver();
        try {
            this.ws.connect(new URI(this.wsServerUrl), this.wsObserver);
        } catch (WebSocketException e) {
            reportError("WebSocket connection error: " + e.getMessage());
        } catch (URISyntaxException e2) {
            reportError("URI error: " + e2.getMessage());
        }
    }

    public void register(String str, String str2) {
        this.roomID = str;
        this.clientID = str2;
        if (this.state != WebSocketConnectionState.CONNECTED) {
            return;
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("cmd", "register");
            jSONObject.put("roomid", str);
            jSONObject.put("clientid", str2);
            this.ws.sendTextMessage(jSONObject.toString());
            this.state = WebSocketConnectionState.REGISTERED;
            Iterator<String> it = this.wsSendQueue.iterator();
            while (it.hasNext()) {
                send(it.next());
            }
            this.wsSendQueue.clear();
        } catch (JSONException e) {
            reportError("WebSocket register JSON error: " + e.getMessage());
        }
    }

    public void send(String str) {
        switch (this.state) {
            case NEW:
            case CONNECTED:
              //  Log.d(TAG, "WS ACC: " + str);
                this.wsSendQueue.add(str);
                return;
            case ERROR:
            case CLOSED:
               // Log.e(TAG, "WebSocket send() in error or closed state : " + str);
                return;
            case REGISTERED:
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("cmd", "send");
                    jSONObject.put(NotificationCompat.CATEGORY_MESSAGE, str);
                    this.ws.sendTextMessage(jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    reportError("WebSocket send JSON error: " + e.getMessage());
                    return;
                }
            default:
                return;
        }
    }

    public void post(String str) {
        sendWSSMessage("POST", str);
    }

    public void disconnect(boolean z) {
       // Log.d(TAG, "Disconnect WebSocket. State: " + this.state);
        if (this.state == WebSocketConnectionState.REGISTERED) {
            send("{\"type\": \"bye\"}");
            this.state = WebSocketConnectionState.CONNECTED;
        }
        if (this.state == WebSocketConnectionState.CONNECTED || this.state == WebSocketConnectionState.ERROR) {
            this.ws.disconnect();
            this.state = WebSocketConnectionState.CLOSED;
            if (z) {
                synchronized (this.closeEventLock) {
                    while (!this.closeEvent) {
                        try {
                            this.closeEventLock.wait(1000);
                            break;
                        } catch (InterruptedException e) {
                       //
                        }
                    }
                }
            }
        }
      //  Log.d(TAG, "Disconnecting WebSocket done.");
    }

    private void reportError(final String str) {
        this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketChannelClient.1
            @Override // java.lang.Runnable
            public void run() {
                if (WebSocketChannelClient.this.state != WebSocketConnectionState.ERROR) {
                    WebSocketChannelClient.this.state = WebSocketConnectionState.ERROR;
                    WebSocketChannelClient.this.events.onWebSocketError(str);
                }
            }
        });
    }


    private class WebSocketObserver implements WebSocket.WebSocketConnectionObserver {
        @Override // de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
        public void onBinaryMessage(byte[] bArr) {
        }

        @Override // de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
        public void onRawTextMessage(byte[] bArr) {
        }

        private WebSocketObserver() {
        }

        @Override // de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
        public void onOpen() {
          //  Log.d(WebSocketChannelClient.TAG, "WebSocket connection opened to: " + WebSocketChannelClient.this.wsServerUrl);
            WebSocketChannelClient.this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketChannelClient.WebSocketObserver.1
                @Override // java.lang.Runnable
                public void run() {
                    WebSocketChannelClient.this.state = WebSocketConnectionState.CONNECTED;
                    if (WebSocketChannelClient.this.roomID != null && WebSocketChannelClient.this.clientID != null) {
                        WebSocketChannelClient.this.register(WebSocketChannelClient.this.roomID, WebSocketChannelClient.this.clientID);
                    }
                }
            });
        }

        @Override // de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
        public void onClose(WebSocketCloseNotification webSocketCloseNotification, String str) {
           // Log.d(WebSocketChannelClient.TAG, "WebSocket connection closed. Code: " + webSocketCloseNotification + ". Reason: " + str + ". State: " + WebSocketChannelClient.this.state);
            synchronized (WebSocketChannelClient.this.closeEventLock) {
                WebSocketChannelClient.this.closeEvent = true;
                WebSocketChannelClient.this.closeEventLock.notify();
            }
            WebSocketChannelClient.this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketChannelClient.WebSocketObserver.2
                @Override // java.lang.Runnable
                public void run() {
                    if (WebSocketChannelClient.this.state != WebSocketConnectionState.CLOSED) {
                        WebSocketChannelClient.this.state = WebSocketConnectionState.CLOSED;
                        WebSocketChannelClient.this.events.onWebSocketClose();
                    }
                }
            });
        }

        @Override // de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
        public void onTextMessage(final String str) {
            WebSocketChannelClient.this.handler.post(new Runnable() { // from class: com.app.videocallrandomchat2.WebSocketChannelClient.WebSocketObserver.3
                @Override // java.lang.Runnable
                public void run() {
                    if (WebSocketChannelClient.this.state == WebSocketConnectionState.CONNECTED || WebSocketChannelClient.this.state == WebSocketConnectionState.REGISTERED) {
                        WebSocketChannelClient.this.events.onWebSocketMessage(str);
                    }
                }
            });
        }
    }
}
