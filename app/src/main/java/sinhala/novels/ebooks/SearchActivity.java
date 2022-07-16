package sinhala.novels.ebooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import sinhala.novels.ebooks.Threads.SearchThread;

public class SearchActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public TextView noDataAvailable;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Context context=this;
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        noDataAvailable=findViewById(R.id.noDataAvailable);
        progressBar=findViewById(R.id.progressBar);
        FloatingActionButton searchBtn = findViewById(R.id.searchBtn);

        SearchThread thread=new SearchThread(context,SearchActivity.this);
        thread.start();

        EditText searchED=findViewById(R.id.searchText);

        searchED.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH){
                    recyclerView.setAdapter(null);
                    progressBar.setVisibility(View.VISIBLE);
                    thread.getData(searchED.getText().toString());

                    View view=SearchActivity.this.getCurrentFocus();
                    if (view!=null){
                        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }

                    return true;
                }
                return false;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(null);
                progressBar.setVisibility(View.VISIBLE);
                thread.getData(searchED.getText().toString());

                View view=SearchActivity.this.getCurrentFocus();
                if (view!=null){
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }

            }
        });

    }
}