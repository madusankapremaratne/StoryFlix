package sinhala.novels.ebooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sinhala.novels.ebooks.Threads.CategoryThread;

public class ViewCategoryActivity extends AppCompatActivity {

    private Context context;
    public RecyclerView recyclerView;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);

        context=this;
        progressBar=findViewById(R.id.progressBar);

        double id = getIntent().getDoubleExtra("CategoryID", 0);

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
        TextView categoryName = findViewById(R.id.categoryName);
        categoryName.setText(getIntent().getStringExtra("categoryName"));

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        CategoryThread thread=new CategoryThread(context,ViewCategoryActivity.this, id);
        thread.start();

    }
}