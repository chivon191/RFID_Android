package com.example.rfif_android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class NotificationBackgroundService extends Service {

    private static final String TAG = "NotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");

        // 👉 Chỉ gọi gắn listener 1 lần
        NotificationApp app = (NotificationApp) getApplication();
        app.startListeningToFailCounts();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service is running");
        return START_STICKY; // Service sẽ tiếp tục chạy sau khi bị hệ thống dừng
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");


    }
}