package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debit_credit_card.creditcardmanager.DATABASE.CARD;
import com.debit_credit_card.creditcardmanager.DATABASE.EXPENSE;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ADD_EXPENSE extends AppCompatActivity /*implements DatePickerDialog.OnDateSetListener*/ {

    @BindView(R.id.card_name)
    MaterialSpinner cardName;
    @BindView(R.id.expense_name)
    EditText expenseName;
    @BindView(R.id.expense_money)
    EditText expenseMoney;
    @BindView(R.id.expense_date)
    TextView expenseDate;
    @BindView(R.id.expense_type)
    MaterialSpinner expenseType;

    Realm realm;
    Context context;
    List<CARD> cardList = new ArrayList<>();
    @BindView(R.id.expense_save)
    Button expenseSave;
    CARD Selected_card;
    String type;
    String name;
    String expense;
    String date;
    Date final_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__expense);
        ButterKnife.bind(this);
        try {
            context = ADD_EXPENSE.this;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_expense_title_string));
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("card.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            cardList = realm.where(CARD.class).findAll();
            Log.d("size", cardList.size() + "");
            Spinner_Adapter cardadapter = new Spinner_Adapter(context, cardList);
            cardName.setAdapter(cardadapter);

            String[] ITEMS = {getResources().getString(R.string.select_debit_string), getResources().getString(R.string.select_credit_string)};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            expenseType.setAdapter(adapter);
            cardName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            expenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            Admob_Init();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @OnClick({R.id.expense_date, R.id.expense_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.expense_date:
                try {
                    new SingleDateAndTimePickerDialog.Builder(context)
                            //.bottomSheet()
                            //.curved()
                            //.minutesStep(15)
                            //.displayHours(false)
                            //.displayMinutes(false)
                            //.todayText("aujourd'hui")
                            .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                                @Override
                                public void onDisplayed(SingleDateAndTimePicker picker) {
                                    //retrieve the SingleDateAndTimePicker
                                }
                            })

                            .title(getResources().getString(R.string.select_save_string))
                            .listener(new SingleDateAndTimePickerDialog.Listener() {
                                @Override
                                public void onDateSelected(Date d) {
                                    Log.d("Date", d.toString());
                                    //Toast.makeText(context, "date" + d.toString(), Toast.LENGTH_LONG).show();
                                    date = d.toString();
                                    String[] parts = date.split(" ");
                                    String part1 = parts[0]; // 004
                                    String part2 = parts[1];
                                    String part3 = parts[2];
                                    expenseDate.setText(part1 + " " + part3 + " " + part2);
                                    expenseDate.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    final_date = d;
                                }
                            }).display();
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
            case R.id.expense_save:
                try {
                    if (Selected_card != null/*!TextUtils.isEmpty(Selected_card.getCardNumber())*/ && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(expenseMoney.getText().toString()) && !TextUtils.isEmpty(expenseName.getText().toString())) {
                        Log.d("save", Selected_card.getCardNumber());
                        try {
                            //for saving
                            realm.beginTransaction();
                            EXPENSE data = realm.createObject(EXPENSE.class);
                            data.setCardname(Selected_card);
                            data.setExpensename(expenseName.getText().toString());
                            data.setExpensemoney(expenseMoney.getText().toString());
                            data.setExpensedate(final_date);
                            data.setExpensetype(type);
                            Format formatter = new SimpleDateFormat("MM");
                            data.setDatemonth(formatter.format(final_date));
                            realm.commitTransaction();
                            finish();
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }
                    } else if (Selected_card == null) {
                        cardName.setError(getResources().getString(R.string.select_card_string));
                        cardName.requestFocusFromTouch();
                    } else if (TextUtils.isEmpty(expenseName.getText().toString())) {
                        expenseName.setError(getResources().getString(R.string.select_name_string));
                        expenseName.requestFocus();
                    } else if (TextUtils.isEmpty(expenseMoney.getText().toString())) {
                        expenseMoney.setError(getResources().getString(R.string.select_price_string));
                        expenseMoney.requestFocusFromTouch();
                    } else if (TextUtils.isEmpty(date)) {
                        expenseDate.setError(getResources().getString(R.string.select_date_string));
                        expenseDate.requestFocusFromTouch();
                    } else if (TextUtils.isEmpty(type)) {
                        expenseType.setError(getResources().getString(R.string.select_type_hint_string));
                        expenseType.requestFocusFromTouch();
                    }
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void Admob_Init() {
        AdView mAdView;
        //banner
        mAdView = findViewById(R.id.addexpense_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
}
