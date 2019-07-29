package com.debit_credit_card.creditcardmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CreditCardView;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;
import com.debit_credit_card.creditcardmanager.DATABASE.EXPENSE;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class Card_Details extends AppCompatActivity {

    Realm realm;
    Context context;
    String card_number;
    CARD card;
    Expense_Adapter expense_adapter;
    @BindView(R.id.expense_date)
    MaterialSpinner expenseDate;
    @BindView(R.id.details_recycle)
    RecyclerView detailsRecycle;
    RealmResults<EXPENSE> results;
    @BindView(R.id.expense_card)
    CreditCardView expenseCard;
    @BindView(R.id.expense_credit)
    TextView expenseCredit;
    @BindView(R.id.expense_debit)
    TextView expenseDebit;

    double credit = 0;
    double debit = 0;

    CARD Selected_card;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__details);
        ButterKnife.bind(this);
        try {
            context = Card_Details.this;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.expense_details_title_string));
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("card.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            card_number = getIntent().getStringExtra("card_number");
            results = realm.where(EXPENSE.class).equalTo("cardname.cardNumber", card_number).findAll().sort("expensedate", Sort.DESCENDING);
            for (EXPENSE expense : results) {
                if (expense.getExpensetype().equalsIgnoreCase(getResources().getString(R.string.select_credit_string))) {
                    credit = credit + Double.parseDouble(expense.getExpensemoney());
                } else {
                    debit = debit + Double.parseDouble(expense.getExpensemoney());
                }
            }
            expenseCredit.setText(credit + "");
            expenseDebit.setText(debit + "");
            expense_adapter = new Expense_Adapter(results, context);
            detailsRecycle.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            detailsRecycle.setLayoutManager(mLayoutManager);
            detailsRecycle.setItemAnimator(new DefaultItemAnimator());
            detailsRecycle.setAdapter(expense_adapter);
            if (!TextUtils.isEmpty(card_number)) {
                card = realm.where(CARD.class).equalTo("cardNumber", card_number).findFirst();
                expenseCard.setCardHolderName(card.getName());
                expenseCard.setCardNumber(card.getCardNumber());
                expenseCard.setCardExpiry(card.getExpiry());
                expenseCard.setCVV(card.getCvv());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months_array));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expenseDate.setAdapter(adapter);
                expenseDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != -1) {
                            String date = ((String) parent.getAdapter().getItem(position));
                            String d = "0";
                            if (date.equalsIgnoreCase("January")) {
                                d = "01";
                            } else if (date.equalsIgnoreCase("February")) {
                                d = "02";
                            } else if (date.equalsIgnoreCase("March")) {
                                d = "03";
                            } else if (date.equalsIgnoreCase("April")) {
                                d = "04";
                            } else if (date.equalsIgnoreCase("May")) {
                                d = "05";
                            } else if (date.equalsIgnoreCase("June")) {
                                d = "06";
                            } else if (date.equalsIgnoreCase("July")) {
                                d = "07";
                            } else if (date.equalsIgnoreCase("August")) {
                                d = "08";
                            } else if (date.equalsIgnoreCase("September")) {
                                d = "09";
                            } else if (date.equalsIgnoreCase("October")) {
                                d = "10";
                            } else if (date.equalsIgnoreCase("November")) {
                                d = "11";
                            } else if (date.equalsIgnoreCase("December")) {
                                d = "12";
                            }
                            results = realm.where(EXPENSE.class).equalTo("cardname.cardNumber", card.getCardNumber()).equalTo("datemonth", d).findAll().sort("expensedate", Sort.DESCENDING);
                            Log.d("Data", results.size() + "");
                            expense_adapter = new Expense_Adapter(results, context);
                            detailsRecycle.setAdapter(expense_adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                expense_adapter.notifyDataSetChanged();

                detailsRecycle.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), detailsRecycle, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        EXPENSE movie = results.get(position);
                        showFilterDialog(movie);
                        //Toast.makeText(context, movie.getExpensename(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void showFilterDialog(EXPENSE ex) {
        try {
            HashMap<String, Integer> screen = getScreenRes();
            int width = screen.get(SCREEN_WIDTH);
            int height = screen.get(SCREEN_HEIGHT);
            int mywidth = (width / 10) * 9;
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.edit_expense_dialoug);
            final MaterialSpinner card_spinner = dialog.findViewById(R.id.card_name);
            final MaterialSpinner type_spinner = dialog.findViewById(R.id.expense_type);
            EditText expense_name = dialog.findViewById(R.id.expense_name);
            EditText expense_price = dialog.findViewById(R.id.expense_money);
            expense_name.setText(ex.getExpensename());
            expense_price.setText(ex.getExpensemoney());
            Button yes = dialog.findViewById(R.id.expense_edit);
            Button no = dialog.findViewById(R.id.expense_cancel);
            RealmResults<CARD> card_list = realm.where(CARD.class).findAll();
            Spinner_Adapter cardadapter = new Spinner_Adapter(context, card_list);
            card_spinner.setAdapter(cardadapter);
            String[] ITEMS = {getResources().getString(R.string.select_debit_string), getResources().getString(R.string.select_credit_string)};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type_spinner.setAdapter(adapter);
            LinearLayout ll = dialog.findViewById(R.id.dialog_layout_size);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            ll.setLayoutParams(params);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!TextUtils.isEmpty(expense_price.getText().toString()) && !TextUtils.isEmpty(expense_name.getText().toString())) {
                        //Log.d("save", Selected_card.getCardNumber());
                        try {
                            //for saving
                            EXPENSE data = realm.where(EXPENSE.class).equalTo("cardname.cardNumber", card.getCardNumber()).equalTo("expensename", ex.getExpensename()).equalTo("expensemoney", ex.getExpensemoney()).equalTo("expensetype", ex.getExpensetype()).equalTo("datemonth", ex.getDatemonth()).findFirst();
                            if (data != null) {
                                realm.beginTransaction();
                                data.setExpensename(expense_name.getText().toString());
                                data.setExpensemoney(expense_price.getText().toString());
                                if (type != null) {
                                    data.setExpensetype(type);
                                }
                                if (Selected_card != null) {
                                    data.setCardname(Selected_card);
                                }
                                realm.commitTransaction();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            card_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != -1) {
                        Selected_card = ((CARD) parent.getAdapter().getItem(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != -1) {
                        type = ((String) parent.getAdapter().getItem(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            dialog.setCancelable(true);
            dialog.show();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    public HashMap<String, Integer> getScreenRes() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        map.put(SCREEN_WIDTH, width);
        map.put(SCREEN_HEIGHT, height);
        map.put(SCREEN_DENSITY, (int) metrics.density);
        return map;
    }

    public static String SCREEN_WIDTH = "width";
    public static String SCREEN_HEIGHT = "height";
    public static String SCREEN_DENSITY = "density";
}
