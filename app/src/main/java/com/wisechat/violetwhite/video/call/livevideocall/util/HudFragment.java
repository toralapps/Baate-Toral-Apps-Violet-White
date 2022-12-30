package com.wisechat.violetwhite.video.call.livevideocall.util;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wisechat.violetwhite.video.call.R;
import com.wisechat.violetwhite.video.call.livevideocall.activity.CallLiveActivity;

import org.webrtc.StatsReport;

import java.util.HashMap;
import java.util.Map;

public class HudFragment extends Fragment {
    private CpuMonitor cpuMonitor;
    private boolean displayHud;
    private TextView encoderStatView;
    private TextView hudViewBwe;
    private TextView hudViewConnection;
    private TextView hudViewVideoRecv;
    private TextView hudViewVideoSend;
    private volatile boolean isRunning;
    private ImageButton toggleDebugButton;
    private boolean videoCallEnabled;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_hud, viewGroup, false);
        this.encoderStatView = (TextView) inflate.findViewById(R.id.encoder_stat_call);
        this.hudViewBwe = (TextView) inflate.findViewById(R.id.hud_stat_bwe);
        this.hudViewConnection = (TextView) inflate.findViewById(R.id.hud_stat_connection);
        this.hudViewVideoSend = (TextView) inflate.findViewById(R.id.hud_stat_video_send);
        this.hudViewVideoRecv = (TextView) inflate.findViewById(R.id.hud_stat_video_recv);
        this.toggleDebugButton = (ImageButton) inflate.findViewById(R.id.button_toggle_debug);
        this.toggleDebugButton.setOnClickListener(new View.OnClickListener() { // from class: com.app.videocallrandomchat2.HudFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (HudFragment.this.displayHud) {
                    HudFragment.this.hudViewsSetProperties(HudFragment.this.hudViewBwe.getVisibility() == 0 ? 4 : 0);
                }
            }
        });
        return inflate;
    }

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        int i = 0;
        if (arguments != null) {
            this.videoCallEnabled = arguments.getBoolean(CallLiveActivity.EXTRA_VIDEO_CALL, true);
            this.displayHud = arguments.getBoolean(CallLiveActivity.EXTRA_DISPLAY_HUD, false);
        }
        if (!this.displayHud) {
            i = 4;
        }
        this.encoderStatView.setVisibility(i);
        this.toggleDebugButton.setVisibility(i);
        hudViewsSetProperties(4);
        this.isRunning = true;
    }

    @Override // android.app.Fragment
    public void onStop() {
        this.isRunning = false;
        super.onStop();
    }

    public void setCpuMonitor(CpuMonitor cpuMonitor) {
        this.cpuMonitor = cpuMonitor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hudViewsSetProperties(int i) {
        this.hudViewBwe.setVisibility(i);
        this.hudViewConnection.setVisibility(i);
        this.hudViewVideoSend.setVisibility(i);
        this.hudViewVideoRecv.setVisibility(i);
        this.hudViewBwe.setTextSize(3, 5.0f);
        this.hudViewConnection.setTextSize(3, 5.0f);
        this.hudViewVideoSend.setTextSize(3, 5.0f);
        this.hudViewVideoRecv.setTextSize(3, 5.0f);
    }

    private Map<String, String> getReportMap(StatsReport statsReport) {
        HashMap hashMap = new HashMap();
        StatsReport.Value[] valueArr = statsReport.values;
        for (StatsReport.Value value : valueArr) {
            hashMap.put(value.name, value.value);
        }
        return hashMap;
    }

    public void updateEncoderStatistics(StatsReport[] statsReportArr) {
        int i;
        String str;
        StatsReport[] statsReportArr2 = statsReportArr;
        if (this.isRunning && this.displayHud) {
            StringBuilder sb = new StringBuilder(128);
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            StringBuilder sb4 = new StringBuilder();
            StringBuilder sb5 = new StringBuilder();
            int length = statsReportArr2.length;
            String str2 = null;
            String str3 = null;
            String str4 = null;
            int i2 = 0;
            while (i2 < length) {
                StatsReport statsReport = statsReportArr2[i2];
                if (!statsReport.type.equals("ssrc") || !statsReport.id.contains("ssrc") || !statsReport.id.contains("send")) {
                    i = length;
                    if (!statsReport.type.equals("ssrc") || !statsReport.id.contains("ssrc") || !statsReport.id.contains("recv")) {
                        if (statsReport.id.equals("bweforvideo")) {
                            Map<String, String> reportMap = getReportMap(statsReport);
                            String str5 = reportMap.get("googTargetEncBitrate");
                            String str6 = reportMap.get("googActualEncBitrate");
                            sb2.append(statsReport.id);
                            sb2.append("\n");
                            StatsReport.Value[] valueArr = statsReport.values;
                            int length2 = valueArr.length;
                            int i3 = 0;
                            while (i3 < length2) {
                                StatsReport.Value value = valueArr[i3];
                                sb2.append(value.name.replace("goog", "").replace("Available", ""));
                                sb2.append("=");
                                sb2.append(value.value);
                                sb2.append("\n");
                                i3++;
                                str6 = str6;
                            }
                            str3 = str5;
                            str4 = str6;
                        } else if (statsReport.type.equals("googCandidatePair") && (str = getReportMap(statsReport).get("googActiveConnection")) != null && str.equals("true")) {
                            sb3.append(statsReport.id);
                            sb3.append("\n");
                            StatsReport.Value[] valueArr2 = statsReport.values;
                            int length3 = valueArr2.length;
                            int i4 = 0;
                            while (i4 < length3) {
                                StatsReport.Value value2 = valueArr2[i4];
                                sb3.append(value2.name.replace("goog", ""));
                                sb3.append("=");
                                sb3.append(value2.value);
                                sb3.append("\n");
                                i4++;
                                valueArr2 = valueArr2;
                            }
                        }
                    } else if (getReportMap(statsReport).get("googFrameWidthReceived") != null) {
                        sb5.append(statsReport.id);
                        sb5.append("\n");
                        StatsReport.Value[] valueArr3 = statsReport.values;
                        int length4 = valueArr3.length;
                        int i5 = 0;
                        while (i5 < length4) {
                            StatsReport.Value value3 = valueArr3[i5];
                            sb5.append(value3.name.replace("goog", ""));
                            sb5.append("=");
                            sb5.append(value3.value);
                            sb5.append("\n");
                            i5++;
                            valueArr3 = valueArr3;
                        }
                    }
                } else {
                    Map<String, String> reportMap2 = getReportMap(statsReport);
                    String str7 = reportMap2.get("googTrackId");
                    if (str7 != null && str7.contains(PeerConnectionClient.VIDEO_TRACK_ID)) {
                        str2 = reportMap2.get("googFrameRateSent");
                        sb4.append(statsReport.id);
                        sb4.append("\n");
                        StatsReport.Value[] valueArr4 = statsReport.values;
                        int length5 = valueArr4.length;
                        int i6 = 0;
                        while (i6 < length5) {
                            StatsReport.Value value4 = valueArr4[i6];
                            sb4.append(value4.name.replace("goog", ""));
                            sb4.append("=");
                            sb4.append(value4.value);
                            sb4.append("\n");
                            i6++;
                            length = length;
                            valueArr4 = valueArr4;
                        }
                    }
                    i = length;
                }
                i2++;
                length = i;
                statsReportArr2 = statsReportArr;
            }
            this.hudViewBwe.setText(sb2.toString());
            this.hudViewConnection.setText(sb3.toString());
            this.hudViewVideoSend.setText(sb4.toString());
            this.hudViewVideoRecv.setText(sb5.toString());
            if (this.videoCallEnabled) {
                if (str2 != null) {
                    sb.append("Fps:  ");
                    sb.append(str2);
                    sb.append("\n");
                }
                if (str3 != null) {
                    sb.append("Target BR: ");
                    sb.append(str3);
                    sb.append("\n");
                }
                if (str4 != null) {
                    sb.append("Actual BR: ");
                    sb.append(str4);
                    sb.append("\n");
                }
            }
            if (this.cpuMonitor != null) {
                sb.append("CPU%: ");
                sb.append(this.cpuMonitor.getCpuUsageCurrent());
                sb.append("/");
                sb.append(this.cpuMonitor.getCpuUsageAverage());
                sb.append(". Freq: ");
                sb.append(this.cpuMonitor.getFrequencyScaleAverage());
            }
            this.encoderStatView.setText(sb.toString());
        }
    }
}
