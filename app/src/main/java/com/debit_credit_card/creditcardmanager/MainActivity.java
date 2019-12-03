package com.debit_credit_card.creditcardmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.debit_credit_card.creditcardmanager.DATABASE.CARD;
import com.debit_credit_card.creditcardmanager.DATABASE.EXPENSE;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

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
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmResults<CARD> card_list;
    Context context;

    CARD edit_card;

    List<CARD> card_list2 = new ArrayList<>();
    @BindView(R.id.fab)
    Button fab;

    final int GET_NEW_CARD = 2;

    Two_Adapter adapter;
    @BindView(R.id.fab_add)
    Button fabAdd;
    @BindView(R.id.total_money)
    TextView totalMoney;
    double credit = 0;
    double debit = 0;


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
            try {
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                        try {
                            if (card_list.size() > 0) {
                                RealmResults<EXPENSE> results = realm.where(EXPENSE.class).equalTo("cardname.cardNumber", card_list2.get(i).getCardNumber()).findAll().sort("expensedate", Sort.DESCENDING);
                                for (EXPENSE expense : results) {
                                    if (expense.getExpensetype().equalsIgnoreCase(getResources().getString(R.string.select_credit_string))) {
                                        credit = credit + Double.parseDouble(expense.getExpensemoney());
                                    } else {
                                        debit = debit + Double.parseDouble(expense.getExpensemoney());
                                    }
                                }
                                totalMoney.setText(getResources().getString(R.string.available_title_string) + (credit - debit));
                                credit = 0;
                                debit = 0;
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });
                check();
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }

            Admob_Init();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
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
        super.onDestroy();
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
                        Intent intent = new Intent(context, EDIT_CARD_ACTIVITY.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            check();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    public void check() {
        try {
            if (card_list.size() > 0) {
                RealmResults<EXPENSE> results = realm.where(EXPENSE.class).equalTo("cardname.cardNumber", card_list2.get(0).getCardNumber()).findAll().sort("expensedate", Sort.DESCENDING);
                for (EXPENSE expense : results) {
                    if (expense.getExpensetype().equalsIgnoreCase(getResources().getString(R.string.select_credit_string))) {
                        credit = credit + Double.parseDouble(expense.getExpensemoney());
                    } else {
                        debit = debit + Double.parseDouble(expense.getExpensemoney());
                    }
                }
                totalMoney.setText(getResources().getString(R.string.available_title_string) + (credit - debit));
                credit = 0;
                debit = 0;
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    public void Admob_Init() {
        AdView mAdView;
        InterstitialAd mInterstitialAd;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //banner
        mAdView = findViewById(R.id.adView);
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
        /*AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());*/

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_home_inter));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
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
                // Code to be executed when the ad is displayed.
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
                // Code to be executed when the interstitial ad is closed.

                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something...
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        }, 9000);
    }
}
