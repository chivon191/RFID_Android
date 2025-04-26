package com.example.rfif_android;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rfif_android.models.AccessCard;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class cardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView tv_getid, tv_cardid;
    private Button btn_save;
    private EditText edt_cardname;
    private List<AccessCard> cardList;
    private AdapterListCard adapterListCard;
    private DatabaseReference databaseReference, controlRef, idsRef;

    public cardFragment() {}

    public static cardFragment newInstance(String param1, String param2) {
        cardFragment fragment = new cardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        btn_save = view.findViewById(R.id.btn_save);
        tv_getid = view.findViewById(R.id.tv_get_id);
        tv_cardid = view.findViewById(R.id.tv_card_id);
        edt_cardname = view.findViewById(R.id.edt_card_name);

        ListView listView = view.findViewById(R.id.listcard);

        cardList = new ArrayList<>();
        adapterListCard = new AdapterListCard(requireContext(), cardList);
        listView.setAdapter(adapterListCard);

        databaseReference = FirebaseDatabase.getInstance().getReference("access_card");
        controlRef = FirebaseDatabase.getInstance().getReference("nfc_control");
        idsRef = FirebaseDatabase.getInstance().getReference("nfc_ids");

//        tv_getid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                controlRef.child("command").setValue("read_nfc").addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        tv_cardid.setText("NFC ID: Waiting...");
//                    } else {
//                        tv_cardid.setText("Error: Failed to send command");
//                    }
//                });
//                Toast.makeText(requireContext(), "Send message to hardware for getID", Toast.LENGTH_SHORT).show();
//            }
//        });

//        idsRef.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String nfcID = snapshot.getValue(String.class);
//                    if (nfcID != null) {
//                        tv_cardid.setText(nfcID);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                tv_cardid.setText("Error: " + databaseError.getMessage());
//            }
//        });

        tv_getid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlRef.child("command").setValue("read_nfc").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tv_cardid.setText("NFC ID: Waiting...");
                        Toast.makeText(requireContext(), "Sent command, waiting for NFC ID...", Toast.LENGTH_SHORT).show();

                        // Bây giờ bắt đầu lắng nghe NFC ID trả về
                        idsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String nfcId = snapshot.getValue(String.class);
                                if (nfcId != null && !nfcId.isEmpty()) {
                                    tv_cardid.setText(nfcId);
                                } else {
                                    tv_cardid.setText("NFC ID: Not Found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                tv_cardid.setText("Error: " + error.getMessage());
                            }
                        });

                    } else {
                        tv_cardid.setText("Error: Failed to send command");
                        Toast.makeText(requireContext(), "Failed to send command", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = tv_cardid.getText().toString().trim();
                String name = edt_cardname.getText().toString().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    Toast.makeText(requireContext(), "ID or Name is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Xác nhận lưu")
                            .setMessage("Bạn có chắc chắn muốn lưu thẻ này?")
                            .setPositiveButton("Lưu", (dialog, which) -> {
                                DatabaseReference newCard = databaseReference.push();
                                newCard.child("id").setValue(id);
                                newCard.child("name").setValue(name);
                                tv_cardid.setText("");
                                edt_cardname.setText("");
                                controlRef.child("command").setValue("update_card");
                                Toast.makeText(requireContext(), "Đã lưu", Toast.LENGTH_LONG).show();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialogItem(i);
                return false;
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccessCard card = snapshot.getValue(AccessCard.class);
                    if (card != null) {
                        cardList.add(card);
                    }
                }
                adapterListCard.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu", databaseError.toException());
            }
        });
        
        return view;
    }

    private void showDialogItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose option").setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    editItem(position);
                } else deleteItem(position);
            }
        }).setNegativeButton("Cancel", null).show();
    }

    private void editItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Item");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        builder.setView(dialogView);

        EditText editTextID = dialogView.findViewById(R.id.edittext_edit_id);
        EditText editTextName = dialogView.findViewById(R.id.edittext_edit_name);

        AccessCard currentItem = cardList.get(position);
        editTextID.setText(currentItem.getId());
        editTextName.setText(currentItem.getName());

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newIdStr = editTextID.getText().toString().trim();
            String newName = editTextName.getText().toString().trim();

            if (!newIdStr.isEmpty() && !newName.isEmpty()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận chỉnh sửa")
                        .setMessage("Bạn có chắc chắn muốn cập nhật thẻ này?")
                        .setPositiveButton("Cập nhật", (confirmDialog, which) -> {
                            getItemKeyById(currentItem.getId(), new OnKeyRetrievedListener() {
                                @Override
                                public void onKeyRetrieved(String key) {
                                    if (key != null) {
                                        DatabaseReference itemRef = databaseReference.child(key);
                                        itemRef.child("id").setValue(newIdStr);
                                        itemRef.child("name").setValue(newName);
                                        controlRef.child("command").setValue("update_card");
                                        Toast.makeText(requireContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            } else {
                Toast.makeText(requireContext(), "Không được để trống ID hoặc Tên", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteItem(int position) {
        AccessCard itemToDelete = cardList.get(position);

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thẻ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    getItemKeyById(itemToDelete.getId(), new OnKeyRetrievedListener() {
                        @Override
                        public void onKeyRetrieved(String key) {
                            if (key != null) {
                                DatabaseReference itemRef = databaseReference.child(key);
                                itemRef.removeValue();
                                controlRef.child("command").setValue("update_card");
                                Toast.makeText(requireContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void getItemKeyById(String id, OnKeyRetrievedListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccessCard item = snapshot.getValue(AccessCard.class);
                    if (item != null && item.getId().equals(id)) {
                        listener.onKeyRetrieved(snapshot.getKey());
                        return;
                    }
                }
                listener.onKeyRetrieved(null);  // Nếu không tìm thấy
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu", databaseError.toException());
            }
        });
    }

    public interface OnKeyRetrievedListener {
        void onKeyRetrieved(String key);
    }
}