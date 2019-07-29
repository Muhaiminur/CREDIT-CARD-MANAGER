package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CreditCardView;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;

import java.util.List;

public class Two_Adapter extends PagerAdapter {

    List<CARD> card_list;
    Context context;
    LayoutInflater layoutInflater;

    public Two_Adapter(List<CARD> lstImages, Context context) {
        this.card_list = lstImages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return card_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.item_two, container, false);
        CreditCardView card = view.findViewById(R.id.card);
        //imageView.setImageResource(lstImages.get(position));
        card.setCVV(card_list.get(position).getCvv());
        card.setCardHolderName(card_list.get(position).getName());
        card.setCardExpiry(card_list.get(position).getExpiry());
        card.setCardNumber(card_list.get(position).getCardNumber());
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.d("abir","ok");
                Intent intent = new Intent(context, ADD_CARD.class);
                intent.putExtra("card_number",card_list.get(position).getCardNumber());
                context.startActivity(intent);*/

                Intent intent = new Intent(context, Card_Details.class);
                intent.putExtra("card_number", card_list.get(position).getCardNumber());
                context.startActivity(intent);
            }
        });
        return view;
    }
}