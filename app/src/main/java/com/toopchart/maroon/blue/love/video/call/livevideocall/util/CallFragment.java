package com.toopchart.maroon.blue.love.video.call.livevideocall.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toopchart.maroon.blue.love.video.call.R;
import com.toopchart.maroon.blue.love.video.call.livevideocall.activity.CallLiveActivity;

import org.webrtc.RendererCommon;


public class CallFragment extends Fragment {
    private OnCallEvents callEvents;
    private ImageButton cameraSwitchButton;
    private SeekBar captureFormatSlider;
    private TextView captureFormatText;
    private TextView contactView;
    private RendererCommon.ScalingType scalingType;
    private ImageButton toggleMuteButton;
    private boolean videoCallEnabled = true;
    private ImageButton videoScalingButton;
    ImageView privacy;

    public interface OnCallEvents {
        void onCallHangUp();

        void onCameraSwitch();

        void onCaptureFormatChange(int i, int i2, int i3);

        boolean onToggleMic();

        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_call, viewGroup, false);
        this.contactView = (TextView) inflate.findViewById(R.id.contact_name_call);
        this.cameraSwitchButton = (ImageButton) inflate.findViewById(R.id.button_call_switch_camera);
        this.videoScalingButton = (ImageButton) inflate.findViewById(R.id.button_call_scaling_mode);
        this.toggleMuteButton = (ImageButton) inflate.findViewById(R.id.button_call_toggle_mic);
        this.captureFormatText = (TextView) inflate.findViewById(R.id.capture_format_text_call);
        this.captureFormatSlider = (SeekBar) inflate.findViewById(R.id.capture_format_slider_call);
        this.privacy = inflate.findViewById(R.id.privacyy_short_video);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity(), R.style.Transparent);
                dialog.setContentView(R.layout.report_dailog);
                dialog.findViewById(R.id.reno).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (dialog.isShowing()) {

                            dialog.dismiss();
                        }

                    }
                });
                dialog.findViewById(R.id.reyes).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "User Blocked", Toast.LENGTH_SHORT).show();

                        CallFragment.this.callEvents.onCallHangUp();
                        getActivity().finish();
                    }
                });
                dialog.show();
            }
        });
        ((ImageButton) inflate.findViewById(R.id.button_call_disconnect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallFragment.this.callEvents.onCallHangUp();
            }
        });
        this.cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallFragment.this.callEvents.onCameraSwitch();
            }
        });
        this.videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CallFragment.this.scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {
                    CallFragment.this.videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
                    CallFragment.this.scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                } else {
                    CallFragment.this.videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
                    CallFragment.this.scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
                }
                CallFragment.this.callEvents.onVideoScalingSwitch(CallFragment.this.scalingType);
            }
        });
        this.scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        this.toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallFragment.this.toggleMuteButton.setAlpha(CallFragment.this.callEvents.onToggleMic() ? 1.0f : 0.3f);
            }
        });
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        boolean z = false;
        if (arguments != null) {
            this.contactView.setText(arguments.getString(CallLiveActivity.EXTRA_ROOMID));
            this.videoCallEnabled = arguments.getBoolean(CallLiveActivity.EXTRA_VIDEO_CALL, true);
            if (this.videoCallEnabled && arguments.getBoolean(CallLiveActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false)) {
                z = true;
            }
        }
        if (!this.videoCallEnabled) {
            this.cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
        if (z) {
            this.captureFormatSlider.setOnSeekBarChangeListener(new CaptureQualityController(this.captureFormatText, this.callEvents));
            return;
        }
        this.captureFormatText.setVisibility(View.GONE);
        this.captureFormatSlider.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callEvents = (OnCallEvents) activity;
    }

    public void updateEncoderStatistics(String str) {
        this.contactView.setText(str);
        str.equals("Connected");
        str.equals("Disconnected");
    }
}
