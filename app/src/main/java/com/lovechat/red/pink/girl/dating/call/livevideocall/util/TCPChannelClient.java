package com.lovechat.red.pink.girl.dating.call.livevideocall.util;

import android.util.Log;

import org.webrtc.ThreadUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;


public class TCPChannelClient {
    private static final String TAG = "TCPChannelClient";
    private final TCPChannelEvents eventListener;
    private final ExecutorService executor;
    private final ThreadUtils.ThreadChecker executorThreadCheck = new ThreadUtils.ThreadChecker();
    private TCPSocket socket;


    public interface TCPChannelEvents {
        void onTCPClose();

        void onTCPConnected(boolean z);

        void onTCPError(String str);

        void onTCPMessage(String str);
    }

    public TCPChannelClient(ExecutorService executorService, TCPChannelEvents tCPChannelEvents, String str, int i) {
        this.executor = executorService;
        this.executorThreadCheck.detachThread();
        this.eventListener = tCPChannelEvents;
        try {
            InetAddress byName = InetAddress.getByName(str);
            if (byName.isAnyLocalAddress()) {
                this.socket = new TCPSocketServer(byName, i);
            } else {
                this.socket = new TCPSocketClient(byName, i);
            }
            this.socket.start();
        } catch (UnknownHostException unused) {
            reportError("Invalid IP address.");
        }
    }

    public void disconnect() {
        this.executorThreadCheck.checkIsOnValidThread();
        this.socket.disconnect();
    }

    public void send(String str) {
        this.executorThreadCheck.checkIsOnValidThread();
        this.socket.send(str);
    }

    public void reportError(final String str) {
       // Log.e(TAG, "TCP Error: " + str);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                TCPChannelClient.this.eventListener.onTCPError(str);
            }
        });
    }


    private abstract class TCPSocket extends Thread {
        @Nullable
        private PrintWriter out;
        @Nullable
        private Socket rawSocket;
        protected final Object rawSocketLock = new Object();

        @Nullable
        public abstract Socket connect();

        public abstract boolean isServer();

        TCPSocket() {
        }

        @Override
        public void run() {
          //  Log.d(TCPChannelClient.TAG, "Listening thread started...");
            Socket connect = connect();
           // Log.d(TCPChannelClient.TAG, "TCP connection established.");
            synchronized (this.rawSocketLock) {
                if (this.rawSocket != null) {
                  //  Log.e(TCPChannelClient.TAG, "Socket already existed and will be replaced.");
                }
                this.rawSocket = connect;
                if (this.rawSocket != null) {
                    try {
                        this.out = new PrintWriter((Writer) new OutputStreamWriter(this.rawSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.rawSocket.getInputStream(), StandardCharsets.UTF_8));
                        Log.v(TCPChannelClient.TAG, "Execute onTCPConnected");
                        TCPChannelClient.this.executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.TCPChannelClient.TCPSocket.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Log.v(TCPChannelClient.TAG, "Run onTCPConnected");
                                TCPChannelClient.this.eventListener.onTCPConnected(TCPSocket.this.isServer());
                            }
                        });
                        while (true) {
                            try {
                                final String readLine = bufferedReader.readLine();
                                if (readLine == null) {
                                    break;
                                }
                                TCPChannelClient.this.executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.TCPChannelClient.TCPSocket.2
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        Log.v(TCPChannelClient.TAG, "Receive: " + readLine);
                                        TCPChannelClient.this.eventListener.onTCPMessage(readLine);
                                    }
                                });
                            } catch (IOException e) {
                                synchronized (this.rawSocketLock) {
                                    if (this.rawSocket != null) {
                                        TCPChannelClient tCPChannelClient = TCPChannelClient.this;
                                        tCPChannelClient.reportError("Failed to read from rawSocket: " + e.getMessage());
                                    }
                                }
                            }
                        }
                      //  Log.d(TCPChannelClient.TAG, "Receiving thread exiting...");
                        disconnect();
                    } catch (IOException e2) {
                        TCPChannelClient tCPChannelClient2 = TCPChannelClient.this;
                        tCPChannelClient2.reportError("Failed to open IO on rawSocket: " + e2.getMessage());
                    }
                }
            }
        }

        public void disconnect() {
            try {
                synchronized (this.rawSocketLock) {
                    if (this.rawSocket != null) {
                        this.rawSocket.close();
                        this.rawSocket = null;
                        this.out = null;
                        TCPChannelClient.this.executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.TCPChannelClient.TCPSocket.3
                            @Override // java.lang.Runnable
                            public void run() {
                                TCPChannelClient.this.eventListener.onTCPClose();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                TCPChannelClient tCPChannelClient = TCPChannelClient.this;
                tCPChannelClient.reportError("Failed to close rawSocket: " + e.getMessage());
            }
        }

        public void send(String str) {
            Log.v(TCPChannelClient.TAG, "Send: " + str);
            synchronized (this.rawSocketLock) {
                if (this.out == null) {
                    TCPChannelClient.this.reportError("Sending data on closed socket.");
                    return;
                }
                PrintWriter printWriter = this.out;
                printWriter.write(str + "\n");
                this.out.flush();
            }
        }
    }


    private class TCPSocketServer extends TCPSocket {
        private final InetAddress address;
        private final int port;
        @Nullable
        private ServerSocket serverSocket;

        @Override
        public boolean isServer() {
            return true;
        }

        public TCPSocketServer(InetAddress inetAddress, int i) {
            super();
            this.address = inetAddress;
            this.port = i;
        }

        @Override
        @Nullable
        public Socket connect() {
         //   Log.d(TCPChannelClient.TAG, "Listening on [" + this.address.getHostAddress() + "]:" + Integer.toString(this.port));
            try {
                ServerSocket serverSocket = new ServerSocket(this.port, 0, this.address);
                synchronized (this.rawSocketLock) {
                    if (this.serverSocket != null) {
                      //  Log.e(TCPChannelClient.TAG, "Server rawSocket was already listening and new will be opened.");
                    }
                    this.serverSocket = serverSocket;
                }
                try {
                    return serverSocket.accept();
                } catch (IOException e) {
                    TCPChannelClient tCPChannelClient = TCPChannelClient.this;
                    tCPChannelClient.reportError("Failed to receive connection: " + e.getMessage());
                    return null;
                }
            } catch (IOException e2) {
                TCPChannelClient tCPChannelClient2 = TCPChannelClient.this;
                tCPChannelClient2.reportError("Failed to create server socket: " + e2.getMessage());
                return null;
            }
        }

        @Override
        public void disconnect() {
            try {
                synchronized (this.rawSocketLock) {
                    if (this.serverSocket != null) {
                        this.serverSocket.close();
                        this.serverSocket = null;
                    }
                }
            } catch (IOException e) {
                TCPChannelClient tCPChannelClient = TCPChannelClient.this;
                tCPChannelClient.reportError("Failed to close server socket: " + e.getMessage());
            }
            super.disconnect();
        }
    }


    private class TCPSocketClient extends TCPSocket {
        private final InetAddress address;
        private final int port;

        @Override
        public boolean isServer() {
            return false;
        }

        public TCPSocketClient(InetAddress inetAddress, int i) {
            super();
            this.address = inetAddress;
            this.port = i;
        }

        @Override
        @Nullable
        public Socket connect() {
          //  Log.d(TCPChannelClient.TAG, "Connecting to [" + this.address.getHostAddress() + "]:" + Integer.toString(this.port));
            try {
                return new Socket(this.address, this.port);
            } catch (IOException e) {
                TCPChannelClient tCPChannelClient = TCPChannelClient.this;
                tCPChannelClient.reportError("Failed to connect: " + e.getMessage());
                return null;
            }
        }
    }
}
