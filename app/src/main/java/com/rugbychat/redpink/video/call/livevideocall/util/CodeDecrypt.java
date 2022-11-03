package com.rugbychat.redpink.video.call.livevideocall.util;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class CodeDecrypt {

    public byte[] encrypt(String str, @NonNull String str2) {
        int length = str2.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str2.charAt(i), 16) << 4) + Character.digit(str2.charAt(i + 1), 16));
        }
        Log.d("DEEPArr", Arrays.toString(bArr));
        return bArr;
    }

}
