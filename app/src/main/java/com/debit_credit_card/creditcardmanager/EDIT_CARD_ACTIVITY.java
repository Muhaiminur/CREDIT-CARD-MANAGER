package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EDIT_CARD_ACTIVITY extends AppCompatActivity {

    Context context;
    final int EDIT_CARD = 5;
    Realm realm;
    CARD card;
    @BindView(R.id.show_card)
    CreditCardView showCard;
    @BindView(R.id.fab_edit)
    Button fabEdit;

    String card_number = "";
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.cvv)
    TextView cvv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__card);
        ButterKnife.bind(this);
        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_card_title_string));
            context = EDIT_CARD_ACTIVITY.this;
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("card.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            Intent intent = getIntent();
            card_number = intent.getStringExtra("card_number");
            if (card_number != null) {
                card = realm.where(CARD.class).equalTo("cardNumber", card_number).findFirst();
            }
            if (card != null) {
                showCard.setCVV(card.getCvv());
                showCard.setCardHolderName(card.getName());
                showCard.setCardExpiry(card.getExpiry());
                showCard.setCardNumber(card.getCardNumber());
                name.setText(card.getName());
                number.setText(card.getExpiry());
                date.setText(card.getCardNumber());
                cvv.setText(card.getCvv());
            }

            //loadNativeAd();
            //banner_add();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @OnClick({R.id.show_card, R.id.fab_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_card:
                break;
            case R.id.fab_edit:
                try {
                    if (card != null) {
                        Intent intent = new Intent(context, CardEditActivity.class);
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, card.getName());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, card.getCardNumber());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, card.getExpiry());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_CVV, card.getCvv());
                        intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true); // pass "false" to discard expiry date validation.
                        startActivityForResult(intent, EDIT_CARD);
                    } else {
                        Toast.makeText(this, "You have to add card before Edit", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
        }
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {

                String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                if (cardHolderName != null || cardNumber != null || expiry != null || cvv != null) {
                    if (card_number != null) {
                        CARD re = realm.where(CARD.class).equalTo("cardNumber", card_number).findFirst();
                        realm.beginTransaction();
                        re.setName(cardHolderName);
                        re.setCardNumber(cardNumber);
                        re.setCvv(cvv);
                        re.setExpiry(expiry);
                        realm.commitTransaction();
                    }
                }
                // Your processing goes here.
                Log.d("card", cardHolderName + cardNumber + expiry + cvv);
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
