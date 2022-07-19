package com.lovechat.red.pink.girl.dating.call.livevideocall.activity;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.lovechat.red.pink.girl.dating.call.R;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.AppRTCAudioManager;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.AppRTCClient;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.CallFragment;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.Config_Var;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.CpuMonitor;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.DirectRTCClient;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.HudFragment;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.PeerConnectionClient;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.UnhandledExceptionHandler;
import com.lovechat.red.pink.girl.dating.call.livevideocall.util.WebSocketRTCClient;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("ALL")

public class CallLiveActivity extends AppCompatActivity implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, CallFragment.OnCallEvents {
    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_ENABLE_RTCEVENTLOG = "org.appspot.apprtc.ENABLE_RTCEVENTLOG";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_ID = "org.appspot.apprtc.ID";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED = "org.appspot.apprtc.SAVE_INPUT_AUDIO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS";
    public static final String EXTRA_USE_LEGACY_AUDIO_DEVICE = "org.appspot.apprtc.USE_LEGACY_AUDIO_DEVICE";
    public static final String EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};
    private static final int STAT_CALLBACK_PERIOD = 1000;
    private static final String TAG = "CallRTCClient";
    private static int mediaProjectionPermissionResultCode;
    private static Intent mediaProjectionPermissionResultData;
    private boolean activityRunning;
    @Nullable
    private AppRTCClient appRtcClient;
    @Nullable
    private AppRTCAudioManager audioManager;
    private CallFragment callFragment;
    private long callStartedTimeMs;
    private boolean commandLineRun;
    private CpuMonitor cpuMonitor;
    @Nullable
    private SurfaceViewRenderer fullscreenRenderer;
    private HudFragment hudFragment;
    private boolean iceConnected;
    private boolean isError;
    private boolean isSwappedFeeds;
    private Toast logToast;
    @Nullable
    private PeerConnectionClient peerConnectionClient;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    @Nullable
    private SurfaceViewRenderer pipRenderer;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private boolean screencaptureEnabled;
    @Nullable
    private AppRTCClient.SignalingParameters signalingParameters;
    @Nullable
    private VideoFileRenderer videoFileRenderer;
    private VideoView videoView;
    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private final List<VideoSink> remoteSinks = new ArrayList();
    private boolean callControlFragmentVisible = true;
    private boolean micEnabled = true;

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onIceDisconnected() {
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onPeerConnectionClosed() {
    }

    /* JADX INFO: Access modifiers changed from: private */

    public static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        private ProxyVideoSink() {
        }

        @Override // org.webrtc.VideoSink
        public synchronized void onFrame(VideoFrame videoFrame) {
            if (this.target == null) {
                Logging.d(CallLiveActivity.TAG, "Dropping frame in proxy because target is null.");
            } else {
                this.target.onFrame(videoFrame);
            }
        }

        public synchronized void setTarget(VideoSink videoSink) {
            this.target = videoSink;
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
//        int i = 0;
//        int i2 = 0;
        super.onCreate(bundle);
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_call_live);




        this.iceConnected = false;
        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        this.signalingParameters = null;
        this.videoView = (VideoView) findViewById(R.id.videoView);
        this.callFragment = new CallFragment();
        this.hudFragment = new HudFragment();
        this.pipRenderer = (SurfaceViewRenderer) findViewById(R.id.pip_video_view);
        this.fullscreenRenderer = (SurfaceViewRenderer) findViewById(R.id.fullscreen_video_view);
        if (!Config_Var.video_call) {
            this.videoView.setVideoURI(Uri.parse(Config_Var.mediaurl + Config_Var.media_number + ".mp4"));
            Log.d("DEEP",Uri.parse(Config_Var.mediaurl + Config_Var.media_number + ".mp4").toString());
            this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.app.videocallrandomchat2.CallActivity.1
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(false);
                    CallLiveActivity.this.callFragment.updateEncoderStatistics("Connected");
                    CallLiveActivity.this.setSwappedFeeds(false);
                    CallLiveActivity.this.videoView.start();
                }
            });
            this.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.app.videocallrandomchat2.CallActivity.2
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i3, int i4) {
                    return true;
                }
            });
            this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.app.videocallrandomchat2.CallActivity.3
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    CallLiveActivity.this.videoView.stopPlayback();
                    CallLiveActivity.this.finish();
                }
            });
        }
        // android.view.View.OnClickListener
        View.OnClickListener r4 = new View.OnClickListener() { // from class: com.app.videocallrandomchat2.CallActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CallLiveActivity.this.toggleCallControlFragmentVisibility();
            }
        };
        this.pipRenderer.setOnClickListener(new View.OnClickListener() { // from class: com.app.videocallrandomchat2.CallActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CallLiveActivity.this.setSwappedFeeds(!CallLiveActivity.this.isSwappedFeeds);
            }
        });
        this.fullscreenRenderer.setOnClickListener(r4);
        this.remoteSinks.add(this.remoteProxyRenderer);
        Intent intent = getIntent();
        EglBase eglBase = EglBase.create();
        this.pipRenderer.init(eglBase.getEglBaseContext(), null);
        this.pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        String stringExtra = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
        if (stringExtra != null) {
            try {
                this.videoFileRenderer = new VideoFileRenderer(stringExtra, intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0), intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0), eglBase.getEglBaseContext());
                this.remoteSinks.add(this.videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException("Failed to open video file for output: " + stringExtra, e);
            }
        }
        this.fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
        this.fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.pipRenderer.setZOrderMediaOverlay(true);
        this.pipRenderer.setEnableHardwareScaler(true);
        this.fullscreenRenderer.setEnableHardwareScaler(false);
        setSwappedFeeds(true);
        String[] strArr = MANDATORY_PERMISSIONS;
        for (String str : strArr) {
            if (checkCallingOrSelfPermission(str) != 0) {
                logAndToast("Permission " + str + " is not granted");
                setResult(0);
                finish();
                return;
            }
        }
        Uri data = intent.getData();
        if (data == null) {
            logAndToast(getString(R.string.missing_url));
          //  Log.e(TAG, "Didn't get any URL in intent!");
            setResult(0);
            finish();
            return;
        }
        String stringExtra2 = intent.getStringExtra(EXTRA_ROOMID);
      //  Log.d(TAG, "Room ID: " + stringExtra2);
        if (stringExtra2 == null || stringExtra2.length() == 0) {
            logAndToast(getString(R.string.missing_url));
           // Log.e(TAG, "Incorrect room ID in intent!");
            setResult(0);
            finish();
            return;
        }
        boolean booleanExtra = intent.getBooleanExtra(EXTRA_LOOPBACK, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);


        boolean booleanExtra2 = intent.getBooleanExtra(EXTRA_TRACING, false);
        int intExtra = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int intExtra2 = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);
        this.screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);


        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && intExtra == 0 && intExtra2 == 0) {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }

//        if (this.screencaptureEnabled && intExtra == 0 && intExtra2 == 0) {
//            DisplayMetrics displayMetrics = getDisplayMetrics();
//            int i3 = displayMetrics.widthPixels;
//            i = displayMetrics.heightPixels;
//            i2 = i3;
//        } else {
//            i2 = intExtra;
//            i = intExtra2;
//        }
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true), intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1), intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL), intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }



        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);

        this.peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), booleanExtra, booleanExtra2, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0), intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC), intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true), intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false), intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC), intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false), intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false), intent.getBooleanExtra(EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, false), intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false), intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false), intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false), intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false), intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), intent.getBooleanExtra(EXTRA_ENABLE_RTCEVENTLOG, false), intent.getBooleanExtra(EXTRA_USE_LEGACY_AUDIO_DEVICE, false), dataChannelParameters);
        this.commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        int intExtra3 = intent.getIntExtra(EXTRA_RUNTIME, 0);
       // Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");
        if (booleanExtra || !DirectRTCClient.IP_PATTERN.matcher(stringExtra2).matches()) {
            this.appRtcClient = new WebSocketRTCClient(this);
        } else {
           // Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            this.appRtcClient = new DirectRTCClient(this);
        }
        String stringExtra3 = intent.getStringExtra(EXTRA_URLPARAMETERS);
       // Log.d(TAG, "Room ID--urlParameters: " + stringExtra3);
        this.roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(data.toString(), stringExtra2, booleanExtra, stringExtra3);
        if (CpuMonitor.isSupported()) {
            this.cpuMonitor = new CpuMonitor(this);
            this.hudFragment.setCpuMonitor(this.cpuMonitor);
        }
        this.callFragment.setArguments(intent.getExtras());
        this.hudFragment.setArguments(intent.getExtras());
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.add(R.id.call_fragment_container, this.callFragment);
        beginTransaction.add(R.id.hud_fragment_container, this.hudFragment);
        beginTransaction.commit();

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && intExtra3 > 0) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect();
                }
            }, intExtra3);
        }



        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(), eglBase, peerConnectionParameters, CallLiveActivity.this);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        if (booleanExtra) {
            options.networkIgnoreMask = 0;
        }
        peerConnectionClient.createPeerConnectionFactory(options);

        if (this.screencaptureEnabled) {
            startScreenCapture();
        } else {
            startCall();
        }
    }

    @Override
    public void onBackPressed() {
        disconnect();
    }

    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getApplication().getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        return Build.VERSION.SDK_INT >= 19 ? 4102 : 6;
    }

    @TargetApi(21)
    private void startScreenCapture() {
        startActivityForResult(((MediaProjectionManager) getApplication().getSystemService("media_projection")).createScreenCaptureIntent(), 1);
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            mediaProjectionPermissionResultCode = i2;
            mediaProjectionPermissionResultData = intent;
            startCall();
        }
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    @Nullable
    private VideoCapturer createCameraCapturer(CameraEnumerator cameraEnumerator) {
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String str : deviceNames) {
            if (cameraEnumerator.isFrontFacing(str)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                CameraVideoCapturer createCapturer = cameraEnumerator.createCapturer(str, null);
                if (createCapturer != null) {
                    return createCapturer;
                }
            }
        }
        Logging.d(TAG, "Looking for other cameras.");
        for (String str2 : deviceNames) {
            if (!cameraEnumerator.isFrontFacing(str2)) {
                Logging.d(TAG, "Creating other camera capturer.");
                CameraVideoCapturer createCapturer2 = cameraEnumerator.createCapturer(str2, null);
                if (createCapturer2 != null) {
                    return createCapturer2;
                }
            }
        }
        return null;
    }

    @Nullable
    @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode == -1) {
            return new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new MediaProjection.Callback() { // from class: com.app.videocallrandomchat2.CallActivity.7
                @Override // android.media.projection.MediaProjection.Callback
                public void onStop() {
                    CallLiveActivity.this.reportError("User revoked permission to capture the screen.");
                }
            });
        }
        reportError("User didn't give permission to capture the screen.");
        return null;
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        this.activityRunning = false;
        if (this.peerConnectionClient != null && !this.screencaptureEnabled) {
            this.peerConnectionClient.stopVideoSource();
        }
        if (this.cpuMonitor != null) {
            this.cpuMonitor.pause();
        }
        if (!Config_Var.video_call) {
            finish();
        }
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        this.activityRunning = true;
        if (this.peerConnectionClient != null && !this.screencaptureEnabled) {
            this.peerConnectionClient.startVideoSource();
        }
        if (this.cpuMonitor != null) {
            this.cpuMonitor.resume();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();
        if (this.logToast != null) {
            this.logToast.cancel();
        }
        this.activityRunning = false;
        super.onDestroy();
    }

    @Override // com.app.videocallrandomchat2.CallFragment.OnCallEvents
    public void onCallHangUp() {
        disconnect();
    }

    @Override // com.app.videocallrandomchat2.CallFragment.OnCallEvents
    public void onCameraSwitch() {
        if (this.peerConnectionClient != null) {
            this.peerConnectionClient.switchCamera();
        }
    }

    @Override // com.app.videocallrandomchat2.CallFragment.OnCallEvents
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        this.fullscreenRenderer.setScalingType(scalingType);
    }

    @Override // com.app.videocallrandomchat2.CallFragment.OnCallEvents
    public void onCaptureFormatChange(int i, int i2, int i3) {
        if (this.peerConnectionClient != null) {
            this.peerConnectionClient.changeCaptureFormat(i, i2, i3);
        }
    }

    @Override // com.app.videocallrandomchat2.CallFragment.OnCallEvents
    public boolean onToggleMic() {
        if (this.peerConnectionClient != null) {
            this.micEnabled = !this.micEnabled;
            this.peerConnectionClient.setAudioEnabled(this.micEnabled);
        }
        return this.micEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleCallControlFragmentVisibility() {
        if (this.iceConnected && this.callFragment.isAdded()) {
            this.callControlFragmentVisible = !this.callControlFragmentVisible;
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            if (this.callControlFragmentVisible) {
                beginTransaction.show(this.callFragment);
                beginTransaction.show(this.hudFragment);
            } else {
                beginTransaction.hide(this.callFragment);
                beginTransaction.hide(this.hudFragment);
            }
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            beginTransaction.commit();
        }
    }

    private void startCall() {
        if (this.appRtcClient == null) {
         //   Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        this.callStartedTimeMs = System.currentTimeMillis();
        logAndToast(getString(R.string.connecting_to, new Object[]{this.roomConnectionParameters.roomUrl}));
        this.appRtcClient.connectToRoom(this.roomConnectionParameters);
        this.audioManager = AppRTCAudioManager.create(getApplicationContext());
       // Log.d(TAG, "Starting the audio manager...");
        this.audioManager.start(new AppRTCAudioManager.AudioManagerEvents() { // from class: com.app.videocallrandomchat2.CallActivity.8
            @Override // com.app.videocallrandomchat2.AppRTCAudioManager.AudioManagerEvents
            public void onAudioDeviceChanged(AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> set) {
                CallLiveActivity.this.onAudioManagerDevicesChanged(audioDevice, set);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callConnected() {
        Log.i(TAG, "Call connected: delay=" + (System.currentTimeMillis() - this.callStartedTimeMs) + "ms");
        if (this.peerConnectionClient == null || this.isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        this.peerConnectionClient.enableStatsEvents(true, 1000);
        setSwappedFeeds(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAudioManagerDevicesChanged(AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> set) {
       // Log.d(TAG, "onAudioManagerDevicesChanged: " + set + ", selected: " + audioDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnect() {
        this.activityRunning = false;
        this.remoteProxyRenderer.setTarget(null);
        this.localProxyVideoSink.setTarget(null);
        if (this.appRtcClient != null) {
            this.appRtcClient.disconnectFromRoom();
            this.appRtcClient = null;
        }
        if (this.pipRenderer != null) {
            this.pipRenderer.release();
            this.pipRenderer = null;
        }
        if (this.videoFileRenderer != null) {
            this.videoFileRenderer.release();
            this.videoFileRenderer = null;
        }
        if (this.fullscreenRenderer != null) {
            this.fullscreenRenderer.release();
            this.fullscreenRenderer = null;
        }
        if (this.peerConnectionClient != null) {
            this.peerConnectionClient.close();
            this.peerConnectionClient = null;
        }
        if (this.audioManager != null) {
            this.audioManager.stop();
            this.audioManager = null;
        }
        if (!this.iceConnected || this.isError) {
            setResult(0);
        } else {
            setResult(-1);
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectWithErrorMessage(String str) {
        if (this.commandLineRun || !this.activityRunning) {
           // Log.e(TAG, "Critical error: " + str);
            disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logAndToast(String str) {
      //  Log.d(TAG, str);
        if (this.logToast != null) {
            this.logToast.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportError(final String str) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.9
            @Override // java.lang.Runnable
            public void run() {
                if (!CallLiveActivity.this.isError) {
                    CallLiveActivity.this.isError = true;
                    CallLiveActivity.this.disconnectWithErrorMessage(str);
                }
            }
        });
    }

    @Nullable
    private VideoCapturer createVideoCapturer() {
        VideoCapturer fileVideoCapturer;
        String stringExtra = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (stringExtra != null) {
            try {
                fileVideoCapturer = new FileVideoCapturer(stringExtra);
            } catch (IOException unused) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (this.screencaptureEnabled) {
            return createScreenCapturer();
        } else {
            if (!useCamera2()) {
                Logging.d(TAG, "Creating capturer using camera1 API.");
                fileVideoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
            } else if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            } else {
                Logging.d(TAG, "Creating capturer using camera2 API.");
                fileVideoCapturer = createCameraCapturer(new Camera2Enumerator(this));
            }
        }
        if (fileVideoCapturer != null) {
            return fileVideoCapturer;
        }
        reportError("Failed to open camera");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSwappedFeeds(boolean z) {
        Logging.d(TAG, "setSwappedFeeds: " + z);
        this.isSwappedFeeds = z;
        this.localProxyVideoSink.setTarget(z ? this.fullscreenRenderer : this.pipRenderer);
        this.remoteProxyRenderer.setTarget(z ? this.pipRenderer : this.fullscreenRenderer);
        this.fullscreenRenderer.setMirror(z);
        this.pipRenderer.setMirror(!z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConnectedToRoomInternal(AppRTCClient.SignalingParameters signalingParameters) {
        long currentTimeMillis = System.currentTimeMillis() - this.callStartedTimeMs;
        this.signalingParameters = signalingParameters;
        logAndToast("Creating peer connection, delay=" + currentTimeMillis + "ms");
        this.peerConnectionClient.createPeerConnection(this.localProxyVideoSink, this.remoteSinks, this.peerConnectionParameters.videoCallEnabled ? createVideoCapturer() : null, this.signalingParameters);
        if (Config_Var.initiatorcheck) {
            logAndToast("Creating OFFER...");
            this.peerConnectionClient.createOffer();
            return;
        }
        if (signalingParameters.offerSdp != null) {
            this.peerConnectionClient.setRemoteDescription(signalingParameters.offerSdp);
            logAndToast("Creating ANSWER...");
            this.peerConnectionClient.createAnswer();
        }
        if (signalingParameters.iceCandidates != null) {
            for (IceCandidate iceCandidate : signalingParameters.iceCandidates) {
                this.peerConnectionClient.addRemoteIceCandidate(iceCandidate);
            }
        }
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters signalingParameters) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.10
            @Override // java.lang.Runnable
            public void run() {
                CallLiveActivity.this.onConnectedToRoomInternal(signalingParameters);
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onRemoteDescription(final SessionDescription sessionDescription) {
        final long currentTimeMillis = System.currentTimeMillis() - this.callStartedTimeMs;
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.11
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.peerConnectionClient == null) {
                  //  Log.e(CallActivity.TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                CallLiveActivity callLiveActivity = CallLiveActivity.this;
                callLiveActivity.logAndToast("Received remote " + sessionDescription.type + ", delay=" + currentTimeMillis + "ms");
                CallLiveActivity.this.peerConnectionClient.setRemoteDescription(sessionDescription);
                if (!Config_Var.initiatorcheck) {
                    CallLiveActivity.this.logAndToast("Creating ANSWER...");
                    CallLiveActivity.this.peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onRemoteIceCandidate(final IceCandidate iceCandidate) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.12
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.peerConnectionClient == null) {
                 //   Log.e(CallActivity.TAG, "Received ICE candidate for a non-initialized peer connection.");
                } else {
                    CallLiveActivity.this.peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] iceCandidateArr) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.13
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.peerConnectionClient == null) {
                //    Log.e(CallActivity.TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                } else {
                    CallLiveActivity.this.peerConnectionClient.removeRemoteIceCandidates(iceCandidateArr);
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onChannelClose() {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.14
            @Override // java.lang.Runnable
            public void run() {
                CallLiveActivity.this.logAndToast("Remote end hung up; dropping PeerConnection");
                CallLiveActivity.this.disconnect();
            }
        });
    }

    @Override // com.app.videocallrandomchat2.AppRTCClient.SignalingEvents
    public void onChannelError(String str) {
        reportError(str);
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onLocalDescription(final SessionDescription sessionDescription) {
        final long currentTimeMillis = System.currentTimeMillis() - this.callStartedTimeMs;
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.15
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.appRtcClient != null) {
                    CallLiveActivity callLiveActivity = CallLiveActivity.this;
                    callLiveActivity.logAndToast("Sending " + sessionDescription.type + ", delay=" + currentTimeMillis + "ms");
                    if (Config_Var.initiatorcheck) {
                        CallLiveActivity.this.appRtcClient.sendOfferSdp(sessionDescription);
                    } else {
                        CallLiveActivity.this.appRtcClient.sendAnswerSdp(sessionDescription);
                    }
                }
                if (CallLiveActivity.this.peerConnectionParameters.videoMaxBitrate > 0) {
                 //   Log.d(CallActivity.TAG, "Set video maximum bitrate: " + CallActivity.this.peerConnectionParameters.videoMaxBitrate);
                    CallLiveActivity.this.peerConnectionClient.setVideoMaxBitrate(Integer.valueOf(CallLiveActivity.this.peerConnectionParameters.videoMaxBitrate));
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onIceCandidate(final IceCandidate iceCandidate) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.16
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.appRtcClient != null) {
                    CallLiveActivity.this.appRtcClient.sendLocalIceCandidate(iceCandidate);
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onIceCandidatesRemoved(final IceCandidate[] iceCandidateArr) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.17
            @Override // java.lang.Runnable
            public void run() {
                if (CallLiveActivity.this.appRtcClient != null) {
                    CallLiveActivity.this.appRtcClient.sendLocalIceCandidateRemovals(iceCandidateArr);
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onIceConnected() {
        final long currentTimeMillis = System.currentTimeMillis() - this.callStartedTimeMs;
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.18
            @Override // java.lang.Runnable
            public void run() {
                CallLiveActivity callLiveActivity = CallLiveActivity.this;
                callLiveActivity.logAndToast("ICE connected, delay=" + currentTimeMillis + "ms");
                CallLiveActivity.this.callFragment.updateEncoderStatistics("Connected");
                CallLiveActivity.this.iceConnected = true;
                CallLiveActivity.this.callConnected();
            }
        });
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onPeerConnectionStatsReady(final StatsReport[] statsReportArr) {
        runOnUiThread(new Runnable() { // from class: com.app.videocallrandomchat2.CallActivity.19
            @Override // java.lang.Runnable
            public void run() {
                if (!CallLiveActivity.this.isError && CallLiveActivity.this.iceConnected) {
                    CallLiveActivity.this.hudFragment.updateEncoderStatistics(statsReportArr);
                }
            }
        });
    }

    @Override // com.app.videocallrandomchat2.PeerConnectionClient.PeerConnectionEvents
    public void onPeerConnectionError(String str) {
        reportError(str);
    }
}
