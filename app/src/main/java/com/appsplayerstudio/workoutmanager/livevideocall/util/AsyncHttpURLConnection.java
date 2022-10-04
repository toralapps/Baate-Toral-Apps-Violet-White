package com.appsplayerstudio.workoutmanager.livevideocall.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

import de.tavendo.autobahn.WebSocket;


public class AsyncHttpURLConnection {
    private static final String HTTP_ORIGIN = "https://appr.tc";
    private static final int HTTP_TIMEOUT_MS = 8000;
    private String contentType;
    private final AsyncHttpEvents events;
    private final String message;
    private final String method;
    private final String url;


    public interface AsyncHttpEvents {
        void onHttpComplete(String str);

        void onHttpError(String str);
    }

    public AsyncHttpURLConnection(String str, String str2, String str3, AsyncHttpEvents asyncHttpEvents) {
        this.method = str;
        this.url = str2;
        this.message = str3;
        this.events = asyncHttpEvents;
    }

    public void setContentType(String str) {
        this.contentType = str;
    }

    public void send() {
        new Thread(new Runnable() { // from class: com.app.videocallrandomchat2.util.-$$Lambda$AsyncHttpURLConnection$18u0r0MSWXStuxCXwyYUiXvyQIw
            @Override // java.lang.Runnable
            public final void run() {
                AsyncHttpURLConnection.this.sendHttpMessage();
            }
        }).start();
    }

    public void sendHttpMessage() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.url).openConnection();
            boolean z = false;
            byte[] bArr = new byte[0];
            if (this.message != null) {
                bArr = this.message.getBytes(WebSocket.UTF8_ENCODING);
            }
            httpURLConnection.setRequestMethod(this.method);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(HTTP_TIMEOUT_MS);
            httpURLConnection.setReadTimeout(HTTP_TIMEOUT_MS);
            httpURLConnection.addRequestProperty("origin", HTTP_ORIGIN);
            if (this.method.equals("POST")) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setFixedLengthStreamingMode(bArr.length);
                z = true;
            }
            if (this.contentType == null) {
                httpURLConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            } else {
                httpURLConnection.setRequestProperty("Content-Type", this.contentType);
            }
            if (z && bArr.length > 0) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(bArr);
                outputStream.close();
            }
            if (httpURLConnection.getResponseCode() != 200) {
                AsyncHttpEvents asyncHttpEvents = this.events;
                asyncHttpEvents.onHttpError("Non-200 response to " + this.method + " to URL: " + this.url + " : " + httpURLConnection.getHeaderField((String) null));
                httpURLConnection.disconnect();
                return;
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            String drainStream = drainStream(inputStream);
            inputStream.close();
            httpURLConnection.disconnect();
            this.events.onHttpComplete(drainStream);
        } catch (SocketTimeoutException unused) {
            AsyncHttpEvents asyncHttpEvents2 = this.events;
            asyncHttpEvents2.onHttpError("HTTP " + this.method + " to " + this.url + " timeout");
        } catch (IOException e) {
            AsyncHttpEvents asyncHttpEvents3 = this.events;
            asyncHttpEvents3.onHttpError("HTTP " + this.method + " to " + this.url + " error: " + e.getMessage());
        }
    }

    private static String drainStream(InputStream inputStream) {
        Scanner useDelimiter = new Scanner(inputStream, WebSocket.UTF8_ENCODING).useDelimiter("\\A");
        return useDelimiter.hasNext() ? useDelimiter.next() : "";
    }
}
