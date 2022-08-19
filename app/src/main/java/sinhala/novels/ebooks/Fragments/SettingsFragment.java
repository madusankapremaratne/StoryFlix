package sinhala.novels.ebooks.Fragments;

import static sinhala.novels.ebooks.MainActivity.isPremium;
import static sinhala.novels.ebooks.MainActivity.mainActivity;
import static sinhala.novels.ebooks.MainActivity.onTrial;
import static sinhala.novels.ebooks.MainActivity.premiumMobile;
import static sinhala.novels.ebooks.MainActivity.profileDataChanged;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberyakku.carrierbillingsupporter.CarrierBillingSupporter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import sinhala.novels.ebooks.BuyPremiumActivity;
import sinhala.novels.ebooks.EditProfileActivity;
import sinhala.novels.ebooks.FavoriteActivity;
import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.LocalDatabase.LocalFavModel;
import sinhala.novels.ebooks.MainActivity;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ReportBugActivity;
import sinhala.novels.ebooks.SplashActivity;

public class SettingsFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleActivityResultLauncher;

    TextView userName,loginBtn,version,emailTV;
    CircleImageView profilePicture;

    Dialog authenticatingDialog;
    private DbHandler dbHandler;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchMaterial=view.findViewById(R.id.dSwitch);

        switchMaterial.setChecked(mainActivity.sharedPreferences.getInt("DarkMode", 0) == 1);

        context=getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        dbHandler=new DbHandler(mainActivity);

        authenticatingDialog=new Dialog(context);
        authenticatingDialog.setContentView(R.layout.authenticating_dialog);
        authenticatingDialog.setCancelable(false);
        authenticatingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        authenticatingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile().build();

        googleSignInClient= GoogleSignIn.getClient(mainActivity,gso);

        googleActivityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);

                            if (task.isSuccessful()){
                                handleSignInResult(task);
                            }else{
                                authenticatingDialog.dismiss();
                                Toast.makeText(context,"Login Failed!",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            authenticatingDialog.dismiss();
                            Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        LinearLayout favoriteBtn=view.findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, FavoriteActivity.class));
            }
        });

        userName=(TextView) view.findViewById(R.id.userName);
        loginBtn=(TextView) view.findViewById(R.id.editBtn);
        version=(TextView) view.findViewById(R.id.versionText);
        profilePicture=(CircleImageView) view.findViewById(R.id.profilePicture);
        emailTV=(TextView) view.findViewById(R.id.email);

        if (mainActivity.sharedPreferences.getBoolean("isPremium",false)||mainActivity.sharedPreferences.getBoolean("cbPremium",false)){
            version.setText("Version : Premium");
        }else{
            version.setText("Version : Free");
        }

        if (firebaseAuth.getCurrentUser()!=null){
            loginBtn.setText("Log Out");
            emailTV.setText(mainActivity.sharedPreferences.getString("email",""));
            emailTV.setVisibility(View.VISIBLE);
            userName.setText(mainActivity.sharedPreferences.getString("userName",""));

            LinearLayout layout=view.findViewById(R.id.profileBtn);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProfileDialog();
                }
            });

            RelativeLayout refreshDivider=view.findViewById(R.id.refreshDivider);
            refreshDivider.setVisibility(View.VISIBLE);
            LinearLayout refreshBtn=view.findViewById(R.id.refreshBtn);
            refreshBtn.setVisibility(View.VISIBLE);
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshData(mainActivity.userID);
                }
            });

            if (!mainActivity.sharedPreferences.getString("imageURL","").isEmpty()){
                Picasso.with(context).load(mainActivity.sharedPreferences.getString("imageURL","")).into(profilePicture);
            }

            DocumentReference documentReference=firebaseFirestore.collection("Users").document(String.valueOf(firebaseAuth.getCurrentUser().getUid()));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null && documentSnapshot.exists()){

                        String name=documentSnapshot.getString("Name");
                        String imageURL=documentSnapshot.getString("ImageURL");
                        boolean isPremium=documentSnapshot.getBoolean("IsPremium");
                        String email=documentSnapshot.getString("Email");

                        userName.setText(name);
                        if (imageURL!=null && !imageURL.isEmpty() && !imageURL.equals(mainActivity.sharedPreferences.getString("imageURL",""))){
                            Picasso.with(context).load(imageURL).into(profilePicture);
                        }

                        SharedPreferences.Editor editor=mainActivity.sharedPreferences.edit();
                        editor.putString("userName",name);
                        editor.putString("imageURL",imageURL);
                        editor.putBoolean("isPremium",isPremium);
                        editor.putString("email",email);
                        editor.apply();
                    }
                }
            });

        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (firebaseAuth.getCurrentUser()!=null){

                            new AlertDialog.Builder(context).setTitle("Do you want to log out ?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    logOut();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                        }else{

                            authenticatingDialog.show();
                            Intent intent=googleSignInClient.getSignInIntent();
                            googleActivityResultLauncher.launch(intent);

                        }
                    }
                },100);
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor= mainActivity.sharedPreferences.edit();
                if (isChecked){
                    editor.putInt("DarkMode",1);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    editor.putInt("DarkMode",0);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        LinearLayout inviteFriends=view.findViewById(R.id.inviteFriend);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Read modernly with StoryFlix > \n\nhttps://play.google.com/store/apps/details?id=sinhala.novels.ebooks");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        LinearLayout contactBtn=view.findViewById(R.id.contactBtn);
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Contact
                String number = "+94 706995585"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone="+number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        LinearLayout rateBtn=view.findViewById(R.id.rateBtn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Rate
                try{
                    String uri="market://details?id=sinhala.novels.ebooks";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                }
                catch (ActivityNotFoundException e){
                    String uri="https://play.google.com/store/apps/details?id=sinhala.novels.ebooks";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                }
            }
        });

        LinearLayout reportBtn=view.findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ReportBugActivity.class));
            }
        });

        RelativeLayout premiumBtn=view.findViewById(R.id.premiumPurchase);
        premiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser()==null){
                    //Authenticate the user first
                    authenticatingDialog.show();
                    Intent intent=googleSignInClient.getSignInIntent();
                    googleActivityResultLauncher.launch(intent);
                }else if (!isPremium && !onTrial){
                    startActivity(new Intent(getActivity(), BuyPremiumActivity.class));
                }else{
                    Toast.makeText(context,"You have already subscribed to premium!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout unsubBtn=view.findViewById(R.id.unsubscribe);
        RelativeLayout unsubDivider=view.findViewById(R.id.unSubDiv);

        LinearLayout privacyPolicy=view.findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://storyflixsl.blogspot.com/2022/08/storyflix-terms-and-condition.html"));
                startActivity(browserIntent);
            }
        });

        if (premiumMobile){
            unsubBtn.setVisibility(View.VISIBLE);
            unsubDivider.setVisibility(View.VISIBLE);

            unsubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dialog dialog=new Dialog(context);
                    dialog.setContentView(R.layout.unsub_dialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);

                    Button unsub=dialog.findViewById(R.id.unsub);
                    Button cancel=dialog.findViewById(R.id.cancel);
                    ProgressBar progressBar=dialog.findViewById(R.id.progressBar);

                    unsub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            unsub.setVisibility(View.INVISIBLE);
                            cancel.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            unsubscribe(dialog);
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            });

        }

        return view;

    }

    private void refreshData(String userID) {

        DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot!=null && documentSnapshot.exists()){

                    String name=documentSnapshot.getString("Name");
                    String imageURL=documentSnapshot.getString("ImageURL");
                    String email=documentSnapshot.getString("Email");
                    int premiumMethod=documentSnapshot.getDouble("PremiumMethod").intValue();
                    boolean isPremium=documentSnapshot.getBoolean("IsPremium");
                    String mobileNumber=documentSnapshot.getString("MobileNumber");
                    String accKey="";
                    if (documentSnapshot.getString("accessKey")!=null){
                        accKey=documentSnapshot.getString("accessKey");
                    }

                    userName.setText(name);
                    if (imageURL!=null && !imageURL.isEmpty() && !imageURL.equals(mainActivity.sharedPreferences.getString("imageURL",""))){
                        Picasso.with(context).load(imageURL).into(profilePicture);
                    }
                    emailTV.setText(email);
                    emailTV.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor editor=mainActivity.sharedPreferences.edit();
                    editor.putString("userName",name);
                    editor.putString("imageURL",imageURL);
                    editor.putBoolean("isPremium",isPremium);
                    editor.putString("email",email);
                    editor.putInt("premiumMethod",premiumMethod);
                    editor.putString("mobileNumber",mobileNumber);
                    editor.putString("accessKey",accKey);
                    editor.apply();

                    Toast.makeText(context, "Data Refreshed!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void unsubscribe(Dialog dialog) {
        CarrierBillingSupporter supporter=new CarrierBillingSupporter(mainActivity);
        supporter.initialize("WpIP1g4SB9utHkDX73foOQNjcVyG2EMJrFsm60CLwb5iqTRhKx", "tuDLR7oszgw2qAYGIpMQ", new CarrierBillingSupporter.OnInitializeCompleteListener() {
            @Override
            public void onInitialized() {

                if (supporter.isSubscribed()){
                    supporter.unSubscribe(new CarrierBillingSupporter.OnUnsubscribeActionListener() {
                        @Override
                        public void onSuccess() {
                            SharedPreferences sharedPreferences=context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            onTrial=false;
                            premiumMobile=false;
                            editor.putBoolean("cbPremium",false);
                            editor.apply();
                            Toast.makeText(context, "Unsubscribed!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(context, SplashActivity.class));
                            mainActivity.finish();
                        }

                        @Override
                        public void onFailed() {
                            dialog.dismiss();
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    dialog.dismiss();
                    Toast.makeText(context,"You are not subscribe to this app via your SIM!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSubscriptionChange(boolean subscription) {
            }

            @Override
            public void onSubscribed() {

            }

            @Override
            public void onUnSubscribed() {
            }

            @Override
            public void onPaymentPending() {
                Toast.makeText(context, "Subscription Pending!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc=task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        }catch (ApiException e){
            e.printStackTrace();
            loginFailed();
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {

        AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(context);
                    if (account!=null){

                        final String userID=firebaseAuth.getCurrentUser().getUid();
                        mainActivity.userID=userID;

                        String personName=account.getDisplayName();
                        String personEmail=account.getEmail();
                        String imageURL=String.valueOf(account.getPhotoUrl());

                        DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (documentSnapshot!=null && documentSnapshot.exists()){

                                    authenticatingDialog.dismiss();
                                    loginBtn.setText("Log Out");

                                    String name=documentSnapshot.getString("Name");
                                    String imageURL=documentSnapshot.getString("ImageURL");
                                    String email=documentSnapshot.getString("Email");
                                    int premiumMethod=documentSnapshot.getDouble("PremiumMethod").intValue();
                                    boolean isPremium=documentSnapshot.getBoolean("IsPremium");
                                    String mobileNumber=documentSnapshot.getString("MobileNumber");
                                    String accKey="";
                                    if (documentSnapshot.getString("accessKey")!=null){
                                        accKey=documentSnapshot.getString("accessKey");
                                    }

                                    userName.setText(name);
                                    if (imageURL!=null && !imageURL.isEmpty() && !imageURL.equals(mainActivity.sharedPreferences.getString("imageURL",""))){
                                        Picasso.with(context).load(imageURL).into(profilePicture);
                                    }
                                    emailTV.setText(email);
                                    emailTV.setVisibility(View.VISIBLE);

                                    SharedPreferences.Editor editor=mainActivity.sharedPreferences.edit();
                                    editor.putString("userName",name);
                                    editor.putString("imageURL",imageURL);
                                    editor.putBoolean("isPremium",isPremium);
                                    editor.putString("email",email);
                                    editor.putInt("premiumMethod",premiumMethod);
                                    editor.putString("mobileNumber",mobileNumber);
                                    editor.putString("accessKey",accKey);
                                    editor.apply();

                                    CollectionReference collectionReference=firebaseFirestore.collection("Users").document(userID).collection("Favorites");
                                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                                        LocalFavModel m=new LocalFavModel(snapshot.getDouble("ID"),
                                                                snapshot.getDouble("AlbumID"),
                                                                snapshot.getString("AlbumName"),
                                                                snapshot.getString("CoverURL"),
                                                                snapshot.getString("AuthorName"));
                                                        dbHandler.setFavorite(m);

                                                    }
                                                }
                                            }
                                        }
                                    });


                                }else{

                                    DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
                                    Map<String,Object> userData=new HashMap<>();
                                    userData.put("Name",personName);
                                    userData.put("ImageURL",imageURL);
                                    userData.put("Email",personEmail);
                                    userData.put("RegisteredDate",getDateCode());
                                    userData.put("IsPremium",false);
                                    userData.put("LastChargeDate",0);
                                    userData.put("IsAdmin",false);
                                    userData.put("PremiumMethod",0);
                                    userData.put("MobileNumber","");

                                    SharedPreferences.Editor editor=mainActivity.sharedPreferences.edit();
                                    editor.putString("userName",personName);
                                    editor.putString("imageURL",imageURL);
                                    editor.putString("email",personEmail);
                                    editor.putBoolean("isPremium",false);
                                    editor.putInt("premiumMethod",0);
                                    editor.putString("mobileNumber","");
                                    editor.apply();

                                    documentReference.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                authenticatingDialog.dismiss();
                                                loginBtn.setText("Log Out");

                                                userName.setText(personName);
                                                if (!imageURL.isEmpty()){
                                                    Picasso.with(context).load(imageURL).into(profilePicture);
                                                }
                                                emailTV.setText(personEmail);
                                                emailTV.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }
                            }
                        });


                    }

                }else{
                    loginFailed();
                }

            }
        });

    }

    private int getDateCode() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(String.valueOf(dateFormat.format(Calendar.getInstance().getTime())));
    }

    private void loginFailed(){
        Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show();
        authenticatingDialog.dismiss();
    }

    private void logOut(){
        mainActivity.userID="";
        firebaseAuth.signOut();
        emailTV.setVisibility(View.GONE);
        userName.setText("Guest");
        profilePicture.setImageDrawable(new ColorDrawable(ContextCompat.getColor(context,R.color.color_primary)));
        version.setText("Version : Free");
        loginBtn.setText("Login");

        SharedPreferences.Editor editor=mainActivity.sharedPreferences.edit();
        editor.putBoolean("isPremium",false);
        editor.putString("email","");
        editor.putString("userName","");
        editor.putString("imageURL","");
        editor.apply();

    }

    private void showProfileDialog(){

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.profile_long_click);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        CircleImageView profileImage=dialog.findViewById(R.id.profilePicture);
        TextView userNameText=dialog.findViewById(R.id.userName);
        Button editBtn=dialog.findViewById(R.id.editBtn);

        userNameText.setText(mainActivity.sharedPreferences.getString("userName",""));

        if (!mainActivity.sharedPreferences.getString("imageURL","").isEmpty()){
            Picasso.with(context).load(mainActivity.sharedPreferences.getString("imageURL","")).into(profileImage);
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        });

        dialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileDataChanged){
            profileDataChanged=false;
            userName.setText(mainActivity.sharedPreferences.getString("userName",""));

            if (!mainActivity.sharedPreferences.getString("imageURL","").isEmpty()){
                Picasso.with(context).load(mainActivity.sharedPreferences.getString("imageURL","")).into(profilePicture);
            }
        }
    }
}