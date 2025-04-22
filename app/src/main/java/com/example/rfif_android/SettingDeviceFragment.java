package com.example.rfif_android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingDeviceFragment extends Fragment {
    private EditText etCurrentPin, etNewPin;
    private Button btnChangePin;
    private DatabaseReference passwordRef;

    public SettingDeviceFragment() {
        super(R.layout.fragment_setting_device);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCurrentPin = view.findViewById(R.id.etCurrentPin);
        etNewPin = view.findViewById(R.id.etNewPin);
        btnChangePin = view.findViewById(R.id.btnChangePin);
        passwordRef = FirebaseDatabase.getInstance().getReference("password");

        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Vô hiệu hóa nút để tránh nhấn nhiều lần
                btnChangePin.setEnabled(false);

                String curPin = etCurrentPin.getText().toString().trim();
                String newPin = etNewPin.getText().toString().trim();

                // Kiểm tra trường rỗng
                if (curPin.isEmpty() || newPin.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
                    btnChangePin.setEnabled(true);
                    return;
                }

                // Kiểm tra định dạng PIN mới (ví dụ: chỉ số, độ dài 4-6 chữ số)
                if (!newPin.matches("\\d{4,6}")) {
                    Toast.makeText(requireContext(), "Mật khẩu mới phải là số và có 4-6 chữ số", Toast.LENGTH_SHORT).show();
                    btnChangePin.setEnabled(true);
                    return;
                }

                // Truy vấn dữ liệu từ Firebase
                passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firebasePassword = snapshot.getValue(String.class);

                        // Kiểm tra xem password có tồn tại không
                        if (firebasePassword == null) {
                            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy mật khẩu trong Firebase", Toast.LENGTH_SHORT).show();
                            btnChangePin.setEnabled(true);
                            return;
                        }

                        // So sánh dữ liệu
                        if (firebasePassword.equals(curPin)) {
                            // Thay thế giá trị password bằng newPin
                            passwordRef.setValue(newPin, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference ref) {
                                    if (!isAdded()) {
                                        btnChangePin.setEnabled(true);
                                        return;
                                    }
                                    if (databaseError == null) {
                                        Toast.makeText(getContext(), "Cập nhật mật khẩu mới thành công!", Toast.LENGTH_SHORT).show();
                                        etCurrentPin.setText("");
                                        etNewPin.setText("");
                                    } else {
                                        Toast.makeText(getContext(), "Lỗi khi cập nhật: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    btnChangePin.setEnabled(true);
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Thất bại: Mật khẩu hiện tại không khớp!", Toast.LENGTH_SHORT).show();
                            btnChangePin.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        btnChangePin.setEnabled(true);
                    }
                });
            }
        });
    }
}