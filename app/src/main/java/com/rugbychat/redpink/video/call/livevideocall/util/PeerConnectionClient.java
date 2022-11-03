package com.rugbychat.redpink.video.call.livevideocall.util;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CalledByNative;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpParameters;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.audio.LegacyAudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioTrack;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;


public class PeerConnectionClient {
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private static final int BPS_IN_KBPS = 1000;
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final String RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log";
    private static final String TAG = "PCRTCClient";
    private static final String VIDEO_CODEC_H264 = "H264";
    private static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
    private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String VIDEO_TRACK_TYPE = "video";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Context appContext;
    private MediaConstraints audioConstraints;
    @Nullable
    private AudioSource audioSource;
    @Nullable
    private DataChannel dataChannel;
    private final boolean dataChannelEnabled;
    private final PeerConnectionEvents events;
    @Nullable
    private PeerConnectionFactory factory;
    private boolean isError;
    private boolean isInitiator;
    @Nullable
    private AudioTrack localAudioTrack;
    @Nullable
    private VideoSink localRender;
    @Nullable
    private SessionDescription localSdp;
    @Nullable
    private RtpSender localVideoSender;
    @Nullable
    private VideoTrack localVideoTrack;
    @Nullable
    private PeerConnection peerConnection;
    private final PeerConnectionParameters peerConnectionParameters;
    private boolean preferIsac;
    @Nullable
    private List<IceCandidate> queuedRemoteCandidates;
    @Nullable
    private List<VideoSink> remoteSinks;
    @Nullable
    private VideoTrack remoteVideoTrack;
    private final EglBase rootEglBase;
    @Nullable
    private RtcEventLog rtcEventLog;
    @Nullable
    private RecordedAudioToFileController saveRecordedAudioToFile;
    private MediaConstraints sdpMediaConstraints;
    private AppRTCClient.SignalingParameters signalingParameters;
    @Nullable
    private SurfaceTextureHelper surfaceTextureHelper;
    @Nullable
    private VideoCapturer videoCapturer;
    private boolean videoCapturerStopped;
    private int videoFps;
    private int videoHeight;
    @Nullable
    private VideoSource videoSource;
    private int videoWidth;
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    private final Timer statsTimer = new Timer();
    private boolean renderVideo = true;
    private boolean enableAudio = true;


    public interface PeerConnectionEvents {
        void onIceCandidate(IceCandidate iceCandidate);

        void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr);

        void onIceConnected();

        void onIceDisconnected();

        void onLocalDescription(SessionDescription sessionDescription);

        void onPeerConnectionClosed();

        void onPeerConnectionError(String str);

        void onPeerConnectionStatsReady(StatsReport[] statsReportArr);
    }


    public static class DataChannelParameters {
        public final int id;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final boolean negotiated;
        public final boolean ordered;
        public final String protocol;

        public DataChannelParameters(boolean z, int i, int i2, String str, boolean z2, int i3) {
            this.ordered = z;
            this.maxRetransmitTimeMs = i;
            this.maxRetransmits = i2;
            this.protocol = str;
            this.negotiated = z2;
            this.id = i3;
        }
    }


    public static class PeerConnectionParameters {
        public final boolean aecDump;
        public final String audioCodec;
        public final int audioStartBitrate;
        private final DataChannelParameters dataChannelParameters;
        public final boolean disableBuiltInAEC;
        public final boolean disableBuiltInAGC;
        public final boolean disableBuiltInNS;
        public final boolean disableWebRtcAGCAndHPF;
        public final boolean enableRtcEventLog;
        public final boolean loopback;
        public final boolean noAudioProcessing;
        public final boolean saveInputAudioToFile;
        public final boolean tracing;
        public final boolean useLegacyAudioDevice;
        public final boolean useOpenSLES;
        public final boolean videoCallEnabled;
        public final String videoCodec;
        public final boolean videoCodecHwAcceleration;
        public final boolean videoFlexfecEnabled;
        public final int videoFps;
        public final int videoHeight;
        public final int videoMaxBitrate;
        public final int videoWidth;

        public PeerConnectionParameters(boolean z, boolean z2, boolean z3, int i, int i2, int i3, int i4, String str, boolean z4, boolean z5, int i5, String str2, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13, boolean z14, boolean z15, DataChannelParameters dataChannelParameters) {
            this.videoCallEnabled = z;
            this.loopback = z2;
            this.tracing = z3;
            this.videoWidth = i;
            this.videoHeight = i2;
            this.videoFps = i3;
            this.videoMaxBitrate = i4;
            this.videoCodec = str;
            this.videoFlexfecEnabled = z5;
            this.videoCodecHwAcceleration = z4;
            this.audioStartBitrate = i5;
            this.audioCodec = str2;
            this.noAudioProcessing = z6;
            this.aecDump = z7;
            this.saveInputAudioToFile = z8;
            this.useOpenSLES = z9;
            this.disableBuiltInAEC = z10;
            this.disableBuiltInAGC = z11;
            this.disableBuiltInNS = z12;
            this.disableWebRtcAGCAndHPF = z13;
            this.enableRtcEventLog = z14;
            this.useLegacyAudioDevice = z15;
            this.dataChannelParameters = dataChannelParameters;
        }
    }


    /**
     * Create a PeerConnectionClient with the specified parameters. PeerConnectionClient takes
     * ownership of |eglBase|.
     */
    public PeerConnectionClient(Context appContext, EglBase eglBase,
                                PeerConnectionParameters peerConnectionParameters, PeerConnectionEvents events) {
        this.rootEglBase = eglBase;
        this.appContext = appContext;
        this.events = events;
        this.peerConnectionParameters = peerConnectionParameters;
        this.dataChannelEnabled = peerConnectionParameters.dataChannelParameters != null;


        final String fieldTrials = getFieldTrials(peerConnectionParameters);
        executor.execute(() -> {
            PeerConnectionFactory.initialize(
                    PeerConnectionFactory.InitializationOptions.builder(appContext)
                            .setFieldTrials(fieldTrials)
                            .setEnableInternalTracer(true)
                            .createInitializationOptions());
        });
    }




    /**
     * This function should only be called once.
     */
    public void createPeerConnectionFactory(PeerConnectionFactory.Options options) {
        if (factory != null) {
            throw new IllegalStateException("PeerConnectionFactory has already been constructed");
        }
        executor.execute(() -> createPeerConnectionFactoryInternal(options));
    }




    public void createPeerConnection(final VideoSink localRender, final List<VideoSink> remoteSinks,
                                     final VideoCapturer videoCapturer, final AppRTCClient.SignalingParameters signalingParameters) {
        if (peerConnectionParameters == null) {
          //  Log.e(TAG, "Creating peer connection without initializing factory.");
            return;
        }
        this.localRender = localRender;
        this.remoteSinks = remoteSinks;
        this.videoCapturer = videoCapturer;
        this.signalingParameters = signalingParameters;
        executor.execute(() -> {
            try {
                createMediaConstraintsInternal();
                createPeerConnectionInternal();
                maybeCreateAndStartRtcEventLog();
            } catch (Exception e) {
                reportError("Failed to create peer connection: " + e.getMessage());
                throw e;
            }
        });
    }




    public void close() {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$ufBGBddjjFu-EZrnsZHL0x-JEEQ
            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.this.closeInternal();
            }
        });
    }

    public boolean isVideoCallEnabled() {
        return this.peerConnectionParameters.videoCallEnabled && this.videoCapturer != null;
    }

    private void createPeerConnectionFactoryInternal(PeerConnectionFactory.Options options) {
        isError = false;

        if (peerConnectionParameters.tracing) {
            PeerConnectionFactory.startInternalTracingCapture(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                            + "webrtc-trace.txt");
        }

        // Check if ISAC is used by default.
        preferIsac = peerConnectionParameters.audioCodec != null
                && peerConnectionParameters.audioCodec.equals(AUDIO_CODEC_ISAC);

        // It is possible to save a copy in raw PCM format on a file by checking
        // the "Save input audio to file" checkbox in the Settings UI. A callback
        // interface is set when this flag is enabled. As a result, a copy of recorded
        // audio samples are provided to this client directly from the native audio
        // layer in Java.
        if (peerConnectionParameters.saveInputAudioToFile) {
            if (!peerConnectionParameters.useOpenSLES) {
             //   Log.d(TAG, "Enable recording of microphone input audio to file");
                saveRecordedAudioToFile = new RecordedAudioToFileController(executor);
            } else {
                // TODO(henrika): ensure that the UI reflects that if OpenSL ES is selected,
                // then the "Save inut audio to file" option shall be grayed out.
             //   Log.e(TAG, "Recording of input audio is not supported for OpenSL ES");
            }
        }

        final AudioDeviceModule adm = createJavaAudioDevice();

        // Create peer connection factory.
        if (options != null) {
          //  Log.d(TAG, "Factory networkIgnoreMask option: " + options.networkIgnoreMask);
        }
        final boolean enableH264HighProfile =
                VIDEO_CODEC_H264_HIGH.equals(peerConnectionParameters.videoCodec);
        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;

        if (peerConnectionParameters.videoCodecHwAcceleration) {
            encoderFactory = new DefaultVideoEncoderFactory(
                    rootEglBase.getEglBaseContext(), true /* enableIntelVp8Encoder */, enableH264HighProfile);
            decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        } else {
            encoderFactory = new SoftwareVideoEncoderFactory();
            decoderFactory = new SoftwareVideoDecoderFactory();
        }

        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(adm)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
       // Log.d(TAG, "Peer connection factory created.");
        adm.release();
    }


    AudioDeviceModule createLegacyAudioDevice() {
        if (!this.peerConnectionParameters.useOpenSLES) {
           // Log.d(TAG, "Disable OpenSL ES audio even if device supports it");
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);
        } else {
           // Log.d(TAG, "Allow OpenSL ES audio if device supports it");
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
        }
        if (this.peerConnectionParameters.disableBuiltInAEC) {
           // Log.d(TAG, "Disable built-in AEC even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        } else {
          //  Log.d(TAG, "Enable built-in AEC if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
        }
        if (this.peerConnectionParameters.disableBuiltInNS) {
          //  Log.d(TAG, "Disable built-in NS even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        } else {
          //  Log.d(TAG, "Enable built-in NS if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
        }
        WebRtcAudioRecord.setOnAudioSamplesReady(this.saveRecordedAudioToFile);
        WebRtcAudioRecord.setErrorCallback(new WebRtcAudioRecord.WebRtcAudioRecordErrorCallback() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.1
            @Override // org.webrtc.voiceengine.WebRtcAudioRecord.WebRtcAudioRecordErrorCallback
            public void onWebRtcAudioRecordInitError(String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordInitError: " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.voiceengine.WebRtcAudioRecord.WebRtcAudioRecordErrorCallback
            public void onWebRtcAudioRecordStartError(WebRtcAudioRecord.AudioRecordStartErrorCode audioRecordStartErrorCode, String str) {
               // Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordStartError: " + audioRecordStartErrorCode + ". " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.voiceengine.WebRtcAudioRecord.WebRtcAudioRecordErrorCallback
            public void onWebRtcAudioRecordError(String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordError: " + str);
                PeerConnectionClient.this.reportError(str);
            }
        });
        WebRtcAudioTrack.setErrorCallback(new WebRtcAudioTrack.ErrorCallback() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.2
            @Override // org.webrtc.voiceengine.WebRtcAudioTrack.ErrorCallback
            public void onWebRtcAudioTrackInitError(String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackInitError: " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.voiceengine.WebRtcAudioTrack.ErrorCallback
            public void onWebRtcAudioTrackStartError(WebRtcAudioTrack.AudioTrackStartErrorCode audioTrackStartErrorCode, String str) {
               // Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackStartError: " + audioTrackStartErrorCode + ". " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.voiceengine.WebRtcAudioTrack.ErrorCallback
            public void onWebRtcAudioTrackError(String str) {
              //  Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackError: " + str);
                PeerConnectionClient.this.reportError(str);
            }
        });
        return new LegacyAudioDeviceModule();
    }

    AudioDeviceModule createJavaAudioDevice() {
        if (!this.peerConnectionParameters.useOpenSLES) {
           // Log.w(TAG, "External OpenSLES ADM not implemented yet.");
        }
        // from class: com.app.videocallrandomchat2.PeerConnectionClient.3
        // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
        // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
        // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
        JavaAudioDeviceModule.AudioRecordErrorCallback r0 = new JavaAudioDeviceModule.AudioRecordErrorCallback() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.3
            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
            public void onWebRtcAudioRecordInitError(String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordInitError: " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
            public void onWebRtcAudioRecordStartError(JavaAudioDeviceModule.AudioRecordStartErrorCode audioRecordStartErrorCode, String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordStartError: " + audioRecordStartErrorCode + ". " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
            public void onWebRtcAudioRecordError(String str) {
             //   Log.e(PeerConnectionClient.TAG, "onWebRtcAudioRecordError: " + str);
                PeerConnectionClient.this.reportError(str);
            }
        };
        return JavaAudioDeviceModule.builder(this.appContext).setSamplesReadyCallback(this.saveRecordedAudioToFile).setUseHardwareAcousticEchoCanceler(!this.peerConnectionParameters.disableBuiltInAEC).setUseHardwareNoiseSuppressor(!this.peerConnectionParameters.disableBuiltInNS).setAudioRecordErrorCallback(r0).setAudioTrackErrorCallback(new JavaAudioDeviceModule.AudioTrackErrorCallback() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.4
            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
            public void onWebRtcAudioTrackInitError(String str) {
              //  Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackInitError: " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
            public void onWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode audioTrackStartErrorCode, String str) {
              //  Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackStartError: " + audioTrackStartErrorCode + ". " + str);
                PeerConnectionClient.this.reportError(str);
            }

            @Override // org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
            public void onWebRtcAudioTrackError(String str) {
               // Log.e(PeerConnectionClient.TAG, "onWebRtcAudioTrackError: " + str);
                PeerConnectionClient.this.reportError(str);
            }
        }).createAudioDeviceModule();
    }

    private void createMediaConstraintsInternal() {
        if (isVideoCallEnabled()) {
            this.videoWidth = this.peerConnectionParameters.videoWidth;
            this.videoHeight = this.peerConnectionParameters.videoHeight;
            this.videoFps = this.peerConnectionParameters.videoFps;
            if (this.videoWidth == 0 || this.videoHeight == 0) {
                this.videoWidth = HD_VIDEO_WIDTH;
                this.videoHeight = HD_VIDEO_HEIGHT;
            }
            if (this.videoFps == 0) {
                this.videoFps = 30;
            }
           // Logging.d(TAG, "Capturing format: " + this.videoWidth + "x" + this.videoHeight + "@" + this.videoFps);
        }
        this.audioConstraints = new MediaConstraints();
        if (this.peerConnectionParameters.noAudioProcessing) {
          //  Log.d(TAG, "Disabling audio processing");
            this.audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            this.audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            this.audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            this.audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        this.sdpMediaConstraints = new MediaConstraints();
        this.sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        this.sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", Boolean.toString(isVideoCallEnabled())));
    }

    private void createPeerConnectionInternal() {
        if (this.factory == null || this.isError) {
          //  Log.e(TAG, "Peerconnection factory is not created");
            return;
        }
       // Log.d(TAG, "Create peer connection.");
        this.queuedRemoteCandidates = new ArrayList();
        PeerConnection.RTCConfiguration rTCConfiguration = new PeerConnection.RTCConfiguration(this.signalingParameters.iceServers);
        rTCConfiguration.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rTCConfiguration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rTCConfiguration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rTCConfiguration.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rTCConfiguration.keyType = PeerConnection.KeyType.ECDSA;
        rTCConfiguration.enableDtlsSrtp = Boolean.valueOf(!this.peerConnectionParameters.loopback);
        rTCConfiguration.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
        this.peerConnection = this.factory.createPeerConnection(rTCConfiguration, this.pcObserver);
        if (this.dataChannelEnabled) {
            DataChannel.Init init = new DataChannel.Init();
            init.ordered = this.peerConnectionParameters.dataChannelParameters.ordered;
            init.negotiated = this.peerConnectionParameters.dataChannelParameters.negotiated;
            init.maxRetransmits = this.peerConnectionParameters.dataChannelParameters.maxRetransmits;
            init.maxRetransmitTimeMs = this.peerConnectionParameters.dataChannelParameters.maxRetransmitTimeMs;
            init.id = this.peerConnectionParameters.dataChannelParameters.id;
            init.protocol = this.peerConnectionParameters.dataChannelParameters.protocol;
            this.dataChannel = this.peerConnection.createDataChannel("ApprtcDemo data", init);
        }
        this.isInitiator = false;
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
        List<String> singletonList = Collections.singletonList("ARDAMS");
        if (isVideoCallEnabled()) {
            this.peerConnection.addTrack(createVideoTrack(this.videoCapturer), singletonList);
            this.remoteVideoTrack = getRemoteVideoTrack();
            this.remoteVideoTrack.setEnabled(this.renderVideo);
            for (VideoSink videoSink : this.remoteSinks) {
                this.remoteVideoTrack.addSink(videoSink);
            }
        }
        this.peerConnection.addTrack(createAudioTrack(), singletonList);
        if (isVideoCallEnabled()) {
            findVideoSender();
        }
        if (this.peerConnectionParameters.aecDump) {
            try {
                this.factory.startAecDump(ParcelFileDescriptor.open(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Download/audio.aecdump"), 1006632960).detachFd(), -1);
            } catch (IOException e) {
              //  Log.e(TAG, "Can not open aecdump file", e);
            }
        }
        if (this.saveRecordedAudioToFile != null && this.saveRecordedAudioToFile.start()) {
          //  Log.d(TAG, "Recording input audio to file is activated");
        }
       // Log.d(TAG, "Peer connection created.");
    }

    private File createRtcEventLogOutputFile() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmm_ss", Locale.getDefault());
        Date date = new Date();
        return new File(this.appContext.getDir(RTCEVENTLOG_OUTPUT_DIR_NAME, 0), "event_log_" + simpleDateFormat.format(date) + ".log");
    }

    private void maybeCreateAndStartRtcEventLog() {
        if (this.appContext != null && this.peerConnection != null) {
            if (!this.peerConnectionParameters.enableRtcEventLog) {
            //    Log.d(TAG, "RtcEventLog is disabled.");
                return;
            }
            this.rtcEventLog = new RtcEventLog(this.peerConnection);
            this.rtcEventLog.start(createRtcEventLogOutputFile());
        }
    }

    public void closeInternal() {
        if (this.factory != null && this.peerConnectionParameters.aecDump) {
            this.factory.stopAecDump();
        }
      //  Log.d(TAG, "Closing peer connection.");
        this.statsTimer.cancel();
        if (this.dataChannel != null) {
            this.dataChannel.dispose();
            this.dataChannel = null;
        }
        if (this.rtcEventLog != null) {
            this.rtcEventLog.stop();
            this.rtcEventLog = null;
        }
        if (this.peerConnection != null) {
            this.peerConnection.dispose();
            this.peerConnection = null;
        }
      //  Log.d(TAG, "Closing audio source.");
        if (this.audioSource != null) {
            this.audioSource.dispose();
            this.audioSource = null;
        }
      //  Log.d(TAG, "Stopping capture.");
        if (this.videoCapturer != null) {
            try {
                this.videoCapturer.stopCapture();
                this.videoCapturerStopped = true;
                this.videoCapturer.dispose();
                this.videoCapturer = null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
       // Log.d(TAG, "Closing video source.");
        if (this.videoSource != null) {
            this.videoSource.dispose();
            this.videoSource = null;
        }
        if (this.surfaceTextureHelper != null) {
            this.surfaceTextureHelper.dispose();
            this.surfaceTextureHelper = null;
        }
        if (this.saveRecordedAudioToFile != null) {
        //    Log.d(TAG, "Closing audio file for recorded input audio.");
            this.saveRecordedAudioToFile.stop();
            this.saveRecordedAudioToFile = null;
        }
        this.localRender = null;
        this.remoteSinks = null;
     //   Log.d(TAG, "Closing peer connection factory.");
        if (this.factory != null) {
            this.factory.dispose();
            this.factory = null;
        }
        this.rootEglBase.release();
      //  Log.d(TAG, "Closing peer connection done.");
        this.events.onPeerConnectionClosed();
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
    }

    public boolean isHDVideo() {
        return isVideoCallEnabled() && this.videoWidth * this.videoHeight >= 921600;
    }

    public void getStats() {
        if (this.peerConnection != null && !this.isError && !this.peerConnection.getStats(new StatsObserver() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.5
            @Override // org.webrtc.StatsObserver
            public void onComplete(StatsReport[] statsReportArr) {
                PeerConnectionClient.this.events.onPeerConnectionStatsReady(statsReportArr);
            }
        }, null)) {
          //  Log.e(TAG, "getStats() returns false!");
        }
    }

    public void enableStatsEvents(boolean z, int i) {
        if (z) {
            try {
                this.statsTimer.schedule(new TimerTask() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.6
                    public void lambda$run$0() {
                        PeerConnectionClient.this.getStats();
                    }

                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        PeerConnectionClient.executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$6$CQuOFL-XWbJtYxJ2f15nZ-bXIq0
                            @Override // java.lang.Runnable
                            public final void run() {
                                lambda$run$0();
                            }
                        });
                    }
                }, 0, (long) i);
            } catch (Exception e) {
               // Log.e(TAG, "Can not schedule statistics timer", e);
            }
        } else {
            this.statsTimer.cancel();
        }
    }

    public void setAudioEnabled(boolean z) {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$ufjUAfMemby50cvKcvKvTHHEdzA
            private final boolean f$1;

            {
                this.f$1 = z;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$setAudioEnabled$3(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$setAudioEnabled$3(PeerConnectionClient peerConnectionClient, boolean z) {
        peerConnectionClient.enableAudio = z;
        if (peerConnectionClient.localAudioTrack != null) {
            peerConnectionClient.localAudioTrack.setEnabled(peerConnectionClient.enableAudio);
        }
    }

    public void setVideoEnabled(boolean z) {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$lZIW9NlSnFr2KuSvh6VIwz6HjwY
            private final boolean f$1;

            {
                this.f$1 = z;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$setVideoEnabled$4(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$setVideoEnabled$4(PeerConnectionClient peerConnectionClient, boolean z) {
        peerConnectionClient.renderVideo = z;
        if (peerConnectionClient.localVideoTrack != null) {
            peerConnectionClient.localVideoTrack.setEnabled(peerConnectionClient.renderVideo);
        }
        if (peerConnectionClient.remoteVideoTrack != null) {
            peerConnectionClient.remoteVideoTrack.setEnabled(peerConnectionClient.renderVideo);
        }
    }

    public void createOffer() {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$GyscE6kxRxPro1PjOYYdwfmNqUI
            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$createOffer$5(PeerConnectionClient.this);
            }
        });
    }

    public static void lambda$createOffer$5(PeerConnectionClient peerConnectionClient) {
        if (peerConnectionClient.peerConnection != null && !peerConnectionClient.isError) {
            Log.d(TAG, "PC Create OFFER");
            peerConnectionClient.isInitiator = true;
            peerConnectionClient.peerConnection.createOffer(peerConnectionClient.sdpObserver, peerConnectionClient.sdpMediaConstraints);
        }
    }

    public void createAnswer() {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$FuJAp31zHu9V9iZ4m7yGpgnHreA
            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$createAnswer$6(PeerConnectionClient.this);
            }
        });
    }

    public static void lambda$createAnswer$6(PeerConnectionClient peerConnectionClient) {
        if (peerConnectionClient.peerConnection != null && !peerConnectionClient.isError) {
            Log.d(TAG, "PC create ANSWER");
            peerConnectionClient.isInitiator = false;
            peerConnectionClient.peerConnection.createAnswer(peerConnectionClient.sdpObserver, peerConnectionClient.sdpMediaConstraints);
        }
    }

    public void addRemoteIceCandidate(IceCandidate iceCandidate) {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$EKucbFN8YDMFFODMlItDh5JwrDM
            private final IceCandidate f$1;

            {
                this.f$1 = iceCandidate;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$addRemoteIceCandidate$7(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$addRemoteIceCandidate$7(PeerConnectionClient peerConnectionClient, IceCandidate iceCandidate) {
        if (peerConnectionClient.peerConnection != null && !peerConnectionClient.isError) {
            if (peerConnectionClient.queuedRemoteCandidates != null) {
                peerConnectionClient.queuedRemoteCandidates.add(iceCandidate);
            } else {
                peerConnectionClient.peerConnection.addIceCandidate(iceCandidate);
            }
        }
    }

    public void removeRemoteIceCandidates(IceCandidate[] iceCandidateArr) {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$dyyhF_dCRGh7MCQPhDzq-ffy0LI
            private final IceCandidate[] f$1;

            {
                this.f$1 = iceCandidateArr;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$removeRemoteIceCandidates$8(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$removeRemoteIceCandidates$8(PeerConnectionClient peerConnectionClient, IceCandidate[] iceCandidateArr) {
        if (peerConnectionClient.peerConnection != null && !peerConnectionClient.isError) {
            peerConnectionClient.drainCandidates();
            peerConnectionClient.peerConnection.removeIceCandidates(iceCandidateArr);
        }
    }

    public void setRemoteDescription(final SessionDescription sdp) {
        executor.execute(() -> {
            if (peerConnection == null || isError) {
                return;
            }
            String sdpDescription = sdp.description;
            if (preferIsac) {
                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
            }
            if (isVideoCallEnabled()) {
                sdpDescription =
                        preferCodec(sdpDescription, getSdpVideoCodecName(peerConnectionParameters), false);
            }
            if (peerConnectionParameters.audioStartBitrate > 0) {
                sdpDescription = setStartBitrate(
                        AUDIO_CODEC_OPUS, false, sdpDescription, peerConnectionParameters.audioStartBitrate);
            }
            Log.d(TAG, "Set remote SDP.");
            SessionDescription sdpRemote = new SessionDescription(sdp.type, sdpDescription);
            peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
        });
    }




    public void stopVideoSource() {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$6we46ZxMp3G6KSxTgLncq7CnptI
            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$stopVideoSource$10(PeerConnectionClient.this);
            }
        });
    }

    public static void lambda$stopVideoSource$10(PeerConnectionClient peerConnectionClient) {
        if (peerConnectionClient.videoCapturer != null && !peerConnectionClient.videoCapturerStopped) {
            Log.d(TAG, "Stop video source.");
            try {
                peerConnectionClient.videoCapturer.stopCapture();
            } catch (InterruptedException unused) {
            }
            peerConnectionClient.videoCapturerStopped = true;
        }
    }

    public void startVideoSource() {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$dqaUDc81roonz3E1LpAU73MOcWY
            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$startVideoSource$11(PeerConnectionClient.this);
            }
        });
    }

    public static void lambda$startVideoSource$11(PeerConnectionClient peerConnectionClient) {
        if (peerConnectionClient.videoCapturer != null && peerConnectionClient.videoCapturerStopped) {
            Log.d(TAG, "Restart video source.");
            peerConnectionClient.videoCapturer.startCapture(peerConnectionClient.videoWidth, peerConnectionClient.videoHeight, peerConnectionClient.videoFps);
            peerConnectionClient.videoCapturerStopped = false;
        }
    }

    public void setVideoMaxBitrate(@Nullable Integer num) {
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$A4eRfGcOuZWQBbvreijwAkrCcv4
            private final Integer f$1;

            {
                this.f$1 = num;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$setVideoMaxBitrate$12(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$setVideoMaxBitrate$12(PeerConnectionClient peerConnectionClient, Integer num) {
        Integer num2;
        if (!(peerConnectionClient.peerConnection == null || peerConnectionClient.localVideoSender == null || peerConnectionClient.isError)) {
            Log.d(TAG, "Requested max video bitrate: " + num);
            if (peerConnectionClient.localVideoSender == null) {
                Log.w(TAG, "Sender is not ready.");
                return;
            }
            RtpParameters parameters = peerConnectionClient.localVideoSender.getParameters();
            if (parameters.encodings.size() == 0) {
                Log.w(TAG, "RtpParameters are not ready.");
                return;
            }
            for (RtpParameters.Encoding encoding : parameters.encodings) {
                if (num == null) {
                    num2 = null;
                } else {
                    num2 = Integer.valueOf(num.intValue() * 1000);
                }
                encoding.maxBitrateBps = num2;
            }
            if (!peerConnectionClient.localVideoSender.setParameters(parameters)) {
              //  Log.e(TAG, "RtpSender.setParameters failed.");
            }
           // Log.d(TAG, "Configured max video bitrate to: " + num);
        }
    }

    public void reportError(String str) {
      //  Log.e(TAG, "Peerconnection error: " + str);
        executor.execute(new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$PeerConnectionClient$nlrjL795lPR-GolYcK_MaWXcN4Y
            private final String f$1;

            {
                this.f$1 = str;
            }

            @Override // java.lang.Runnable
            public final void run() {
                PeerConnectionClient.lambda$reportError$13(PeerConnectionClient.this, this.f$1);
            }
        });
    }

    public static void lambda$reportError$13(PeerConnectionClient peerConnectionClient, String str) {
        if (!peerConnectionClient.isError) {
            peerConnectionClient.events.onPeerConnectionError(str);
            peerConnectionClient.isError = true;
        }
    }

    @Nullable
    private AudioTrack createAudioTrack() {
        this.audioSource = this.factory.createAudioSource(this.audioConstraints);
        this.localAudioTrack = this.factory.createAudioTrack(AUDIO_TRACK_ID, this.audioSource);
        this.localAudioTrack.setEnabled(this.enableAudio);
        return this.localAudioTrack;
    }

    @Nullable
    private VideoTrack createVideoTrack(VideoCapturer videoCapturer) {
        this.surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", this.rootEglBase.getEglBaseContext());
        this.videoSource = this.factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(this.surfaceTextureHelper, this.appContext, this.videoSource.getCapturerObserver());
        videoCapturer.startCapture(this.videoWidth, this.videoHeight, this.videoFps);
        this.localVideoTrack = this.factory.createVideoTrack(VIDEO_TRACK_ID, this.videoSource);
        this.localVideoTrack.setEnabled(this.renderVideo);
        this.localVideoTrack.addSink(this.localRender);
        return this.localVideoTrack;
    }

    private void findVideoSender() {
        for (RtpSender rtpSender : this.peerConnection.getSenders()) {
            if (rtpSender.track() != null && rtpSender.track().kind().equals("video")) {
                Log.d(TAG, "Found video sender.");
                this.localVideoSender = rtpSender;
            }
        }
    }

    @Nullable
    private VideoTrack getRemoteVideoTrack() {
        for (RtpTransceiver rtpTransceiver : this.peerConnection.getTransceivers()) {
            MediaStreamTrack track = rtpTransceiver.getReceiver().track();
            if (track instanceof VideoTrack) {
                return (VideoTrack) track;
            }
        }
        return null;
    }

    private static String getSdpVideoCodecName(PeerConnectionParameters parameters) {
        switch (parameters.videoCodec) {
            case VIDEO_CODEC_VP8:
                return VIDEO_CODEC_VP8;
            case VIDEO_CODEC_VP9:
                return VIDEO_CODEC_VP9;
            case VIDEO_CODEC_H264_HIGH:
            case VIDEO_CODEC_H264_BASELINE:
                return VIDEO_CODEC_H264;
            default:
                return VIDEO_CODEC_VP8;
        }
    }

    private static String getFieldTrials(PeerConnectionParameters peerConnectionParameters) {
        String str = "";
        if (peerConnectionParameters.videoFlexfecEnabled) {
            str = str + VIDEO_FLEXFEC_FIELDTRIAL;
            Log.d(TAG, "Enable FlexFEC field trial.");
        }
        String str2 = str + VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
        if (!peerConnectionParameters.disableWebRtcAGCAndHPF) {
            return str2;
        }
        String str3 = str2 + DISABLE_WEBRTC_AGC_FIELDTRIAL;
        Log.d(TAG, "Disable WebRTC AGC field trial.");
        return str3;
    }

    @SuppressWarnings("StringSplitter")
    private static String setStartBitrate(
            String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                Log.d(TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet =
                            "a=fmtp:" + codecRtpMap + " " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
                            + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }
        }
        return newSdpDescription.toString();
    }

    /** Returns the line number containing "m=audio|video", or -1 if no such line exists. */
    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }


    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

    private static @Nullable String movePayloadTypesToFront(
            List<String> preferredPayloadTypes, String mLine) {
        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
        final List<String> origLineParts = Arrays.asList(mLine.split(" "));
        if (origLineParts.size() <= 3) {
          //  Log.e(TAG, "Wrong SDP media description format: " + mLine);
            return null;
        }
        final List<String> header = origLineParts.subList(0, 3);
        final List<String> unpreferredPayloadTypes =
                new ArrayList<>(origLineParts.subList(3, origLineParts.size()));
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes);
        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
        // types.
        final List<String> newLineParts = new ArrayList<>();
        newLineParts.addAll(header);
        newLineParts.addAll(preferredPayloadTypes);
        newLineParts.addAll(unpreferredPayloadTypes);
        return joinString(newLineParts, " ", false /* delimiterAtEnd */);
    }


    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
        if (mLineIndex == -1) {
            Log.w(TAG, "No mediaDescription line, so can't prefer " + codec);
            return sdpDescription;
        }
        // A list with all the payload types with name |codec|. The payload types are integers in the
        // range 96-127, but they are stored as strings here.
        final List<String> codecPayloadTypes = new ArrayList<>();
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
        for (String line : lines) {
            Matcher codecMatcher = codecPattern.matcher(line);
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1));
            }
        }
        if (codecPayloadTypes.isEmpty()) {
            Log.w(TAG, "No payload types with name " + codec);
            return sdpDescription;
        }

        final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
        if (newMLine == null) {
            return sdpDescription;
        }
        Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
        lines[mLineIndex] = newMLine;
        return joinString(Arrays.asList(lines), "\r\n", true /* delimiterAtEnd */);
    }

    public void drainCandidates() {
        if (this.queuedRemoteCandidates != null) {
            Log.d(TAG, "Add " + this.queuedRemoteCandidates.size() + " remote candidates");
            for (IceCandidate iceCandidate : this.queuedRemoteCandidates) {
                this.peerConnection.addIceCandidate(iceCandidate);
            }
            this.queuedRemoteCandidates = null;
        }
    }

    public void switchCameraInternal() {
        if (!(this.videoCapturer instanceof CameraVideoCapturer)) {
          //  Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
        } else if (!isVideoCallEnabled() || this.isError) {
          //  Log.e(TAG, "Failed to switch camera. Video: " + isVideoCallEnabled() + ". Error : " + this.isError);
        } else {
          //  Log.d(TAG, "Switch camera");
            ((CameraVideoCapturer) this.videoCapturer).switchCamera(null);
        }
    }

    public void switchCamera() {
        executor.execute(new Runnable() {
            @Override
            public final void run() {
                PeerConnectionClient.this.switchCameraInternal();
            }
        });
    }

    public void changeCaptureFormat(final int width, final int height, final int framerate) {
        executor.execute(() -> changeCaptureFormatInternal(width, height, framerate));
    }


    private void changeCaptureFormatInternal(int width, int height, int framerate) {
        if (!isVideoCallEnabled() || isError || videoCapturer == null) {
          //  Log.e(TAG, "Failed to change capture format. Video: " + isVideoCallEnabled() + ". Error : " + isError);
            return;
        }
        Log.d(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate);
        videoSource.adaptOutputFormat(width, height, framerate);
    }


    public class PCObserver implements PeerConnection.Observer {
        @Override // org.webrtc.PeerConnection.Observer
        public void onAddStream(MediaStream mediaStream) {
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreamArr) {
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onRemoveStream(MediaStream mediaStream) {
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onRenegotiationNeeded() {
        }

        @Override // org.webrtc.PeerConnection.Observer
        @CalledByNative("Observer")
        public void onTrack(RtpTransceiver rtpTransceiver) {
//            PeerConnection.Observer.CC.$default$onTrack(this, rtpTransceiver);
        }

        private PCObserver() {
//            PeerConnectionClient.this = r1;
        }

        public void lambda$onIceCandidate$0(PCObserver pCObserver, IceCandidate iceCandidate) {
            PeerConnectionClient.this.events.onIceCandidate(iceCandidate);
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            executor.execute(() -> events.onIceCandidate(candidate));
        }

        public void lambda$onIceCandidatesRemoved$1(PCObserver pCObserver, IceCandidate[] iceCandidateArr) {
            PeerConnectionClient.this.events.onIceCandidatesRemoved(iceCandidateArr);
        }

        @Override
        public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
            executor.execute(() -> events.onIceCandidatesRemoved(candidates));
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d(PeerConnectionClient.TAG, "SignalingState: " + signalingState);
        }

        @Override
        public void onIceConnectionChange(final PeerConnection.IceConnectionState newState) {
            executor.execute(() -> {
                Log.d(TAG, "IceConnectionState: " + newState);
                if (newState == PeerConnection.IceConnectionState.CONNECTED) {
                    events.onIceConnected();
                } else if (newState == PeerConnection.IceConnectionState.DISCONNECTED) {
                    events.onIceDisconnected();
                } else if (newState == PeerConnection.IceConnectionState.FAILED) {
                    reportError("ICE connection failed.");
                }
            });
        }




        @Override // org.webrtc.PeerConnection.Observer
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d(PeerConnectionClient.TAG, "IceGatheringState: " + iceGatheringState);
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onIceConnectionReceivingChange(boolean z) {
            Log.d(PeerConnectionClient.TAG, "IceConnectionReceiving changed to " + z);
        }

        @Override // org.webrtc.PeerConnection.Observer
        public void onDataChannel(final DataChannel dataChannel) {
            Log.d(PeerConnectionClient.TAG, "New Data channel " + dataChannel.label());
            if (PeerConnectionClient.this.dataChannelEnabled) {
                dataChannel.registerObserver(new DataChannel.Observer() { // from class: com.app.videocallrandomchat2.PeerConnectionClient.PCObserver.1
                    @Override // org.webrtc.DataChannel.Observer
                    public void onBufferedAmountChange(long j) {
                        Log.d(PeerConnectionClient.TAG, "Data channel buffered amount changed: " + dataChannel.label() + ": " + dataChannel.state());
                    }

                    @Override // org.webrtc.DataChannel.Observer
                    public void onStateChange() {
                        Log.d(PeerConnectionClient.TAG, "Data channel state changed: " + dataChannel.label() + ": " + dataChannel.state());
                    }

                    @Override // org.webrtc.DataChannel.Observer
                    public void onMessage(DataChannel.Buffer buffer) {
                        if (buffer.binary) {
                            Log.d(PeerConnectionClient.TAG, "Received binary msg over " + dataChannel);
                            return;
                        }
                        ByteBuffer byteBuffer = buffer.data;
                        byte[] bArr = new byte[byteBuffer.capacity()];
                        byteBuffer.get(bArr);
                        String str = new String(bArr, StandardCharsets.UTF_8);
                        Log.d(PeerConnectionClient.TAG, "Got msg: " + str + " over " + dataChannel);
                    }
                });
            }
        }
    }

    // Implementation detail: handle offer creation/signaling and answer setting,
    // as well as adding remote ICE candidates once the answer SDP is set.
    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(final SessionDescription origSdp) {
            if (localSdp != null) {
                reportError("Multiple SDP create.");
                return;
            }
            String sdpDescription = origSdp.description;
            if (preferIsac) {
                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
            }
            if (isVideoCallEnabled()) {
                sdpDescription =
                        preferCodec(sdpDescription, getSdpVideoCodecName(peerConnectionParameters), false);
            }
            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
            localSdp = sdp;
            executor.execute(() -> {
                if (peerConnection != null && !isError) {
                    Log.d(TAG, "Set local SDP from " + sdp.type);
                    peerConnection.setLocalDescription(sdpObserver, sdp);
                }
            });
        }

        @Override
        public void onSetSuccess() {
            executor.execute(() -> {
                if (peerConnection == null || isError) {
                    return;
                }
                if (isInitiator) {
                    // For offering peer connection we first create offer and set
                    // local SDP, then after receiving answer set remote SDP.
                    if (peerConnection.getRemoteDescription() == null) {
                        // We've just set our local SDP so time to send it.
                        Log.d(TAG, "Local SDP set succesfully");
                        events.onLocalDescription(localSdp);
                    } else {
                        // We've just set remote description, so drain remote
                        // and send local ICE candidates.
                        Log.d(TAG, "Remote SDP set succesfully");
                        drainCandidates();
                    }
                } else {
                    // For answering peer connection we set remote SDP and then
                    // create answer and set local SDP.
                    if (peerConnection.getLocalDescription() != null) {
                        // We've just set our local SDP so time to send it, drain
                        // remote and send local ICE candidates.
                        Log.d(TAG, "Local SDP set succesfully");
                        events.onLocalDescription(localSdp);
                        drainCandidates();
                    } else {
                        // We've just set remote SDP - do nothing for now -
                        // answer will be created soon.
                        Log.d(TAG, "Remote SDP set succesfully");
                    }
                }
            });
        }
        @Override
        public void onCreateFailure(final String error) {
            reportError("createSDP error: " + error);
        }

        @Override
        public void onSetFailure(final String error) {
            reportError("setSDP error: " + error);
        }
    }
}
