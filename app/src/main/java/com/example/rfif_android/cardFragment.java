package com.example.rfif_android;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

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

    private TextView tv_getid;
    private EditText tv_cardid;
    private Button btn_save;
    private EditText edt_cardname;
    private List<AccessCard> cardList;
    private AdapterListCard adapterListCard;
    private DatabaseReference databaseReference;

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

        tv_getid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Send message to hardware for getID", Toast.LENGTH_SHORT).show();
                cardList.add(new AccessCard("6AHG87", "Chi Von"));
                cardList.add(new AccessCard("663A87", "Huu Tien"));
                adapterListCard.notifyDataSetChanged();
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
                    DatabaseReference newCard = databaseReference.push();
                    newCard.child("id").setValue(id);
                    newCard.child("name").setValue(name);
                    tv_cardid.setText("");
                    edt_cardname.setText("");
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show();
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

        // Inflate layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        builder.setView(dialogView);

        // Ánh xạ EditText từ layout
        EditText editTextID = dialogView.findViewById(R.id.edittext_edit_id);
        EditText editTextName = dialogView.findViewById(R.id.edittext_edit_name);

        // Lấy thông tin item hiện tại
        AccessCard currentItem = cardList.get(position);
        editTextID.setText(currentItem.getId()); // Hiển thị ID
        editTextName.setText(currentItem.getName()); // Hiển thị Name

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newIdStr = editTextID.getText().toString().trim();
                String newName = editTextName.getText().toString().trim();

                if (!newIdStr.isEmpty() && !newName.isEmpty()) {
                    getItemKeyById(currentItem.getId(), new OnKeyRetrievedListener() {
                        @Override
                        public void onKeyRetrieved(String key) {
                            if (key != null) {
                                DatabaseReference itemRef = databaseReference.child(key);
                                itemRef.child("id").setValue(newIdStr);
                                itemRef.child("name").setValue(newName);
                            }
                        }
                    });
                    Toast.makeText(requireContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "ID và Name không được để trống!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void deleteItem(int position) {
        AccessCard itemtoDelete = cardList.get(position);
        getItemKeyById(itemtoDelete.getId(), new OnKeyRetrievedListener() {
            @Override
            public void onKeyRetrieved(String key) {
                if (key != null) {
                    databaseReference.child(key).removeValue();
                    Toast.makeText(requireContext(), "Đã xóa khỏi Firebase!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy item để xóa!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    // Listener để trả về key
    public interface OnKeyRetrievedListener {
        void onKeyRetrieved(String key);
    }
}