package com.example.rfif_android;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationApp extends Application {
    public interface NotificationCallback {
        void onAlertTriggered();      // Khi cảnh báo được kích hoạt
        void onReminderTriggered();   // Khi nhắc nhở được kích hoạt
    }

    private NotificationCallback callback;
    private boolean isListening = false; // Biến chặn add listener nhiều lần
    private int lastCount = -1; // Biến kiểm tra để tránh lặp lại thông báo

    public void setNotificationCallback(NotificationCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void startListeningToFailCounts() {
        if (isListening) return;
        isListening = true;

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        final boolean isVibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", false);
        final boolean isNotificationEnabled = sharedPreferences.getBoolean("notificationEnabled", false);
        final boolean isAlertEnabled = sharedPreferences.getBoolean("alertEnabled", false);

        if (!isNotificationEnabled) return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("count");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String countFail = snapshot.getValue(String.class);
                if (countFail.equals("2")) {
                    playNotificationSound("Bạn đã nhập sai 2 lần. Hãy cẩn thận!");
                    if (callback != null) callback.onReminderTriggered();
                } else if (countFail .equals("3") ) {
                    playNotificationSound("Cảnh báo: Đăng nhập sai nhiều lần!");
                    vibrateDevice();
                    if (callback != null) callback.onAlertTriggered();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationApp", "Lỗi khi lắng nghe cập nhật count", error.toException());
            }
        });
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    private void playNotificationSound(String message) {
        String channelId = "notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Chỉ tạo channel nếu chưa tồn tại
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Thông báo RFID",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh thông báo RFID cho ứng dụng");
            notificationManager.createNotificationChannel(channel);
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.app.Notification notification = new android.app.Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle("Hệ thống RFID")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setSound(soundUri)
                .setPriority(android.app.Notification.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }
}
