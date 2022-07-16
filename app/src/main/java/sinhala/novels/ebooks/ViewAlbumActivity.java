package sinhala.novels.ebooks;

import static sinhala.novels.ebooks.MainActivity.favoriteChanged;
import static sinhala.novels.ebooks.MainActivity.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.LocalDatabase.LocalFavModel;
import sinhala.novels.ebooks.Model.AlbumCommentModel;
import sinhala.novels.ebooks.Model.AlbumModel;

import sinhala.novels.ebooks.Model.EpiModel;
import sinhala.novels.ebooks.Threads.ViewAlbumThread;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.api.LabelProto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewAlbumActivity extends AppCompatActivity {

    private Context context;
    private ShapeableImageView imageView;
    private TextView name,author,epiCount,category,previewText,tagline;
    public TextView viewCount;
    public RecyclerView recyclerView;

    public AlbumModel model;
    public static ArrayList<EpiModel> epiArray;
    public static boolean bookmarkChanged=false;

    private boolean isFavorite;
    private ImageView favImage;

    private DbHandler dbHandler;
    private ViewAlbumThread thread;
    private FirebaseFirestore firebaseFirestore;

    double favId;

    public DocumentSnapshot documentSnapshot;
    public ArrayList<AlbumCommentModel> commentsArray;

    public RecyclerView cRecyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;

    public boolean isLoading=false,startLoading=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album);

        context=this;
        epiArray=new ArrayList<>();
        dbHandler=new DbHandler(context);
        firebaseFirestore=FirebaseFirestore.getInstance();

        RelativeLayout backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },100);
            }
        });

        imageView=findViewById(R.id.imageView);
        name=findViewById(R.id.albumName);
        author=findViewById(R.id.author);
        epiCount=findViewById(R.id.epiCount);
        category=findViewById(R.id.category);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        previewText=findViewById(R.id.previewText);
        viewCount=findViewById(R.id.viewCount);
        tagline=findViewById(R.id.tagline);

        FloatingActionButton addCommentBtn = findViewById(R.id.addCommentBtn);

        RelativeLayout favoriteBtn=findViewById(R.id.favoriteBtn);
        favImage=findViewById(R.id.favImage);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                            Toast.makeText(context,"Sign up to save favorites!",Toast.LENGTH_SHORT).show();
                        }else {
                            if (isFavorite) {

                                dbHandler.deleteFavorite(model.getAlbumID());

                                isFavorite = false;
                                favImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                                ImageViewCompat.setImageTintList(favImage, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.mainTextColor)));

                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(String.valueOf(favId));
                                documentReference.delete();

                            } else {

                                favId = generateID();

                                LocalFavModel m = new LocalFavModel(favId, model.getAlbumID(), model.getAlbumName(), model.getCoverURL(), model.getAuthorName());
                                dbHandler.setFavorite(m);

                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(String.valueOf(favId));
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("ID", favId);
                                userData.put("AlbumID", model.getAlbumID());
                                userData.put("AlbumName", model.getAuthorName());
                                userData.put("CoverURL", model.getCoverURL());
                                userData.put("AuthorName", model.getAuthorName());

                                documentReference.set(userData);

                                isFavorite = true;
                                favImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                                ImageViewCompat.setImageTintList(favImage, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.warningColor)));
                                Toast.makeText(context, "Added to favorite!", Toast.LENGTH_SHORT).show();

                            }
                            favoriteChanged = true;
                        }
                    }
                },100);
            }
        });

        setData();

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentsDialog();
            }
        });

    }

    private double generateID() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyMMdd.HHmmss");
        return Double.parseDouble(String.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())));
    }

    private void setData(){

        Intent i=getIntent();

        model=new AlbumModel(i.getStringExtra("albumName"),
                i.getDoubleExtra("albumID",0),
                i.getDoubleExtra("categoryID",0),
                i.getStringExtra("coverURL"),
                i.getDoubleExtra("viewCount",0),
                i.getStringExtra("tagline"),
                i.getStringExtra("previewText"),
                i.getDoubleExtra("epiCount",0),
                i.getStringExtra("authorName"));

        name.setText(model.getAlbumName());
        author.setText(model.getAuthorName());
        epiCount.setText(convertEpiCount(model.getEpiCount()));
        if (model.getCoverURL()!=null && !model.getCoverURL().isEmpty()){
            Picasso.with(context).load(model.getCoverURL()).into(imageView);
        }
        previewText.setText(model.getPreviewText());
        category.setText(getCategory(model.getCategoryID()));
        viewCount.setText(convertViewCount(model.getViewCount()));
        tagline.setText(model.getTagline());

        favId=dbHandler.isFavorite(model.getAlbumID());
        if (favId>0){
            isFavorite=true;
        }
        if (isFavorite){
            favImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_24));
            ImageViewCompat.setImageTintList(favImage, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.warningColor)));
        }

        thread=new ViewAlbumThread(model.getAlbumID(),ViewAlbumActivity.this,context);
        thread.start();

    }

    private String getCategory(double categoryID) {
        if (categoryID==0){
            return String.valueOf("#Novels");
        }else if (categoryID==1){
            return String.valueOf("#Short Stories");
        }else if (categoryID==2){
            return String.valueOf("#Translation");
        }else{
            return String.valueOf("#Other");
        }
    }

    private String convertEpiCount(double episodesCount) {
        NumberFormat numberFormat=new DecimalFormat("#,##0");
        return String.valueOf("#Episodes : "+String.valueOf(numberFormat.format(episodesCount)));
    }

    private String convertViewCount(double viewCount){
        NumberFormat numberFormat=new DecimalFormat("#,##0");
        return String.valueOf(numberFormat.format(viewCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (thread!=null){
            thread.checkCount();
        }
        if (bookmarkChanged){
            bookmarkChanged=false;
            thread.adapter.notifyDataSetChanged();
        }
    }

    private void showCommentsDialog(){

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
                            v=new View(ViewAlbumActivity.this);
                        }
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        v.clearFocus();

                        String userName = mainActivity.sharedPreferences.getString("userName", "");
                        String imageURL = mainActivity.sharedPreferences.getString("imageURL", "");

                        AlbumCommentModel aModel = new AlbumCommentModel(generateCommentID(),
                                cmnt,
                                "",
                                model.getAlbumName(),
                                model.getCoverURL(),
                                model.getAlbumID(),
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