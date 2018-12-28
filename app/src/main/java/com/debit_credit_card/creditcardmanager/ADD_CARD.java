package com.debit_credit_card.creditcardmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ADD_CARD extends AppCompatActivity {

    Context context;
    final int EDIT_CARD = 5;
    Realm realm;
    CARD card;
    @BindView(R.id.show_card)
    CreditCardView showCard;
    @BindView(R.id.fab_edit)
    FloatingActionButton fabEdit;

    String card_number = "";
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.cvv)
    TextView cvv;


    private final String TAG = ADD_CARD.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__card);
        ButterKnife.bind(this);
        try {
            context=ADD_CARD.this;
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

            loadNativeAd();
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
                    if (card!=null){
                        Intent intent = new Intent(context, CardEditActivity.class);
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, card.getName());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, card.getCardNumber());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, card.getExpiry());
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_CVV, card.getCvv());
                        intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true); // pass "false" to discard expiry date validation.
                        startActivityForResult(intent, EDIT_CARD);
                    }
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
        }
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        try {
            if(resultCode == RESULT_OK) {

                String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                if (cardHolderName!=null||cardNumber!=null||expiry!=null||cvv!=null){
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
                Log.d("card",cardHolderName+cardNumber+expiry+cvv);
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getResources().getString(R.string.add_card_native));
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        // Request an ad
        nativeAd.loadAd();
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(ADD_CARD.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(ADD_CARD.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}
