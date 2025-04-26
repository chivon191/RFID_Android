package com.example.rfif_android;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class homeFragment extends Fragment {

    private Switch switch_door, switch_buzzer;
    private DatabaseReference controlRef;
    private ValueEventListener commandListener;

    public homeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch_door = view.findViewById(R.id.switch_door);
        switch_buzzer = view.findViewById(R.id.switch_buzzer);
        controlRef = FirebaseDatabase.getInstance().getReference("nfc_control");

        switch_door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    controlRef.child("command").setValue("open_door");
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Đã gửi tín hiệu mở khóa cửa!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        switch_buzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    controlRef.child("command").setValue("open_warning");
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Đã gửi tín hiệu bật còi warning!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        commandListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                String command = snapshot.getValue(String.class);
                if (command == null || command.equals("none")) return;

                switch (command) {
                    case "close_door":
                        switch_door.setChecked(false); // tự động tắt switch
                        Toast.makeText(requireContext(), "Cửa đã đóng!", Toast.LENGTH_SHORT).show();
                        controlRef.child("command").setValue("none");
                        break;
                    case "close_warning":
                        switch_buzzer.setChecked(false);
                        Toast.makeText(requireContext(), "Còi cảnh báo đã tắt!", Toast.LENGTH_SHORT).show();
                        controlRef.child("command").setValue("none");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi đọc Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        controlRef.child("command").addValueEventListener(commandListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (commandListener != null) {
            controlRef.child("command").removeEventListener(commandListener);
        }
    }
}
