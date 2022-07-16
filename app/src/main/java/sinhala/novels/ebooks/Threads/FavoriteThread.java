package sinhala.novels.ebooks.Threads;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sinhala.novels.ebooks.Adapters.FavoriteAdapter;
import sinhala.novels.ebooks.FavoriteActivity;
import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.LocalDatabase.LocalFavModel;

public class FavoriteThread extends Thread{

    private final Context context;
    private final FavoriteActivity activity;
    private final DbHandler dbHandler;
    private Dialog dialog;


    public FavoriteThread(Context context,FavoriteActivity activity,Dialog dialog){
        this.context=context;
        this.activity=activity;
        this.dialog=dialog;
        this.dbHandler=new DbHandler(context);
    }

    public void run(){
        loadFavorites();

    }

    public void loadFavorites(){
        ArrayList<LocalFavModel> arrayList=new ArrayList<>();
        arrayList=dbHandler.getFavoriteItems();
        FavoriteAdapter adapter=new FavoriteAdapter(context,arrayList,dialog);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                activity.progressBar.setVisibility(View.GONE);
                activity.recyclerView.setAdapter(adapter);
            }
        });
    }

}
