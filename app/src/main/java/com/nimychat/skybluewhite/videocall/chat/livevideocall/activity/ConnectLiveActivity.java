package com.nimychat.skybluewhite.videocall.chat.livevideocall.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nimychat.skybluewhite.videocall.chat.R;
import com.nimychat.skybluewhite.videocall.chat.livevideocall.util.Config_Var;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class ConnectLiveActivity extends Activity {
    private static final int CONNECTION_REQUEST = 1;
    private static final int REMOVE_FAVORITE_INDEX = 0;
    private static final String TAG = "ConnectActivity";
    private static boolean commandLineRun = false;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefFps;
    private String keyprefResolution;
    private String keyprefRoomServerUrl;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private SharedPreferences sharedPref;
    CountDownTimer r1CallsForLiv;
    TextView timeText;



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.keyprefResolution = getString(R.string.pref_resolution_key);
        this.keyprefFps = getString(R.string.pref_fps_key);
        this.keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        this.keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        this.keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        this.keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        this.keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        setContentView(R.layout.activity_connecting);

        timeText = findViewById(R.id.timer_txt);



        Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
            connectToRoom("connecting..", true, intent.getBooleanExtra(CallLiveActivity.EXTRA_LOOPBACK, false), intent.getBooleanExtra(CallLiveActivity.EXTRA_USE_VALUES_FROM_INTENT, false), intent.getIntExtra(CallLiveActivity.EXTRA_RUNTIME, 0));
        }

        r1CallsForLiv = new CountDownTimer(3000, 1000) {
            public void onTick(long j) {

                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(j / 1000);
                timeText.setText(sb.toString());
            }

            public void onFinish() {

                timeText.setText("00:00");

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        Config_Var.initiatorcheck = false;
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConnectLiveActivity.this.getApplicationContext());
                        ArrayList arrayList = new ArrayList();
                        arrayList.clear();
                        for (int i = 1; i <= Config_Var.limit; i++) {
                            if (!defaultSharedPreferences.getBoolean(i + "", false)) {
                                arrayList.add(Integer.valueOf(i));
                            }
                        }
                        Config_Var.video_call = new Random().nextInt(Config_Var.percent) != 0;
                        if (arrayList.size() > 0) {
                            Collections.shuffle(arrayList);
                            Config_Var.media_number = arrayList.get(0) + "";
                        } else {
                            Config_Var.video_call = true;
                        }
                        boolean z = ContextCompat.checkSelfPermission(ConnectLiveActivity.this, "android.permission.RECORD_AUDIO") == 0;
                        if (ContextCompat.checkSelfPermission(ConnectLiveActivity.this, "android.permission.CAMERA") != 0) {
                            z = false;
                        }
                        if (z) {
                            ConnectLiveActivity.this.connectToRoom("Connecting..", false, false, false, 0);
                        } else {
                            ActivityCompat.requestPermissions(ConnectLiveActivity.this, new String[]{"android.permission.RECORD_AUDIO", "android.permission.CAMERA"}, 123);
                        }

                    }
                }, 500);

            }
        };
        r1CallsForLiv.start();

    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && commandLineRun) {

            setResult(i2);
            commandLineRun = false;
            finish();
        }
        Log.d(TAG, "Return:a " + intent);
    }

    private String sharedPrefGetString(int i, String str, int i2, boolean z) {
        String string = getString(i2);
        if (z) {
            String stringExtra = getIntent().getStringExtra(str);
            return stringExtra != null ? stringExtra : string;
        }
        return this.sharedPref.getString(getString(i), string);
    }

    private boolean sharedPrefGetBoolean(int i, String str, int i2, boolean z) {
        boolean booleanValue = Boolean.valueOf(getString(i2)).booleanValue();
        if (z) {
            return getIntent().getBooleanExtra(str, booleanValue);
        }
        return this.sharedPref.getBoolean(getString(i), booleanValue);
    }

    private int sharedPrefGetInteger(int i, String str, int i2, boolean z) {
        String string = getString(i2);
        int parseInt = Integer.parseInt(string);
        if (z) {
            return getIntent().getIntExtra(str, parseInt);
        }
        String string2 = getString(i);
        String string3 = this.sharedPref.getString(string2, string);
        try {
            return Integer.parseInt(string3);
        } catch (NumberFormatException unused) {
            return parseInt;
        }
    }


    @SuppressWarnings("StringSplitter")
    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        ConnectLiveActivity.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallLiveActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallLiveActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallLiveActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallLiveActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallLiveActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallLiveActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallLiveActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallLiveActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallLiveActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallLiveActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        boolean saveInputAudioToFile =
                sharedPrefGetBoolean(R.string.pref_enable_save_input_audio_to_file_key,
                        CallLiveActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED,
                        R.string.pref_enable_save_input_audio_to_file_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallLiveActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallLiveActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallLiveActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallLiveActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallLiveActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(CallLiveActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(CallLiveActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(CallLiveActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallLiveActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(CallLiveActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(CallLiveActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallLiveActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallLiveActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Check Enable RtcEventLog.
        boolean rtcEventLogEnabled = sharedPrefGetBoolean(R.string.pref_enable_rtceventlog_key,
                CallLiveActivity.EXTRA_ENABLE_RTCEVENTLOG, R.string.pref_enable_rtceventlog_default,
                useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallLiveActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallLiveActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallLiveActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallLiveActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallLiveActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallLiveActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallLiveActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity.
         Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallLiveActivity.class);
            intent.setData(uri);
            intent.putExtra(CallLiveActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallLiveActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallLiveActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallLiveActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallLiveActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallLiveActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallLiveActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallLiveActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallLiveActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallLiveActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallLiveActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, saveInputAudioToFile);
            intent.putExtra(CallLiveActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallLiveActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallLiveActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallLiveActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallLiveActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallLiveActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallLiveActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallLiveActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallLiveActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallLiveActivity.EXTRA_ENABLE_RTCEVENTLOG, rtcEventLogEnabled);
            intent.putExtra(CallLiveActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallLiveActivity.EXTRA_RUNTIME, runTimeMs);
            intent.putExtra(CallLiveActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(CallLiveActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallLiveActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallLiveActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallLiveActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallLiveActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallLiveActivity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallLiveActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(CallLiveActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallLiveActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallLiveActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST);
            finish();

        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 123) {
            if (iArr.length > 1 && iArr[0] == 0 && iArr[1] == 0) {
                connectToRoom("connecting..", false, false, false, 0);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission required");
            builder.setMessage("Please put the Microphone or Camera permission").setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() { // from class: com.app.videocallrandomchat2.ConnectActivity.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    ConnectLiveActivity.this.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", ConnectLiveActivity.this.getPackageName(), null)));
                }
            }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() { // from class: com.app.videocallrandomchat2.ConnectActivity.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
super.onBackPressed();
        if (r1CallsForLiv != null) {
            r1CallsForLiv.cancel();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (r1CallsForLiv != null) {
            r1CallsForLiv.cancel();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (r1CallsForLiv != null) {
            r1CallsForLiv.cancel();
        }

    }
}
