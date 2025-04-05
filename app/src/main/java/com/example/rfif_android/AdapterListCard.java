package com.example.rfif_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rfif_android.models.AccessCard;
import java.util.List;

public class AdapterListCard extends BaseAdapter {

    private Context context;
    private List<AccessCard> cardList;

    public AdapterListCard(Context context, List<AccessCard> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView text1 = convertView.findViewById(android.R.id.text1);
        AccessCard card = cardList.get(position);
        text1.setText("Name: " + card.getName());
        return convertView;
    }
}
