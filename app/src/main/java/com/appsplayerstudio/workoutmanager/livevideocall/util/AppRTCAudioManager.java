package com.appsplayerstudio.workoutmanager.livevideocall.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;

import com.appsplayerstudio.workoutmanager.R;

import org.webrtc.MediaStreamTrack;
import org.webrtc.ThreadUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;


public class AppRTCAudioManager {
    private static final String SPEAKERPHONE_AUTO = "auto";
    private static final String SPEAKERPHONE_FALSE = "false";
    private static final String SPEAKERPHONE_TRUE = "true";
    private static final String TAG = "AppRTCAudioManager";
    private final Context apprtcContext;
    @Nullable
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    @Nullable
    private AudioManager audioManager;
    @Nullable
    private AudioManagerEvents audioManagerEvents;
    private final AppRTCBluetoothManager bluetoothManager;
    private AudioDevice defaultAudioDevice;
    private boolean hasWiredHeadset;
    @Nullable
    private AppRTCProximitySensor proximitySensor;
    private boolean savedIsMicrophoneMute;
    private boolean savedIsSpeakerPhoneOn;
    private AudioDevice selectedAudioDevice;
    private final String useSpeakerphone;
    private AudioDevice userSelectedAudioDevice;
    private int savedAudioMode = -2;
    private Set<AudioDevice> audioDevices = new HashSet();
    private BroadcastReceiver wiredHeadsetReceiver = new WiredHeadsetReceiver();
    private AudioManagerState amState = AudioManagerState.UNINITIALIZED;


    public enum AudioDevice {
        SPEAKER_PHONE,
        WIRED_HEADSET,
        EARPIECE,
        BLUETOOTH,
        NONE
    }


    public interface AudioManagerEvents {
        void onAudioDeviceChanged(AudioDevice audioDevice, Set<AudioDevice> set);
    }


    public enum AudioManagerState {
        UNINITIALIZED,
        PREINITIALIZED,
        RUNNING
    }

    public void onProximitySensorChangedState() {
        if (!this.useSpeakerphone.equals(SPEAKERPHONE_AUTO) || this.audioDevices.size() != 2 || !this.audioDevices.contains(AudioDevice.EARPIECE) || !this.audioDevices.contains(AudioDevice.SPEAKER_PHONE)) {
            return;
        }
        if (this.proximitySensor.sensorReportsNearState()) {
            setAudioDeviceInternal(AudioDevice.EARPIECE);
        } else {
            setAudioDeviceInternal(AudioDevice.SPEAKER_PHONE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */

    public class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int HAS_MIC = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int STATE_UNPLUGGED = 0;

        private WiredHeadsetReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("state", 0);
            int intExtra2 = intent.getIntExtra("microphone", 0);
            String stringExtra = intent.getStringExtra("name");
            StringBuilder sb = new StringBuilder();
            sb.append("WiredHeadsetReceiver.onReceive");
            sb.append(AppRTCUtils.getThreadInfo());
            sb.append(": a=");
            sb.append(intent.getAction());
            sb.append(", s=");
            sb.append(intExtra == 0 ? "unplugged" : "plugged");
            sb.append(", m=");
            boolean z = true;
            sb.append(intExtra2 == 1 ? "mic" : "no mic");
            sb.append(", n=");
            sb.append(stringExtra);
            sb.append(", sb=");
            sb.append(isInitialStickyBroadcast());
           // Log.d(AppRTCAudioManager.TAG, sb.toString());
            AppRTCAudioManager appRTCAudioManager = AppRTCAudioManager.this;
            if (intExtra != 1) {
                z = false;
            }
            appRTCAudioManager.hasWiredHeadset = z;
            AppRTCAudioManager.this.updateAudioDeviceState();
        }
    }

    public static AppRTCAudioManager create(Context context) {
        return new AppRTCAudioManager(context);
    }

    @SuppressLint("WrongConstant")
    private AppRTCAudioManager(Context context) {
       // Log.d(TAG, "ctor");
        ThreadUtils.checkIsOnMainThread();
        this.apprtcContext = context;
        this.audioManager = (AudioManager) context.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
        this.bluetoothManager = AppRTCBluetoothManager.create(context, this);
        this.useSpeakerphone = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_speakerphone_key), context.getString(R.string.pref_speakerphone_default));
      //  Log.d(TAG, "useSpeakerphone: " + this.useSpeakerphone);
        if (this.useSpeakerphone.equals(SPEAKERPHONE_FALSE)) {
            this.defaultAudioDevice = AudioDevice.EARPIECE;
        } else {
            this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
        }
        this.proximitySensor = AppRTCProximitySensor.create(context, new Runnable() { // from class: com.app.videocallrandomchat2.-$$Lambda$AppRTCAudioManager$z3mBVLGa6MO86mtlcWG8-cbgLJM
            @Override // java.lang.Runnable
            public final void run() {
                AppRTCAudioManager.this.onProximitySensorChangedState();
            }
        });
       // Log.d(TAG, "defaultAudioDevice: " + this.defaultAudioDevice);
        AppRTCUtils.logDeviceInfo(TAG);
    }

    @SuppressLint("WrongConstant")
    public void start(AudioManagerEvents audioManagerEvents) {
       // Log.d(TAG, "start");
        ThreadUtils.checkIsOnMainThread();
        if (this.amState == AudioManagerState.RUNNING) {
            return;
        }
       // Log.d(TAG, "AudioManager starts...");
        this.audioManagerEvents = audioManagerEvents;
        this.amState = AudioManagerState.RUNNING;
        this.savedAudioMode = this.audioManager.getMode();
        this.savedIsSpeakerPhoneOn = this.audioManager.isSpeakerphoneOn();
        this.savedIsMicrophoneMute = this.audioManager.isMicrophoneMute();
        this.hasWiredHeadset = hasWiredHeadset();
        this.audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.app.videocallrandomchat2.AppRTCAudioManager.1
            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public void onAudioFocusChange(int i) {
                String str;
                switch (i) {
                    case -3:
                        str = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                        break;
                    case -2:
                        str = "AUDIOFOCUS_LOSS_TRANSIENT";
                        break;
                    case -1:
                        str = "AUDIOFOCUS_LOSS";
                        break;
                    case 0:
                    default:
                        str = "AUDIOFOCUS_INVALID";
                        break;
                    case 1:
                        str = "AUDIOFOCUS_GAIN";
                        break;
                    case 2:
                        str = "AUDIOFOCUS_GAIN_TRANSIENT";
                        break;
                    case 3:
                        str = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                        break;
                    case 4:
                        str = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
                        break;
                }
             //   Log.d(AppRTCAudioManager.TAG, "onAudioFocusChange: " + str);
            }
        };
        if (this.audioManager.requestAudioFocus(this.audioFocusChangeListener, 0, 2) == 1) {
       //
        } else {
            //
        }
        this.audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        setMicrophoneMute(false);
        this.userSelectedAudioDevice = AudioDevice.NONE;
        this.selectedAudioDevice = AudioDevice.NONE;
        this.audioDevices.clear();
        this.bluetoothManager.start();
        updateAudioDeviceState();
        registerReceiver(this.wiredHeadsetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
      //  Log.d(TAG, "AudioManager started");
    }

    @SuppressLint("WrongConstant")
    public void stop() {
       // Log.d(TAG, "stop");
        ThreadUtils.checkIsOnMainThread();
        if (this.amState != AudioManagerState.RUNNING) {
            return;
        }
        this.amState = AudioManagerState.UNINITIALIZED;
        unregisterReceiver(this.wiredHeadsetReceiver);
        this.bluetoothManager.stop();
        setSpeakerphoneOn(this.savedIsSpeakerPhoneOn);
        setMicrophoneMute(this.savedIsMicrophoneMute);
        this.audioManager.setMode(this.savedAudioMode);
        this.audioManager.abandonAudioFocus(this.audioFocusChangeListener);
        this.audioFocusChangeListener = null;
       // Log.d(TAG, "Abandoned audio focus for VOICE_CALL streams");
        if (this.proximitySensor != null) {
            this.proximitySensor.stop();
            this.proximitySensor = null;
        }
        this.audioManagerEvents = null;
      //  Log.d(TAG, "AudioManager stopped");
    }

    private void setAudioDeviceInternal(AudioDevice audioDevice) {
      //  Log.d(TAG, "setAudioDeviceInternal(device=" + audioDevice + ")");
        AppRTCUtils.assertIsTrue(this.audioDevices.contains(audioDevice));
        switch (audioDevice) {
            case SPEAKER_PHONE:
                setSpeakerphoneOn(true);
                break;
            case EARPIECE:
                setSpeakerphoneOn(false);
                break;
            case WIRED_HEADSET:
                setSpeakerphoneOn(false);
                break;
            case BLUETOOTH:
                setSpeakerphoneOn(false);
                break;
            default:
                break;
        }
        this.selectedAudioDevice = audioDevice;
    }

    public void setDefaultAudioDevice(AudioDevice audioDevice) {
        ThreadUtils.checkIsOnMainThread();
        switch (audioDevice) {
            case SPEAKER_PHONE:
                this.defaultAudioDevice = audioDevice;
                break;
            case EARPIECE:
                if (!hasEarpiece()) {
                    this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
                    break;
                } else {
                    this.defaultAudioDevice = audioDevice;
                    break;
                }
            default:
                break;
        }
      //  Log.d(TAG, "setDefaultAudioDevice(device=" + this.defaultAudioDevice + ")");
        updateAudioDeviceState();
    }

    public void selectAudioDevice(AudioDevice audioDevice) {
        ThreadUtils.checkIsOnMainThread();
        if (!this.audioDevices.contains(audioDevice)) {
        }
        this.userSelectedAudioDevice = audioDevice;
        updateAudioDeviceState();
    }

    public Set<AudioDevice> getAudioDevices() {
        ThreadUtils.checkIsOnMainThread();
        return Collections.unmodifiableSet(new HashSet(this.audioDevices));
    }

    public AudioDevice getSelectedAudioDevice() {
        ThreadUtils.checkIsOnMainThread();
        return this.selectedAudioDevice;
    }

    private void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        this.apprtcContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        this.apprtcContext.unregisterReceiver(broadcastReceiver);
    }

    private void setSpeakerphoneOn(boolean z) {
        if (this.audioManager.isSpeakerphoneOn() != z) {
            this.audioManager.setSpeakerphoneOn(z);
        }
    }

    private void setMicrophoneMute(boolean z) {
        if (this.audioManager.isMicrophoneMute() != z) {
            this.audioManager.setMicrophoneMute(z);
        }
    }

    private boolean hasEarpiece() {
        return this.apprtcContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        if (Build.VERSION.SDK_INT < 23) {
            return this.audioManager.isWiredHeadsetOn();
        }
        for (AudioDeviceInfo audioDeviceInfo : this.audioManager.getDevices(3)) {
            int type = audioDeviceInfo.getType();
            if (type == 3) {
              //  Log.d(TAG, "hasWiredHeadset: found wired headset");
                return true;
            } else if (type == 11) {
              //  Log.d(TAG, "hasWiredHeadset: found USB audio device");
                return true;
            }
        }
        return false;
    }

    public void updateAudioDeviceState() {
        AudioDevice audioDevice;
        ThreadUtils.checkIsOnMainThread();
      //  Log.d(TAG, "--- updateAudioDeviceState: wired headset=" + this.hasWiredHeadset + ", BT state=" + this.bluetoothManager.getState());
      //  Log.d(TAG, "Device status: available=" + this.audioDevices + ", selected=" + this.selectedAudioDevice + ", user selected=" + this.userSelectedAudioDevice);
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_DISCONNECTING) {
            this.bluetoothManager.updateDevice();
        }
        HashSet hashSet = new HashSet();
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE) {
            hashSet.add(AudioDevice.BLUETOOTH);
        }
        if (this.hasWiredHeadset) {
            hashSet.add(AudioDevice.WIRED_HEADSET);
        } else {
            hashSet.add(AudioDevice.SPEAKER_PHONE);
            if (hasEarpiece()) {
                hashSet.add(AudioDevice.EARPIECE);
            }
        }
        boolean z = !this.audioDevices.equals(hashSet);
        this.audioDevices = hashSet;
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE && this.userSelectedAudioDevice == AudioDevice.BLUETOOTH) {
            this.userSelectedAudioDevice = AudioDevice.NONE;
        }
        if (this.hasWiredHeadset && this.userSelectedAudioDevice == AudioDevice.SPEAKER_PHONE) {
            this.userSelectedAudioDevice = AudioDevice.WIRED_HEADSET;
        }
        if (!this.hasWiredHeadset && this.userSelectedAudioDevice == AudioDevice.WIRED_HEADSET) {
            this.userSelectedAudioDevice = AudioDevice.SPEAKER_PHONE;
        }
        boolean z2 = false;
        boolean z3 = this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE && (this.userSelectedAudioDevice == AudioDevice.NONE || this.userSelectedAudioDevice == AudioDevice.BLUETOOTH);
        if (!((this.bluetoothManager.getState() != AppRTCBluetoothManager.State.SCO_CONNECTED && this.bluetoothManager.getState() != AppRTCBluetoothManager.State.SCO_CONNECTING) || this.userSelectedAudioDevice == AudioDevice.NONE || this.userSelectedAudioDevice == AudioDevice.BLUETOOTH)) {
            z2 = true;
        }
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED) {
         //   Log.d(TAG, "Need BT audio: start=" + z3 + ", stop=" + z2 + ", BT state=" + this.bluetoothManager.getState());
        }
        if (z2) {
            this.bluetoothManager.stopScoAudio();
            this.bluetoothManager.updateDevice();
        }
        if (z3 && !z2 && !this.bluetoothManager.startScoAudio()) {
            this.audioDevices.remove(AudioDevice.BLUETOOTH);
            z = true;
        }
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED) {
            audioDevice = AudioDevice.BLUETOOTH;
        } else if (this.hasWiredHeadset) {
            audioDevice = AudioDevice.WIRED_HEADSET;
        } else {
            audioDevice = this.defaultAudioDevice;
        }
        if (audioDevice != this.selectedAudioDevice || z) {
            setAudioDeviceInternal(audioDevice);
          //  Log.d(TAG, "New device status: available=" + this.audioDevices + ", selected=" + audioDevice);
            if (this.audioManagerEvents != null) {
                this.audioManagerEvents.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }
       // Log.d(TAG, "--- updateAudioDeviceState done");
    }
}
