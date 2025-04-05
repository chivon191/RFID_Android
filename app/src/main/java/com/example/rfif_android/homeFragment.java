package com.example.rfif_android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.controlhome.models.AccessRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Tìm ListView từ layout
        ListView listCard = view.findViewById(R.id.accesslist);

        // Tạo dữ liệu mẫu cho ListView
        List<AccessRecord> accessRecords = new ArrayList<>();
        accessRecords.add(new AccessRecord("Nguyễn Văn A", "08:00 - 01/03/2024"));
        accessRecords.add(new AccessRecord("Trần Thị B", "09:30 - 02/03/2024"));
        accessRecords.add(new AccessRecord("Lê Văn C", "11:45 - 03/03/2024"));

        // Tạo Adapter và gán vào ListView
        homeFragment.AccessRecordAdapter adapter = new homeFragment.AccessRecordAdapter(accessRecords);
        listCard.setAdapter(adapter);

        return view; // Trả về giao diện đã tạo
    }

    class AccessRecordAdapter extends ArrayAdapter<AccessRecord> {

        public AccessRecordAdapter(List<AccessRecord> records) {
            super(requireContext(), 0, records);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_access_record, parent, false);
            }

            AccessRecord record = getItem(position);

            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvTime = convertView.findViewById(R.id.tvTime);

            if (record != null) {
                tvName.setText(record.getName());
                tvTime.setText(record.getTime());
            }

            return convertView;
        }
    }
}