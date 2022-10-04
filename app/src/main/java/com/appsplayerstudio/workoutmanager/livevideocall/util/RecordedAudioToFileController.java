package com.appsplayerstudio.workoutmanager.livevideocall.util;

import android.os.Environment;
import android.util.Log;

import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;


public class RecordedAudioToFileController implements JavaAudioDeviceModule.SamplesReadyCallback, WebRtcAudioRecord.WebRtcAudioRecordSamplesReadyCallback {
    private static final long MAX_FILE_SIZE_IN_BYTES = 58348800;
    private static final String TAG = "RecordedAudioToFile";
    private final ExecutorService executor;
    private long fileSizeInBytes;
    private boolean isRunning;
    private final Object lock = new Object();
    @Nullable
    private OutputStream rawAudioFileOutputStream;

    public RecordedAudioToFileController(ExecutorService executorService) {
        Log.d(TAG, "ctor");
        this.executor = executorService;
    }

    public boolean start() {
       // Log.d(TAG, "start");
        if (!isExternalStorageWritable()) {
          //  Log.e(TAG, "Writing to external media is not possible");
            return false;
        }
        synchronized (this.lock) {
            this.isRunning = true;
        }
        return true;
    }

    public void stop() {
      //  Log.d(TAG, "stop");
        synchronized (this.lock) {
            this.isRunning = false;
            if (this.rawAudioFileOutputStream != null) {
                try {
                    this.rawAudioFileOutputStream.close();
                } catch (IOException e) {
                 //   Log.e(TAG, "Failed to close file with saved input audio: " + e);
                }
                this.rawAudioFileOutputStream = null;
            }
            this.fileSizeInBytes = 0;
        }
    }

    private boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    private void openRawAudioOutputFile(int i, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append(File.separator);
        sb.append("recorded_audio_16bits_");
        sb.append(String.valueOf(i));
        sb.append("Hz");
        sb.append(i2 == 1 ? "_mono" : "_stereo");
        sb.append(".pcm");
        String sb2 = sb.toString();
        try {
            this.rawAudioFileOutputStream = new FileOutputStream(new File(sb2));
        } catch (FileNotFoundException e) {
          //  Log.e(TAG, "Failed to open audio output file: " + e.getMessage());
        }
       // Log.d(TAG, "Opened file for recording: " + sb2);
    }

    @Override // org.webrtc.voiceengine.WebRtcAudioRecord.WebRtcAudioRecordSamplesReadyCallback
    public void onWebRtcAudioRecordSamplesReady(WebRtcAudioRecord.AudioSamples audioSamples) {
        onWebRtcAudioRecordSamplesReady(new JavaAudioDeviceModule.AudioSamples(audioSamples.getAudioFormat(), audioSamples.getChannelCount(), audioSamples.getSampleRate(), audioSamples.getData()));
    }

    @Override // org.webrtc.audio.JavaAudioDeviceModule.SamplesReadyCallback
    public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples audioSamples) {
        if (audioSamples.getAudioFormat() != 2) {
          //  Log.e(TAG, "Invalid audio format");
            return;
        }
        synchronized (this.lock) {
            if (this.isRunning) {
                if (this.rawAudioFileOutputStream == null) {
                    openRawAudioOutputFile(audioSamples.getSampleRate(), audioSamples.getChannelCount());
                    this.fileSizeInBytes = 0;
                }
                this.executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$RecordedAudioToFileController$UQUb-vFSNXORNK-YwUVrGnYhSiQ
                    private final JavaAudioDeviceModule.AudioSamples f$1;

                    {
                        this.f$1 = audioSamples;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        RecordedAudioToFileController.lambda$onWebRtcAudioRecordSamplesReady$0(RecordedAudioToFileController.this, this.f$1);
                    }
                });
            }
        }
    }

    public static void lambda$onWebRtcAudioRecordSamplesReady$0(RecordedAudioToFileController recordedAudioToFileController, JavaAudioDeviceModule.AudioSamples audioSamples) {
        if (recordedAudioToFileController.rawAudioFileOutputStream != null) {
            try {
                if (recordedAudioToFileController.fileSizeInBytes < MAX_FILE_SIZE_IN_BYTES) {
                    recordedAudioToFileController.rawAudioFileOutputStream.write(audioSamples.getData());
                    recordedAudioToFileController.fileSizeInBytes += (long) audioSamples.getData().length;
                }
            } catch (IOException e) {
               // Log.e(TAG, "Failed to write audio to file: " + e.getMessage());
            }
        }
    }
}
