package com.appsplayerstudio.workoutmanager.livevideocall.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import org.webrtc.ThreadUtils;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;


public class AppRTCBluetoothManager {
    private static final int BLUETOOTH_SCO_TIMEOUT_MS = 4000;
    private static final int MAX_SCO_CONNECTION_ATTEMPTS = 2;
    private static final String TAG = "AppRTCBluetoothManager";
    private final AppRTCAudioManager apprtcAudioManager;
    private final Context apprtcContext;
    @Nullable
    private final AudioManager audioManager;
    @Nullable
    private BluetoothAdapter bluetoothAdapter;
    @Nullable
    private BluetoothDevice bluetoothDevice;
    @Nullable
    private BluetoothHeadset bluetoothHeadset;
    int scoConnectionAttempts;
    private final Runnable bluetoothTimeoutRunnable = new Runnable() { // from class: com.app.videocallrandomchat2.AppRTCBluetoothManager.1
        @Override // java.lang.Runnable
        public void run() {
            AppRTCBluetoothManager.this.bluetoothTimeout();
        }
    };
    private State bluetoothState = State.UNINITIALIZED;
    private final BluetoothProfile.ServiceListener bluetoothServiceListener = new BluetoothServiceListener();
    private final BroadcastReceiver bluetoothHeadsetReceiver = new BluetoothHeadsetBroadcastReceiver();
    private final Handler handler = new Handler(Looper.getMainLooper());


    public enum State {
        UNINITIALIZED,
        ERROR,
        HEADSET_UNAVAILABLE,
        HEADSET_AVAILABLE,
        SCO_DISCONNECTING,
        SCO_CONNECTING,
        SCO_CONNECTED
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String stateToString(int i) {
        switch (i) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "DISCONNECTING";
            default:
                switch (i) {
                    case 10:
                        return "OFF";
                    case 11:
                        return "TURNING_ON";
                    case 12:
                        return "ON";
                    case 13:
                        return "TURNING_OFF";
                    default:
                        return "INVALID";
                }
        }
    }


    private class BluetoothServiceListener implements BluetoothProfile.ServiceListener {
        private BluetoothServiceListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == 1 && AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
              //  Log.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceConnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
                AppRTCBluetoothManager.this.bluetoothHeadset = (BluetoothHeadset) bluetoothProfile;
                AppRTCBluetoothManager.this.updateAudioDeviceState();
              //  Log.d(AppRTCBluetoothManager.TAG, "onServiceConnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            if (i == 1 && AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
              //  Log.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceDisconnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
                AppRTCBluetoothManager.this.stopScoAudio();
                AppRTCBluetoothManager.this.bluetoothHeadset = null;
                AppRTCBluetoothManager.this.bluetoothDevice = null;
                AppRTCBluetoothManager.this.bluetoothState = State.HEADSET_UNAVAILABLE;
                AppRTCBluetoothManager.this.updateAudioDeviceState();
              //  Log.d(AppRTCBluetoothManager.TAG, "onServiceDisconnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }
    }


    private class BluetoothHeadsetBroadcastReceiver extends BroadcastReceiver {
        private BluetoothHeadsetBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                String action = intent.getAction();
                if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                    int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                  //  Log.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(intExtra) + ", sb=" + isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                    if (intExtra == 2) {
                        AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    } else if (!(intExtra == 1 || intExtra == 3 || intExtra != 0)) {
                        AppRTCBluetoothManager.this.stopScoAudio();
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    }
                } else if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                    int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                  //  Log.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(intExtra2) + ", sb=" + isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                    if (intExtra2 == 12) {
                        AppRTCBluetoothManager.this.cancelTimer();
                        if (AppRTCBluetoothManager.this.bluetoothState == State.SCO_CONNECTING) {
                          //  Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connected");
                            AppRTCBluetoothManager.this.bluetoothState = State.SCO_CONNECTED;
                            AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                            AppRTCBluetoothManager.this.updateAudioDeviceState();
                        } else {
                            Log.w(AppRTCBluetoothManager.TAG, "Unexpected state BluetoothHeadset.STATE_AUDIO_CONNECTED");
                        }
                    } else if (intExtra2 == 11) {
                      //  Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connecting...");
                    } else if (intExtra2 == 10) {
                       // Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now disconnected");
                        if (isInitialStickyBroadcast()) {
                         //   Log.d(AppRTCBluetoothManager.TAG, "Ignore STATE_AUDIO_DISCONNECTED initial sticky broadcast.");
                            return;
                        }
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    }
                }
              //  Log.d(AppRTCBluetoothManager.TAG, "onReceive done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AppRTCBluetoothManager create(Context context, AppRTCAudioManager appRTCAudioManager) {
       // Log.d(TAG, "create" + AppRTCUtils.getThreadInfo());
        return new AppRTCBluetoothManager(context, appRTCAudioManager);
    }

    protected AppRTCBluetoothManager(Context context, AppRTCAudioManager appRTCAudioManager) {
      //  Log.d(TAG, "ctor");
        ThreadUtils.checkIsOnMainThread();
        this.apprtcContext = context;
        this.apprtcAudioManager = appRTCAudioManager;
        this.audioManager = getAudioManager(context);
    }

    public State getState() {
        ThreadUtils.checkIsOnMainThread();
        return this.bluetoothState;
    }

    public void start() {
        ThreadUtils.checkIsOnMainThread();
      //  Log.d(TAG, "start");
        if (!hasPermission(this.apprtcContext, "android.permission.BLUETOOTH")) {
            Log.w(TAG, "Process (pid=" + Process.myPid() + ") lacks BLUETOOTH permission");
        } else if (this.bluetoothState != State.UNINITIALIZED) {
            Log.w(TAG, "Invalid BT state");
        } else {
            this.bluetoothHeadset = null;
            this.bluetoothDevice = null;
            this.scoConnectionAttempts = 0;
            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.bluetoothAdapter == null) {
               // Log.w(TAG, "Device does not support Bluetooth");
            } else if (!this.audioManager.isBluetoothScoAvailableOffCall()) {
               // Log.e(TAG, "Bluetooth SCO audio is not available off call");
            } else {
                logBluetoothAdapterInfo(this.bluetoothAdapter);
                if (!getBluetoothProfileProxy(this.apprtcContext, this.bluetoothServiceListener, 1)) {
                  //  Log.e(TAG, "BluetoothAdapter.getProfileProxy(HEADSET) failed");
                    return;
                }
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                intentFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
                registerReceiver(this.bluetoothHeadsetReceiver, intentFilter);
               // Log.d(TAG, "HEADSET profile state: " + stateToString(this.bluetoothAdapter.getProfileConnectionState(1)));
              //  Log.d(TAG, "Bluetooth proxy for headset profile has started");
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
               // Log.d(TAG, "start done: BT state=" + this.bluetoothState);
            }
        }
    }

    public void stop() {
        ThreadUtils.checkIsOnMainThread();
       // Log.d(TAG, "stop: BT state=" + this.bluetoothState);
        if (this.bluetoothAdapter != null) {
            stopScoAudio();
            if (this.bluetoothState != State.UNINITIALIZED) {
                unregisterReceiver(this.bluetoothHeadsetReceiver);
                cancelTimer();
                if (this.bluetoothHeadset != null) {
                    this.bluetoothAdapter.closeProfileProxy(1, this.bluetoothHeadset);
                    this.bluetoothHeadset = null;
                }
                this.bluetoothAdapter = null;
                this.bluetoothDevice = null;
                this.bluetoothState = State.UNINITIALIZED;
             //   Log.d(TAG, "stop done: BT state=" + this.bluetoothState);
            }
        }
    }

    public boolean startScoAudio() {
        ThreadUtils.checkIsOnMainThread();
      //  Log.d(TAG, "startSco: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + isScoOn());
        if (this.scoConnectionAttempts >= 2) {
           // Log.e(TAG, "BT SCO connection fails - no more attempts");
            return false;
        } else if (this.bluetoothState != State.HEADSET_AVAILABLE) {
           // Log.e(TAG, "BT SCO connection fails - no headset available");
            return false;
        } else {
           // Log.d(TAG, "Starting Bluetooth SCO and waits for ACTION_AUDIO_STATE_CHANGED...");
            this.bluetoothState = State.SCO_CONNECTING;
            this.audioManager.startBluetoothSco();
            this.audioManager.setBluetoothScoOn(true);
            this.scoConnectionAttempts++;
            startTimer();
           // Log.d(TAG, "startScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
            return true;
        }
    }

    public void stopScoAudio() {
        ThreadUtils.checkIsOnMainThread();
       // Log.d(TAG, "stopScoAudio: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
        if (this.bluetoothState == State.SCO_CONNECTING || this.bluetoothState == State.SCO_CONNECTED) {
            cancelTimer();
            this.audioManager.stopBluetoothSco();
            this.audioManager.setBluetoothScoOn(false);
            this.bluetoothState = State.SCO_DISCONNECTING;
          //  Log.d(TAG, "stopScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
        }
    }

    public void updateDevice() {
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
          //  Log.d(TAG, "updateDevice");
            List<BluetoothDevice> connectedDevices = this.bluetoothHeadset.getConnectedDevices();
            if (connectedDevices.isEmpty()) {
                this.bluetoothDevice = null;
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
               // Log.d(TAG, "No connected bluetooth headset");
            } else {
                this.bluetoothDevice = connectedDevices.get(0);
                this.bluetoothState = State.HEADSET_AVAILABLE;
              //  Log.d(TAG, "Connected bluetooth headset: name=" + this.bluetoothDevice.getName() + ", state=" + stateToString(this.bluetoothHeadset.getConnectionState(this.bluetoothDevice)) + ", SCO audio=" + this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice));
            }
           // Log.d(TAG, "updateDevice done: BT state=" + this.bluetoothState);
        }
    }

    @Nullable
    protected AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    protected void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        this.apprtcContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    protected void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        this.apprtcContext.unregisterReceiver(broadcastReceiver);
    }

    protected boolean getBluetoothProfileProxy(Context context, BluetoothProfile.ServiceListener serviceListener, int i) {
        return this.bluetoothAdapter.getProfileProxy(context, serviceListener, i);
    }

    protected boolean hasPermission(Context context, String str) {
        return this.apprtcContext.checkPermission(str, Process.myPid(), Process.myUid()) == 0;
    }

    @SuppressLint({"HardwareIds"})
    protected void logBluetoothAdapterInfo(BluetoothAdapter bluetoothAdapter) {
       // Log.d(TAG, "BluetoothAdapter: enabled=" + bluetoothAdapter.isEnabled() + ", state=" + stateToString(bluetoothAdapter.getState()) + ", name=" + bluetoothAdapter.getName() + ", address=" + bluetoothAdapter.getAddress());
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (!bondedDevices.isEmpty()) {
           // Log.d(TAG, "paired devices:");
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
              //  Log.d(TAG, " name=" + bluetoothDevice.getName() + ", address=" + bluetoothDevice.getAddress());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAudioDeviceState() {
        ThreadUtils.checkIsOnMainThread();
       // Log.d(TAG, "updateAudioDeviceState");
        this.apprtcAudioManager.updateAudioDeviceState();
    }

    private void startTimer() {
        ThreadUtils.checkIsOnMainThread();
       // Log.d(TAG, "startTimer");
        this.handler.postDelayed(this.bluetoothTimeoutRunnable, 4000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelTimer() {
        ThreadUtils.checkIsOnMainThread();
      //  Log.d(TAG, "cancelTimer");
        this.handler.removeCallbacks(this.bluetoothTimeoutRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:17:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x00a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void bluetoothTimeout() {
        boolean z;
        ThreadUtils.checkIsOnMainThread();
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
         //   Log.d(TAG, "bluetoothTimeout: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + isScoOn());
            if (this.bluetoothState == State.SCO_CONNECTING) {
                List<BluetoothDevice> connectedDevices = this.bluetoothHeadset.getConnectedDevices();
                if (connectedDevices.size() > 0) {
                    this.bluetoothDevice = connectedDevices.get(0);
                    if (this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice)) {
                      //  Log.d(TAG, "SCO connected with " + this.bluetoothDevice.getName());
                        z = true;
                        if (!z) {
                            this.bluetoothState = State.SCO_CONNECTED;
                            this.scoConnectionAttempts = 0;
                        } else {
                            Log.w(TAG, "BT failed to connect after timeout");
                            stopScoAudio();
                        }
                        updateAudioDeviceState();
                       // Log.d(TAG, "bluetoothTimeout done: BT state=" + this.bluetoothState);
                    }
                 //   Log.d(TAG, "SCO is not connected with " + this.bluetoothDevice.getName());
                }
                z = false;
                if (!z) {
                }
                updateAudioDeviceState();
             //   Log.d(TAG, "bluetoothTimeout done: BT state=" + this.bluetoothState);
            }
        }
    }

    private boolean isScoOn() {
        return this.audioManager.isBluetoothScoOn();
    }
}
