package sinhala.novels.ebooks;

import static sinhala.novels.ebooks.MainActivity.mainActivity;
import static sinhala.novels.ebooks.ViewAlbumActivity.bookmarkChanged;
import static sinhala.novels.ebooks.ViewAlbumActivity.epiArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.Model.AlbumCommentModel;
import sinhala.novels.ebooks.Model.EpiCommentModel;
import sinhala.novels.ebooks.Threads.EpiThread;

public class ReadEpiActivity extends AppCompatActivity {

    private Context context;
    private Animation clickAnimation;
    private boolean inBookmark=false,isDark=false;
    private TextView contentText,title;
    private RelativeLayout mainBackground,nextBtn,previousBtn;
    private ImageView backImage,settingImage,bookmarkImage;

    int position=0,max=0;
    DbHandler dbHandler;
    private EpiThread thread;

    public DocumentSnapshot documentSnapshot;
    public ArrayList<EpiCommentModel> commentsArray;

    public RecyclerView cRecyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;

    public boolean isLoading=false,startLoading=true;
    String albumName,coverURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epi);

        context=this;
        clickAnimation= AnimationUtils.loadAnimation(context,R.anim.click_animation);
        dbHandler=new DbHandler(context);

        position=getIntent().getIntExtra("position",0);
        max=getIntent().getIntExtra("max",0);
        coverURL=getIntent().getStringExtra("coverURL");
        albumName=getIntent().getStringExtra("albumName");

        thread=new EpiThread(context,ReadEpiActivity.this,epiArray.get(position).getEpiID());
        thread.start();

        if (coverURL==null){
            coverURL="";
        }
        if (albumName==null || albumName.isEmpty()){
            albumName="unknown";
        }

        RelativeLayout backBtn=findViewById(R.id.backBtn);
        RelativeLayout bookmarkBtn=findViewById(R.id.bookmark);
        RelativeLayout settingsBtn=findViewById(R.id.settings);
        RelativeLayout commentBtn=findViewById(R.id.commentBtn);
        bookmarkImage=findViewById(R.id.bookmarkImage);
        backImage=findViewById(R.id.backImage);
        settingImage=findViewById(R.id.settingImage);
        contentText=findViewById(R.id.textView);
        title=findViewById(R.id.title);
        mainBackground=findViewById(R.id.mainBackground);
        nextBtn=findViewById(R.id.nextBtn);
        previousBtn=findViewById(R.id.previousBtn);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentBtn.startAnimation(clickAnimation);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCommentDialog();
                    }
                },100);
            }
        });

        if (position==0 && max==0){
            previousBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
        }else if (position==0){
            previousBtn.setVisibility(View.GONE);
        }else if (position==max){
            nextBtn.setVisibility(View.GONE);
        }

        title.setText(epiArray.get(position).getTitle());
        contentText.setText(epiArray.get(position).getContent());

        dbHandler.setNewViewCount(epiArray.get(position).getEpiID(),epiArray.get(position).getAlbumID());

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtn.startAnimation(clickAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(context, ReadEpiActivity.class).putExtra("position",position+1).putExtra("max",max));
                        finish();
                    }
                },100);
            }
        });

        inBookmark=dbHandler.isBookmarked(epiArray.get(position).getEpiID());
        if (inBookmark){
            bookmarkImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_bookmark_24));
        }

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousBtn.startAnimation(clickAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(context, ReadEpiActivity.class).putExtra("position",position-1).putExtra("max",max));
                        finish();
                    }
                },100);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsBtn.startAnimation(clickAnimation);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSettingDialog();
                    }
                },100);
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmarkBtn.startAnimation(clickAnimation);
                bookmarkChanged=true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (inBookmark){
                            inBookmark=false;
                            dbHandler.deleteBookmark(epiArray.get(position).getAlbumID());
                            bookmarkImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_bookmark_border_24));
                        }else{
                            inBookmark=true;
                            dbHandler.setBookmark(epiArray.get(position).getEpiID(),epiArray.get(position).getAlbumID());
                            bookmarkImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_bookmark_24));
                            Toast.makeText(context,"Bookmark added to this episode",Toast.LENGTH_SHORT).show();
                        }
                    }
                },100);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.startAnimation(clickAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },100);
            }
        });

    }

    private void showCommentDialog() {

        documentSnapshot=null;
        commentsArray=new ArrayList<>();

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.comments_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cRecyclerView=dialog.findViewById(R.id.recyclerView);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout=dialog.findViewById(R.id.swipeView);
        EditText commentED=dialog.findViewById(R.id.commentED);
        FloatingActionButton commentBtn=dialog.findViewById(R.id.publishComment);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    String cmnt = commentED.getText().toString();
                    if (cmnt.trim().isEmpty()) {
                        commentED.setError("Please enter your comment!");
                    } else {

                        View v = dialog.getCurrentFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (v == null) {
                            v=new View(ReadEpiActivity.this);
                        }
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        v.clearFocus();

                        String userName = mainActivity.sharedPreferences.getString("userName", "");
                        String imageURL = mainActivity.sharedPreferences.getString("imageURL", "");

                        EpiCommentModel aModel = new EpiCommentModel(generateCommentID(),
                                cmnt,
                                "",
                                albumName,
                                coverURL,
                                epiArray.get(position).getAlbumID(),
                                epiArray.get(position).getEpiID(),
                                userName,
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                imageURL,
                                getTodayDate(),
                                "Unknown");

                        thread.addComment(aModel);

                        commentED.setText("");

                    }
                }else{
                    Toast.makeText(context,"Sign up to comment!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setRefreshing(true);

        dialog.show();

        cRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager=(LinearLayoutManager) cRecyclerView.getLayoutManager();
                int totalItem=linearLayoutManager.getItemCount();
                int lastVisible=linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if ((totalItem<lastVisible+2) && !isLoading && !startLoading){

                    swipeRefreshLayout.setRefreshing(true);
                    isLoading=true;
                    thread.loadComments();

                }
            }
        });

        thread.loadComments();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                documentSnapshot=null;
                startLoading=true;
            }
        });

    }

    private void showSettingDialog() {
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.settings_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        RelativeLayout small,normal,medium,large;
        small=dialog.findViewById(R.id.small);;
        normal=dialog.findViewById(R.id.normal);
        medium=dialog.findViewById(R.id.medium);
        large=dialog.findViewById(R.id.large);

        CircleImageView lightCircle,darkCircle;
        lightCircle=dialog.findViewById(R.id.lightCircle);
        darkCircle=dialog.findViewById(R.id.darkCircle);

        RelativeLayout lightBtn,darkBtn;
        lightBtn=dialog.findViewById(R.id.lightBtn);
        darkBtn=dialog.findViewById(R.id.darkBtn);

        ImageView nextImage=findViewById(R.id.nextImageView);
        ImageView previousImage=findViewById(R.id.previewImageView);

        if (isDark){
            lightCircle.setImageDrawable(new ColorDrawable(ContextCompat.getColor(context,R.color.dividerColor)));
            darkCircle.setImageDrawable(new ColorDrawable(ContextCompat.getColor(context,R.color.premiumColor)));
        }

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lightBtn.startAnimation(clickAnimation);
                if (isDark || AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
                    isDark=false;
                    contentText.setTextColor(ContextCompat.getColor(context,R.color.textColorLight));
                    title.setTextColor(ContextCompat.getColor(context,R.color.textColorLight));
                    mainBackground.setBackgroundColor(ContextCompat.getColor(context,R.color.backgroundLight));
                    ColorStateList list=ColorStateList.valueOf(ContextCompat.getColor(context,R.color.textColorLight));
                    ImageViewCompat.setImageTintList(bookmarkImage, list);
                    ImageViewCompat.setImageTintList(backImage,list);
                    ImageViewCompat.setImageTintList(settingImage,list);
                    ImageViewCompat.setImageTintList(nextImage,list);
                    ImageViewCompat.setImageTintList(previousImage,list);

                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(context,R.color.backgroundLight));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    window.setNavigationBarColor(ContextCompat.getColor(context,R.color.backgroundLight));

                    dialog.dismiss();

                }
            }
        });

        darkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                darkBtn.startAnimation(clickAnimation);
                if (!isDark || AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO) {
                    isDark = true;
                    contentText.setTextColor(ContextCompat.getColor(context, R.color.textColorDark));
                    title.setTextColor(ContextCompat.getColor(context, R.color.textColorDark));
                    mainBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundDark));
                    ColorStateList list=ColorStateList.valueOf(ContextCompat.getColor(context,R.color.textColorDark));
                    ImageViewCompat.setImageTintList(bookmarkImage, list);
                    ImageViewCompat.setImageTintList(backImage,list);
                    ImageViewCompat.setImageTintList(settingImage,list);
                    ImageViewCompat.setImageTintList(nextImage,list);
                    ImageViewCompat.setImageTintList(previousImage,list);

                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(context,R.color.backgroundDark));
                    window.getDecorView().setSystemUiVisibility(0);
                    window.setNavigationBarColor(ContextCompat.getColor(context,R.color.backgroundDark));

                    dialog.dismiss();
                }
            }
        });

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText.setTextSize(13);
                dialog.dismiss();
            }
        });

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText.setTextSize(15);
                dialog.dismiss();
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText.setTextSize(17);
                dialog.dismiss();
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText.setTextSize(19);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        return String.valueOf(dateFormat.format(Calendar.getInstance().getTime()));
    }

    private String generateCommentID() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd.HHmmss");
        String firstPart=dateFormat.format(Calendar.getInstance().getTime());
        return String.valueOf(firstPart+String.valueOf(UUID.randomUUID().toString()));
    }


}