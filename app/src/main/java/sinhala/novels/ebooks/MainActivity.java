package sinhala.novels.ebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import sinhala.novels.ebooks.Class.OrdersPageAdapter;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.cyberyakku.carrierbillingsupporter.CarrierBillingSupporter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public TabLayout tabLayout;
    private Context context;

    //Sorry for not using JAVA INTERFACES
    //This boolean values are used to notify data changes of different activities and fragments
    public static boolean isConnected,favoriteChanged=false,profileDataChanged=false,isPremium=false,onTrial=false,premiumMobile=false;


    public static MainActivity mainActivity;
    public TextView noInternetLayout;
    private int themeColor;
    private boolean exist=false; //For double tap to exit from the app
    public SharedPreferences sharedPreferences;
    public String userID="";
    CarrierBillingSupporter supporter; //SIM Payment Library by cyber yakku

    //Java Interface

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        mainActivity=this;
        noInternetLayout=findViewById(R.id.noInternetLayout);
        isConnected=isNetworkConnected();
        supporter=new CarrierBillingSupporter(MainActivity.this);
        checkInternetConnection();
        themeColor=ContextCompat.getColor(context,R.color.selectedTabIconColor);
        sharedPreferences=getSharedPreferences("UserData",MODE_PRIVATE);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        tabLayout=findViewById(R.id.tabLayout);

        if (sharedPreferences.getInt("notificationEnabled",0)==0){
            FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt("notificationEnabled",1);
                        editor.apply();
                    }
                }
            });
        }

        isPremium=sharedPreferences.getBoolean("isPremium",false);
        onTrial=sharedPreferences.getBoolean("cbPremium",false);
        premiumMobile=sharedPreferences.getBoolean("cbPremium",false);

        checkIsPremium();
        goOn();

    }

    private void checkIsPremium() {

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            long expireDate=sharedPreferences.getLong("ExpireDate",0);

            if (expireDate > 0){
                //Checking : If users payed trough direct bank deposit
                //If payed dat is > 0, then add on 30 days and get the payment expire date

                Calendar calendar=Calendar.getInstance();

                if (calendar.getTimeInMillis()<= expireDate){
                    isPremium=true;
                    Log.d("BankPayment","Premium True");
                }

            }else{

                BillingClient billingClient;
                billingClient = BillingClient.newBuilder(MainActivity.this).enablePendingPurchases().setListener((billingResult, list) -> {
                }).build();

                connectBC(billingClient);

                //CarrierBillingSupporter
                String accessKey=sharedPreferences.getString("accessKey","");
                Log.d("MyTest",accessKey);
                supporter=new CarrierBillingSupporter(MainActivity.this);
                if (!accessKey.isEmpty()){
                    initializeCarrierBilling(accessKey);
                }else{

                    //Check library is ready data
                    if(supporter.isSubscribed()){
                        supporter.getSubscriptionAccessKey(new CarrierBillingSupporter.OnSubscriptionAccessKeyRequestListener() {
                            @Override
                            public void onSubscriptionKey(String key) {
                                if(key != null){
                                    Log.d("MyTest","Main Acitivity - 2");
                                    updateKeyInServer(key);
                                }else{
                                    Log.d("MyTest","Key Null");
                                }
                            }
                        });
                    }else{
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        onTrial=false;
                        premiumMobile=false;
                        editor.putBoolean("cbPremium",false);
                        editor.putString("accessKey","");
                        editor.apply();
                    }

                }

            }

        }

    }

    void initializeCarrierBilling(String key){

        supporter.initializeWithKey(key, "WpIP1g4SB9utHkDX73foOQNjcVyG2EMJrFsm60CLwb5iqTRhKx", "tuDLR7oszgw2qAYGIpMQ", new CarrierBillingSupporter.OnInitializeCompleteListener() {
            @Override
            public void onInitialized() {
                Log.d("MyTest","Initi");
            }

            @Override
            public void onSubscriptionChange(boolean subscription) {
                Log.d("MyTest","Sub change - "+subscription);

                SharedPreferences.Editor editor=sharedPreferences.edit();
                if (subscription){
                    onTrial=true;
                    premiumMobile=true;
                    editor.putBoolean("cbPremium",true);
                }else{
                    onTrial=false;
                    premiumMobile=false;
                    editor.putBoolean("cbPremium",false);
                }
                editor.apply();
            }

            @Override
            public void onSubscribed() {
                Log.d("MyTest","Subscribed");

                //Check key
                String accessKey = sharedPreferences.getString("accessKey",null);
                if(accessKey != null){
                    supporter.getSubscriptionAccessKey(new CarrierBillingSupporter.OnSubscriptionAccessKeyRequestListener() {
                        @Override
                        public void onSubscriptionKey(String key) {
                            if(!key.equals(accessKey)){
                                updateKeyInServer(key);
                            }
                        }
                    });
                }

                SharedPreferences.Editor editor=sharedPreferences.edit();
                onTrial=true;
                premiumMobile=true;
                editor.putBoolean("cbPremium",true);
                editor.apply();

            }

            @Override
            public void onUnSubscribed() {
                Log.d("MyTest","Un Subscribed");

                SharedPreferences.Editor editor=sharedPreferences.edit();
                onTrial=false;
                premiumMobile=false;
                editor.putBoolean("cbPremium",false);

                editor.putString("accessKey","");

                editor.apply();
            }

            @Override
            public void onPaymentPending() {
                Log.d("MyTest","Payment Peniding");

                //Check key
                String accessKey = sharedPreferences.getString("accessKey",null);
                if(accessKey != null){
                    supporter.getSubscriptionAccessKey(new CarrierBillingSupporter.OnSubscriptionAccessKeyRequestListener() {
                        @Override
                        public void onSubscriptionKey(String key) {
                            if(!key.equals(accessKey)){
                                updateKeyInServer(key);
                            }
                        }
                    });
                }

                onTrial=false;
                premiumMobile=false;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showPendingDialog();
                    }
                });
            }
        });

    }

    void updateKeyInServer(String key){

        //Key Updating Progress
        Log.d("MyTest","Subscription key updated on server");

        FirebaseFirestore.getInstance().collection("Users").document(userID).update("accessKey",key).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("accessKey",key);
                editor.apply();
                initializeCarrierBilling(key);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("accessKey",key);
                editor.apply();
                initializeCarrierBilling(key);
            }
        });

    }

    //SIM payment pending dialog
    private void showPendingDialog() {
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.pending_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button hide=dialog.findViewById(R.id.hideBtn);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void connectBC(BillingClient billingClient){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectBC(billingClient);
            }
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(
                                        BillingResult billingResult,
                                        List<Purchase> list) {

                                    if (list!=null){

                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        if (list.size()>0){
                                            //PREMIUM
                                            isPremium=true;
                                            editor.putBoolean("isPremium",true);
                                            Log.d("Premium","true");
                                        }else{
                                            //NOT PREMIUM
                                            isPremium=false;
                                            editor.putBoolean("isPremium",false);
                                            Log.d("Premium","false");
                                        }

                                        editor.apply();

                                    }

                                }
                            }
                    );

                }
            }
        });
    }

    public void goOn(){

        ViewPager2 viewPager2=findViewById(R.id.viewPager);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(new OrdersPageAdapter(MainActivity.this));
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Home");
                        tab.setIcon(R.drawable.home_24);
                        break;
                    case 1:
                        tab.setText("Settings");
                        tab.setIcon(R.drawable.settings_24_outline);
                        break;

                }
            }
        });
        tabLayoutMediator.attach();

        int defaultTabIconColor= ContextCompat.getColor(context,R.color.defaultTabIconColor);

        tabLayout.getTabAt(0).getIcon().setColorFilter(themeColor, PorterDuff.Mode.SRC_IN);
        tabLayout.setTabTextColors(ContextCompat.getColor(context,R.color.tabTextColor),themeColor);
        tabLayout.getTabAt(1).getIcon().setColorFilter(defaultTabIconColor, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tabLayout.getSelectedTabPosition()==0){
                    //Home
                    tab.setIcon(R.drawable.home_24);
                    tab.getIcon().setColorFilter(themeColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.setTabTextColors(ContextCompat.getColor(context,R.color.tabTextColor),themeColor);
                    tabLayout.getTabAt(1).setIcon(R.drawable.settings_24_outline);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(defaultTabIconColor, PorterDuff.Mode.SRC_IN);

                }else{
                    tab.setIcon(R.drawable.settings_24);
                    tabLayout.getTabAt(0).setIcon(R.drawable.home_24_outline);

                    tab.getIcon().setColorFilter(themeColor, PorterDuff.Mode.SRC_IN);
                    tabLayout.setTabTextColors(ContextCompat.getColor(context,R.color.tabTextColor),themeColor);

                    tabLayout.getTabAt(0).getIcon().setColorFilter(defaultTabIconColor, PorterDuff.Mode.SRC_IN);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (exist){
            finish();
        }else{
            Toast.makeText(context,"Tap again to exit",Toast.LENGTH_SHORT).show();
            exist=true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exist=false;
                }
            },2000);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())) {

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(100);
            }

            noInternetLayout.setVisibility(View.VISIBLE);

            return false;
        }
        return true;
    }

    private void checkInternetConnection(){

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                isConnected=true;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        noInternetLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                isConnected=false;
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(100);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        noInternetLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.registerDefaultNetworkCallback(networkCallback);

    }

}