package com.debit_credit_card.creditcardmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmResults<CARD> card_list;
    Context context;

    CARD edit_card;

    List<CARD> card_list2 = new ArrayList<>();
    @BindView(R.id.fab)
    FloatingActionButton fab;

    final int GET_NEW_CARD = 2;

    Two_Adapter adapter;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    private AdView adView;

    private final String TAG = ADD_CARD.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView2;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            context = MainActivity.this;
            Realm.init(this);
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("card.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            card_list = realm.where(CARD.class).findAll();
            if (card_list.size() <= 0) {
                Toast.makeText(context, "ADD CARD", Toast.LENGTH_SHORT).show();
                CARD movie1 = new CARD("Card Holder Name", "522", "01/28", "5000000000000000");
                CARD movie2 = new CARD("Card Holder Name", "522", "01/28", "4000000000000000");
                CARD movie3 = new CARD("Card Holder Name", "522", "01/28", "3000000000000000");
                card_list2.add(movie1);
                card_list2.add(movie2);
                card_list2.add(movie3);
            } else {
                for (CARD c : card_list) {
                    card_list2.add(c);
                }
            }
            final HorizontalInfiniteCycleViewPager pager = findViewById(R.id.horizontal_cycle);
            adapter = new Two_Adapter(card_list2, context);
            pager.setAdapter(adapter);

            AudienceNetworkAds.initialize(this);
            banner_add();
            loadNativeAd();
            interstaler();

        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    /*@OnClick(R.id.fab)
    public void onViewClicked() {
        //startActivity(new Intent(context,ADD_CARD.class));

        Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
        startActivityForResult(intent, GET_NEW_CARD);
    }*/

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {

                String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                if (cardHolderName != null || cardNumber != null || expiry != null || cvv != null) {
                    realm.beginTransaction();
                    CARD re = realm.createObject(CARD.class);
                    re.setName(cardHolderName);
                    re.setCardNumber(cardNumber);
                    re.setCvv(cvv);
                    re.setExpiry(expiry);
                    realm.commitTransaction();
                }
                // Your processing goes here.
                Log.d("card", cardHolderName + cardNumber + expiry + cvv);
                /*card_list2.clear();
                for (CARD c:card_list){
                    card_list2.add(c);
                }
                adapter.notifyDataSetChanged();*/
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
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    public void banner_add() {
        adView = new AdView(this, getResources().getString(R.string.home_page_banner), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                //Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });


        // Request an ad
        adView.loadAd();
    }


    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getResources().getString(R.string.home_page_native));
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
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView2 = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView2);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView2.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView2.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView2.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView2.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView2.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView2.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView2.findViewById(R.id.native_ad_call_to_action);

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
                adView2,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    public void interstaler() {
        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.inter));
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
        showAdWithDelay();
    }

    private void showAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if (interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 9000 * 30); // Show the ad after 15 minutes
    }

    @OnClick({R.id.fab, R.id.fab_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                try {
                    Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                    startActivityForResult(intent, GET_NEW_CARD);
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
            case R.id.fab_add:
                if (card_list.size() > 0) {
                    startActivity(new Intent(context, ADD_EXPENSE.class));
                } else {
                    Toast.makeText(MainActivity.this, "FIRST ADD NEW CARD", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                try {
                    Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                    startActivityForResult(intent, GET_NEW_CARD);
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                return true;
            case R.id.item2:
                try {
                    showFilterDialog();
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showFilterDialog() {
        try {
            HashMap<String, Integer> screen = getScreenRes();
            int width = screen.get(SCREEN_WIDTH);
            int height = screen.get(SCREEN_HEIGHT);
            int mywidth = (width / 10) * 9;
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.edit_card_dialoug);
            final MaterialSpinner card_spinner = dialog.findViewById(R.id.card_name);
            Button yes = dialog.findViewById(R.id.card_edit);
            Button no = dialog.findViewById(R.id.card_cancel);
            Spinner_Adapter cardadapter = new Spinner_Adapter(context, card_list);
            card_spinner.setAdapter(cardadapter);
            LinearLayout ll = dialog.findViewById(R.id.dialog_layout_size);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            ll.setLayoutParams(params);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (edit_card != null) {
                        Intent intent = new Intent(context, ADD_CARD.class);
                        intent.putExtra("card_number", edit_card.getCardNumber());
                        context.startActivity(intent);
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
                        edit_card = ((CARD) parent.getAdapter().getItem(position));
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
