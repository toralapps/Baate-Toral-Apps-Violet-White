package com.nimychat.skybluewhite.videocall.chat.livevideocall.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;


@TargetApi(19)
public
class CpuMonitor {
    private static final int CPU_STAT_LOG_PERIOD_MS = 6000;
    private static final int CPU_STAT_SAMPLE_PERIOD_MS = 2000;
    private static final int MOVING_AVERAGE_SAMPLES = 5;
    private static final String TAG = "CpuMonitor";
    private int actualCpusPresent;
    private final Context appContext;
    private long[] cpuFreqMax;
    private boolean cpuOveruse;
    private int cpusPresent;
    private double[] curFreqScales;
    private String[] curPath;
    @Nullable
    private ScheduledExecutorService executor;
    private final MovingAverage frequencyScale;
    private boolean initialized;
    @Nullable
    private ProcStat lastProcStat;
    private long lastStatLogTimeMs;
    private String[] maxPath;
    private final MovingAverage systemCpuUsage;
    private final MovingAverage totalCpuUsage;
    private final MovingAverage userCpuUsage;

    private int doubleToPercent(double d) {
        return (int) ((d * 100.0d) + 0.5d);
    }


    public static class ProcStat {
        final long idleTime;
        final long systemTime;
        final long userTime;

        ProcStat(long j, long j2, long j3) {
            this.userTime = j;
            this.systemTime = j2;
            this.idleTime = j3;
        }
    }


    public static class MovingAverage {
        private double[] circBuffer;
        private int circBufferIndex;
        private double currentValue;
        private final int size;
        private double sum;

        public MovingAverage(int i) {
            if (i > 0) {
                this.size = i;
                this.circBuffer = new double[i];
                return;
            }
            throw new AssertionError("Size value in MovingAverage ctor should be positive.");
        }

        public void reset() {
            Arrays.fill(this.circBuffer, 0.0d);
            this.circBufferIndex = 0;
            this.sum = 0.0d;
            this.currentValue = 0.0d;
        }

        public void addValue(double d) {
            this.sum -= this.circBuffer[this.circBufferIndex];
            double[] dArr = this.circBuffer;
            int i = this.circBufferIndex;
            this.circBufferIndex = i + 1;
            dArr[i] = d;
            this.currentValue = d;
            this.sum += d;
            if (this.circBufferIndex >= this.size) {
                this.circBufferIndex = 0;
            }
        }

        public double getCurrent() {
            return this.currentValue;
        }

        public double getAverage() {
            double d = this.sum;
            double d2 = (double) this.size;
            Double.isNaN(d2);
            return d / d2;
        }
    }

    public static boolean isSupported() {
        return Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 24;
    }

    public CpuMonitor(Context context) {
        if (isSupported()) {
          //  Log.d(TAG, "CpuMonitor ctor.");
            this.appContext = context.getApplicationContext();
            this.userCpuUsage = new MovingAverage(5);
            this.systemCpuUsage = new MovingAverage(5);
            this.totalCpuUsage = new MovingAverage(5);
            this.frequencyScale = new MovingAverage(5);
            this.lastStatLogTimeMs = SystemClock.elapsedRealtime();
            scheduleCpuUtilizationTask();
            return;
        }
        throw new RuntimeException("CpuMonitor is not supported on this Android version.");
    }

    public void pause() {
        if (this.executor != null) {
            Log.d(TAG, "pause");
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    public void resume() {
        Log.d(TAG, "resume");
        resetStat();
        scheduleCpuUtilizationTask();
    }

    public synchronized void reset() {
        if (this.executor != null) {
            Log.d(TAG, "reset");
            resetStat();
            this.cpuOveruse = false;
        }
    }

    public synchronized int getCpuUsageCurrent() {
        return doubleToPercent(this.userCpuUsage.getCurrent() + this.systemCpuUsage.getCurrent());
    }

    public synchronized int getCpuUsageAverage() {
        return doubleToPercent(this.userCpuUsage.getAverage() + this.systemCpuUsage.getAverage());
    }

    public synchronized int getFrequencyScaleAverage() {
        return doubleToPercent(this.frequencyScale.getAverage());
    }

    private void scheduleCpuUtilizationTask() {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(new Runnable() { // from class: com.app.videocallrandomchat2.CpuMonitor.1
            @Override // java.lang.Runnable
            public void run() {
                CpuMonitor.this.cpuUtilizationTask();
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cpuUtilizationTask() {
        if (sampleCpuUtilization() && SystemClock.elapsedRealtime() - this.lastStatLogTimeMs >= 6000) {
            this.lastStatLogTimeMs = SystemClock.elapsedRealtime();
            Log.d(TAG, getStatString());
        }
    }

    private void init() {
        try {
            FileInputStream fileInputStream = new FileInputStream("/sys/devices/system/cpu/present");
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                try {
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    try {
                        Scanner useDelimiter = new Scanner(bufferedReader).useDelimiter("[-\n]");
                        try {
                            useDelimiter.nextInt();
                            this.cpusPresent = useDelimiter.nextInt() + 1;
                            useDelimiter.close();
                            if (useDelimiter != null) {
                                useDelimiter.close();
                            }
                            bufferedReader.close();
                            inputStreamReader.close();
                            fileInputStream.close();
                        } catch (Throwable th) {
                            if (useDelimiter != null) {
                                try {
                                    useDelimiter.close();
                                } catch (Throwable unused) {
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable unused2) {
                        }
                        throw th2;
                    }
                } catch (Throwable th3) {
                    try {
                        inputStreamReader.close();
                    } catch (Throwable unused3) {
                    }
                    throw th3;
                }
            } catch (Throwable th4) {
                try {
                    fileInputStream.close();
                } catch (Throwable unused4) {
                }
                throw th4;
            }
        } catch (FileNotFoundException unused5) {
           // Log.e(TAG, "Cannot do CPU stats since /sys/devices/system/cpu/present is missing");
        } catch (IOException unused6) {
          //  Log.e(TAG, "Error closing file");
        } catch (Exception unused7) {
           // Log.e(TAG, "Cannot do CPU stats due to /sys/devices/system/cpu/present parsing problem");
        }
        this.cpuFreqMax = new long[this.cpusPresent];
        this.maxPath = new String[this.cpusPresent];
        this.curPath = new String[this.cpusPresent];
        this.curFreqScales = new double[this.cpusPresent];
        for (int i = 0; i < this.cpusPresent; i++) {
            this.cpuFreqMax[i] = 0;
            this.curFreqScales[i] = 0.0d;
            String[] strArr = this.maxPath;
            strArr[i] = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq";
            String[] strArr2 = this.curPath;
            strArr2[i] = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq";
        }
        this.lastProcStat = new ProcStat(0, 0, 0);
        resetStat();
        this.initialized = true;
    }

    private synchronized void resetStat() {
        this.userCpuUsage.reset();
        this.systemCpuUsage.reset();
        this.totalCpuUsage.reset();
        this.frequencyScale.reset();
        this.lastStatLogTimeMs = SystemClock.elapsedRealtime();
    }

    private int getBatteryLevel() {
        Intent registerReceiver = this.appContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int intExtra = registerReceiver.getIntExtra("scale", 100);
        if (intExtra > 0) {
            return (int) ((((float) registerReceiver.getIntExtra("level", 0)) * 100.0f) / ((float) intExtra));
        }
        return 0;
    }

    private synchronized boolean sampleCpuUtilization() {
        if (!this.initialized) {
            init();
        }
        if (this.cpusPresent == 0) {
            return false;
        }
        this.actualCpusPresent = 0;
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        for (int i = 0; i < this.cpusPresent; i++) {
            this.curFreqScales[i] = 0.0d;
            if (this.cpuFreqMax[i] == 0) {
                long readFreqFromFile = readFreqFromFile(this.maxPath[i]);
                if (readFreqFromFile > 0) {
                    Log.d(TAG, "Core " + i + ". Max frequency: " + readFreqFromFile);
                    this.cpuFreqMax[i] = readFreqFromFile;
                    this.maxPath[i] = null;
                    j3 = readFreqFromFile;
                }
            } else {
                j3 = this.cpuFreqMax[i];
            }
            long readFreqFromFile2 = readFreqFromFile(this.curPath[i]);
            if (readFreqFromFile2 != 0 || j3 != 0) {
                if (readFreqFromFile2 > 0) {
                    this.actualCpusPresent++;
                }
                j += readFreqFromFile2;
                j2 += j3;
                if (j3 > 0) {
                    double[] dArr = this.curFreqScales;
                    double d = (double) readFreqFromFile2;
                    double d2 = (double) j3;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    dArr[i] = d / d2;
                }
            }
        }
        if (j == 0 || j2 == 0) {
           // Log.e(TAG, "Could not read max or current frequency for any CPU");
            return false;
        }
        double d3 = (double) j;
        double d4 = (double) j2;
        Double.isNaN(d3);
        Double.isNaN(d4);
        double d5 = d3 / d4;
        if (this.frequencyScale.getCurrent() > 0.0d) {
            d5 = 0.5d * (this.frequencyScale.getCurrent() + d5);
        }
        ProcStat readProcStat = readProcStat();
        if (readProcStat == null) {
            return false;
        }
        long j4 = readProcStat.userTime - this.lastProcStat.userTime;
        long j5 = readProcStat.systemTime - this.lastProcStat.systemTime;
        long j6 = j4 + j5 + (readProcStat.idleTime - this.lastProcStat.idleTime);
        if (!(d5 == 0.0d || j6 == 0)) {
            this.frequencyScale.addValue(d5);
            double d6 = (double) j4;
            double d7 = (double) j6;
            Double.isNaN(d6);
            Double.isNaN(d7);
            double d8 = d6 / d7;
            this.userCpuUsage.addValue(d8);
            double d9 = (double) j5;
            Double.isNaN(d9);
            Double.isNaN(d7);
            double d10 = d9 / d7;
            this.systemCpuUsage.addValue(d10);
            this.totalCpuUsage.addValue((d8 + d10) * d5);
            this.lastProcStat = readProcStat;
            return true;
        }
        return false;
    }

    private synchronized String getStatString() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("CPU User: ");
        sb.append(doubleToPercent(this.userCpuUsage.getCurrent()));
        sb.append("/");
        sb.append(doubleToPercent(this.userCpuUsage.getAverage()));
        sb.append(". System: ");
        sb.append(doubleToPercent(this.systemCpuUsage.getCurrent()));
        sb.append("/");
        sb.append(doubleToPercent(this.systemCpuUsage.getAverage()));
        sb.append(". Freq: ");
        sb.append(doubleToPercent(this.frequencyScale.getCurrent()));
        sb.append("/");
        sb.append(doubleToPercent(this.frequencyScale.getAverage()));
        sb.append(". Total usage: ");
        sb.append(doubleToPercent(this.totalCpuUsage.getCurrent()));
        sb.append("/");
        sb.append(doubleToPercent(this.totalCpuUsage.getAverage()));
        sb.append(". Cores: ");
        sb.append(this.actualCpusPresent);
        sb.append("( ");
        for (int i = 0; i < this.cpusPresent; i++) {
            sb.append(doubleToPercent(this.curFreqScales[i]));
            sb.append(" ");
        }
        sb.append("). Battery: ");
        sb.append(getBatteryLevel());
        if (this.cpuOveruse) {
            sb.append(". Overuse.");
        }
        return sb.toString();
    }

    private long readFreqFromFile(String str) {
        Throwable th;
        InputStreamReader inputStreamReader = null;
        Throwable th2;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            try {
                inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            } catch (Throwable th3) {
                th = th3;
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    long parseLong = parseLong(bufferedReader.readLine());
                    try {
                        try {
                            bufferedReader.close();
                            try {
                                inputStreamReader.close();
                                fileInputStream.close();
                                return parseLong;
                            } catch (Throwable th4) {
                                th = th4;
                                try {
                                    fileInputStream.close();
                                } catch (Throwable unused) {
                                }
                                throw th;
                            }
                        } catch (Throwable th5) {
                            th2 = th5;
                            try {
                                inputStreamReader.close();
                            } catch (Throwable unused2) {
                            }
                            throw th2;
                        }
                    } catch (IOException unused3) {
                        return parseLong;
                    }
                } catch (Throwable th6) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable unused4) {
                    }
                    throw th6;
                }
            } catch (Throwable th7) {
                th2 = th7;
            }
        } catch (IOException unused5) {
            return 0;
        }
        return 0;
    }

    private static long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
           // Log.e(TAG, "parseLong error.", e);
            return 0;
        }
    }

    @Nullable
    private ProcStat readProcStat() {
        long j;
        long j2;
        try {
            FileInputStream fileInputStream = new FileInputStream("/proc/stat");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            try {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    String[] split = bufferedReader.readLine().split("\\s+");
                    int length = split.length;
                    long j3 = 0;
                    if (length >= 5) {
                        j3 = parseLong(split[1]) + parseLong(split[2]);
                        j2 = parseLong(split[3]);
                        j = parseLong(split[4]);
                    } else {
                        j2 = 0;
                        j = 0;
                    }
                    if (length >= 8) {
                        j3 += parseLong(split[5]);
                        j2 = j2 + parseLong(split[6]) + parseLong(split[7]);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    fileInputStream.close();
                    return new ProcStat(j3, j2, j);
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable unused) {
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                try {
                    inputStreamReader.close();
                } catch (Throwable unused2) {
                }
                throw th2;
            }
        } catch (FileNotFoundException e) {
          //  Log.e(TAG, "Cannot open /proc/stat for reading", e);
            return null;
        } catch (Exception e2) {
          //  Log.e(TAG, "Problems parsing /proc/stat", e2);
            return null;
        }
    }
}
