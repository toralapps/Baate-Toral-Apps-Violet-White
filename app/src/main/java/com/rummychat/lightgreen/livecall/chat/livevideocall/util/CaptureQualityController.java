package com.rummychat.lightgreen.livecall.chat.livevideocall.util;

import android.widget.SeekBar;
import android.widget.TextView;

import com.rummychat.lightgreen.livecall.chat.R;

import org.webrtc.CameraEnumerationAndroid;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CaptureQualityController implements SeekBar.OnSeekBarChangeListener {
    private CallFragment.OnCallEvents callEvents;
    private TextView captureFormatText;
    private int framerate;
    private int height;
    private double targetBandwidth;
    private int width;
    private final List<CameraEnumerationAndroid.CaptureFormat> formats = Arrays.asList(new CameraEnumerationAndroid.CaptureFormat(1280, 720, 0, 30000), new CameraEnumerationAndroid.CaptureFormat(960, 540, 0, 30000), new CameraEnumerationAndroid.CaptureFormat(640, 480, 0, 30000), new CameraEnumerationAndroid.CaptureFormat(480, 360, 0, 30000), new CameraEnumerationAndroid.CaptureFormat(320, 240, 0, 30000), new CameraEnumerationAndroid.CaptureFormat(256, 144, 0, 30000));
    private final Comparator<CameraEnumerationAndroid.CaptureFormat> compareFormats = new Comparator<CameraEnumerationAndroid.CaptureFormat>() { // from class: com.app.videocallrandomchat2.CaptureQualityController.1
        public int compare(CameraEnumerationAndroid.CaptureFormat captureFormat, CameraEnumerationAndroid.CaptureFormat captureFormat2) {
            int calculateFramerate = CaptureQualityController.this.calculateFramerate(CaptureQualityController.this.targetBandwidth, captureFormat);
            int calculateFramerate2 = CaptureQualityController.this.calculateFramerate(CaptureQualityController.this.targetBandwidth, captureFormat2);
            return ((calculateFramerate < 15 || calculateFramerate2 < 15) && calculateFramerate != calculateFramerate2) ? calculateFramerate - calculateFramerate2 : (captureFormat.width * captureFormat.height) - (captureFormat2.width * captureFormat2.height);
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public CaptureQualityController(TextView textView, CallFragment.OnCallEvents onCallEvents) {
        this.captureFormatText = textView;
        this.callEvents = onCallEvents;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (i == 0) {
            this.width = 0;
            this.height = 0;
            this.framerate = 0;
            this.captureFormatText.setText(R.string.muted);
            return;
        }
        long j = Long.MIN_VALUE;
        for (CameraEnumerationAndroid.CaptureFormat captureFormat : this.formats) {
            j = Math.max(j, ((long) captureFormat.width) * ((long) captureFormat.height) * ((long) captureFormat.framerate.max));
        }
        double d = (double) i;
        Double.isNaN(d);
        double d2 = (double) j;
        Double.isNaN(d2);
        this.targetBandwidth = ((Math.exp((d / 100.0d) * 3.0d) - 1.0d) / (Math.exp(3.0d) - 1.0d)) * d2;
        CameraEnumerationAndroid.CaptureFormat captureFormat2 = (CameraEnumerationAndroid.CaptureFormat) Collections.max(this.formats, this.compareFormats);
        this.width = captureFormat2.width;
        this.height = captureFormat2.height;
        this.framerate = calculateFramerate(this.targetBandwidth, captureFormat2);
        this.captureFormatText.setText(String.format(this.captureFormatText.getContext().getString(R.string.format_description), Integer.valueOf(this.width), Integer.valueOf(this.height), Integer.valueOf(this.framerate)));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.callEvents.onCaptureFormatChange(this.width, this.height, this.framerate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateFramerate(double d, CameraEnumerationAndroid.CaptureFormat captureFormat) {
        int i = captureFormat.framerate.max;
        double d2 = (double) (captureFormat.width * captureFormat.height);
        Double.isNaN(d2);
        double min = (double) Math.min(i, (int) Math.round(d / d2));
        Double.isNaN(min);
        return (int) Math.round(min / 1000.0d);
    }
}
