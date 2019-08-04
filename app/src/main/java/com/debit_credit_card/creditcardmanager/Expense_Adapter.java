package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debit_credit_card.creditcardmanager.DATABASE.EXPENSE;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Expense_Adapter extends RecyclerView.Adapter<Expense_Adapter.MyViewHolder> {
    Context context;
    List<EXPENSE> expenses_list;


    public Expense_Adapter(List<EXPENSE> notification, Context c) {
        expenses_list = notification;
        context = c;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.expense_name)
        TextView expenseName;
        @BindView(R.id.expense_money)
        TextView expenseMoney;
        @BindView(R.id.expense_type)
        TextView expenseType;
        @BindView(R.id.expense_date)
        TextView expenseDate;
        @BindView(R.id.card_name)
        CardView cardName;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EXPENSE bodyResponse = expenses_list.get(position);
        try {
            if (position % 2 == 0) {
                holder.cardName.setBackgroundColor(Color.parseColor("#f1f6fd"));
            } else {
                holder.cardName.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.expenseName.setText(bodyResponse.getExpensename());
            if (bodyResponse.getExpensetype().equalsIgnoreCase(context.getResources().getString(R.string.select_credit_string))) {
                holder.expenseMoney.setText(bodyResponse.getExpensemoney());
                holder.expenseMoney.setTextColor(context.getResources().getColor(R.color.green_color));
            } else {
                holder.expenseMoney.setText(bodyResponse.getExpensemoney());
            }
            holder.expenseType.setText(bodyResponse.getExpensetype());
            Format formatter = new SimpleDateFormat("dd-MM-yyyy");
            holder.expenseDate.setText(formatter.format(bodyResponse.getExpensedate()));
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getItemCount() {
        return expenses_list.size();
    }
}