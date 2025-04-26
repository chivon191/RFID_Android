package com.example.rfif_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingNotiFragment extends Fragment {
    private Switch swthongbao, swrung;
    private CheckBox cbcanhbao, cbnhacnho;
    private TextView edttong, edtcanhbao, edtnhacnho, txt;
    private Button btnsave;

    private int alertCount = 0;
    private int reminderCount = 0;

    private DatabaseReference databaseReference;

    public SettingNotiFragment() {
        super(R.layout.fragment_setting_noti);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swthongbao = view.findViewById(R.id.notification_switch);
        swrung = view.findViewById(R.id.vibration_switch);
        cbcanhbao = view.findViewById(R.id.alert_checkbox);
        cbnhacnho = view.findViewById(R.id.reminder_checkbox);
        edttong = view.findViewById(R.id.total_notifications);
        edtcanhbao = view.findViewById(R.id.alert_notifications);
        edtnhacnho = view.findViewById(R.id.reminder_notifications);
        btnsave = view.findViewById(R.id.update_stats_button);
        txt = view.findViewById(R.id.txt);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        swthongbao.setChecked(sharedPreferences.getBoolean("notificationEnabled", false));
        swrung.setChecked(sharedPreferences.getBoolean("vibrationEnabled", false));
        cbcanhbao.setChecked(sharedPreferences.getBoolean("alertEnabled", false));
        cbnhacnho.setChecked(sharedPreferences.getBoolean("reminderEnabled", false));
        updateTextViews();
        NotificationApp app = (NotificationApp) getActivity().getApplication();
        app.setNotificationCallback(new NotificationApp.NotificationCallback() {
            @Override
            public void onAlertTriggered() {
                incrementAlertCount();
                updateTextViews();
            }
            @Override
            public void onReminderTriggered() {
                incrementReminderCount();
                updateTextViews();
            }
        });
        swthongbao.setOnCheckedChangeListener((buttonView, isChecked) -> {
            swrung.setEnabled(isChecked);
            cbcanhbao.setEnabled(isChecked);
            cbnhacnho.setEnabled(isChecked);
            btnsave.setEnabled(isChecked);
            if (isChecked) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("count");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String count = snapshot.getValue(String.class);
                        txt.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        txt.setText("Lỗi khi đọc dữ liệu");
                    }
                });
            } else {
                txt.setText("Tắt Switch");
                swrung.setChecked(false);
                swrung.setEnabled(false);
                cbcanhbao.setChecked(false);
                cbcanhbao.setEnabled(false);
                cbnhacnho.setEnabled(false);
                cbnhacnho.setChecked(false);
            }
        });

        btnsave.setOnClickListener(view1 -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationEnabled", swthongbao.isChecked());
            editor.putBoolean("vibrationEnabled", swrung.isChecked());
            editor.putBoolean("alertEnabled", cbcanhbao.isChecked());
            editor.putBoolean("reminderEnabled", cbnhacnho.isChecked());
            editor.apply();


            Toast.makeText(getContext(), "Cài đặt đã được lưu!", Toast.LENGTH_SHORT).show();

            Intent serviceIntent = new Intent(getContext(), NotificationBackgroundService.class);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("count");

            if (swthongbao.isChecked()) {
                getContext().startService(serviceIntent);
//                databaseReference.setValue(0);
            } else {
                getContext().stopService(serviceIntent);
//                databaseReference.setValue(0);
            }
        });
    }

    private void incrementAlertCount() {
        alertCount++;
    }

    private void incrementReminderCount() {
        reminderCount++;
    }

    private void updateTextViews() {
        edtcanhbao.setText("Cảnh báo RFID không hợp lệ " + alertCount);
        edtnhacnho.setText("Nhắc nhở RFID " + reminderCount);
        edttong.setText("Tổng thông báo: " + (alertCount + reminderCount));
    }
}