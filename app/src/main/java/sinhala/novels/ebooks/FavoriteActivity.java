package sinhala.novels.ebooks;

import static sinhala.novels.ebooks.MainActivity.favoriteChanged;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import sinhala.novels.ebooks.Threads.FavoriteThread;

public class FavoriteActivity extends AppCompatActivity {

    private Context context;
    public RecyclerView recyclerView;
    public ProgressBar progressBar;
    public FavoriteThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        context=this;
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

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

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.loading_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        thread=new FavoriteThread(context,this,dialog);
        thread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (favoriteChanged){
            favoriteChanged=false;
            thread.loadFavorites();
        }
    }
}