package com.nimychat.skybluewhite.videocall.chat.livevideocall.util;

import android.os.ParcelFileDescriptor;

import org.webrtc.PeerConnection;

import java.io.File;
import java.io.IOException;


public class RtcEventLog {
    private static final int OUTPUT_FILE_MAX_BYTES = 10000000;
    private static final String TAG = "RtcEventLog";
    private final PeerConnection peerConnection;
    private RtcEventLogState state = RtcEventLogState.INACTIVE;


    enum RtcEventLogState {
        INACTIVE,
        STARTED,
        STOPPED
    }

    public RtcEventLog(PeerConnection peerConnection) {
        if (peerConnection != null) {
            this.peerConnection = peerConnection;
            return;
        }
        throw new NullPointerException("The peer connection is null.");
    }

    public void start(File file) {
        if (this.state == RtcEventLogState.STARTED) {
          //  Log.e(TAG, "RtcEventLog has already started.");
            return;
        }
        try {
            if (!this.peerConnection.startRtcEventLog(ParcelFileDescriptor.open(file, 1006632960).detachFd(), OUTPUT_FILE_MAX_BYTES)) {
              //  Log.e(TAG, "Failed to start RTC event log.");
                return;
            }
            this.state = RtcEventLogState.STARTED;
           // Log.d(TAG, "RtcEventLog started.");
        } catch (IOException e) {
          //  Log.e(TAG, "Failed to create a new file", e);
        }
    }

    public void stop() {
        if (this.state != RtcEventLogState.STARTED) {
           // Log.e(TAG, "RtcEventLog was not started.");
            return;
        }
        this.peerConnection.stopRtcEventLog();
        this.state = RtcEventLogState.STOPPED;
      //  Log.d(TAG, "RtcEventLog stopped.");
    }
}
