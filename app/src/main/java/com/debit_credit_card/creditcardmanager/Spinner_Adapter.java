package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.debit_credit_card.creditcardmanager.DATABASE.CARD;

import java.util.List;

public class Spinner_Adapter extends BaseAdapter {

    Context context;
    List<CARD> jsonArray;

    public Spinner_Adapter(Context context, List<CARD> jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.size();
    }

    @Override
    public Object getItem(int i) {
        return jsonArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_list, null);
        }
        CARD jsonObject = jsonArray.get(i);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(jsonObject.getName() + "/ " + jsonObject.getCardNumber());
        return view;
    }
}
