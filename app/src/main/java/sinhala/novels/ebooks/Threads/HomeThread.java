package sinhala.novels.ebooks.Threads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import sinhala.novels.ebooks.Adapters.AlbumAdapter;
import sinhala.novels.ebooks.Adapters.CategoryAdapter;
import sinhala.novels.ebooks.Adapters.MainAdapter;
import sinhala.novels.ebooks.Adapters.SliderAdapter;
import sinhala.novels.ebooks.Fragments.HomeFragment;
import sinhala.novels.ebooks.Model.AlbumModel;
import sinhala.novels.ebooks.Model.CategoryModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sinhala.novels.ebooks.MainActivity;
import sinhala.novels.ebooks.Model.MainModel;

public class HomeThread extends Thread{

    private final Context context;
    private SliderAdapter sliderAdapter;
    HomeFragment homeFragment;
    public FirebaseFirestore firebaseFirestore;
    public ArrayList<MainModel> arrayList;
    public MainAdapter mainAdapter;
    private int timeRefreshed=0;

    public HomeThread(Context context,HomeFragment homeFragment){
        this.context=context;
        this.homeFragment=homeFragment;
        firebaseFirestore=FirebaseFirestore.getInstance();
        arrayList=new ArrayList<>();
        arrayList.add(new MainModel("Novels",0));
        arrayList.add(new MainModel("Short Stories",1));
        arrayList.add(new MainModel("Translations",2));
        arrayList.add(new MainModel("Other",3));
    }

    public void run(){

        mainAdapter=new MainAdapter(context,arrayList);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                homeFragment.mainRecyclerView.setAdapter(mainAdapter);
            }
        },2000);

        ArrayList<AlbumModel> slideArrayList=new ArrayList<>();

        CollectionReference collectionReference=firebaseFirestore.collection("Albums");
        collectionReference.orderBy("AlbumID", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult() != null) {

                        slideArrayList.clear();

                        for (QueryDocumentSnapshot data : task.getResult()) {

                            AlbumModel model=new AlbumModel(data.getString("AlbumName"),
                                    data.getDouble("AlbumID"),
                                    data.getDouble("CategoryID"),
                                    data.getString("CoverURL"),
                                    data.getDouble("ViewCount"),
                                    data.getString("Tagline"),
                                    data.getString("PreviewText"),
                                    data.getDouble("EpiCount"),
                                    data.getDouble("CreatedDate"),
                                    data.getString("AuthorName"));
                            slideArrayList.add(model);

                        }

                        sliderAdapter=new SliderAdapter(context,slideArrayList);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                homeFragment.sliderRecyclerView.setAdapter(sliderAdapter);
                                LinearSnapHelper helper=new LinearSnapHelper();
                                helper.attachToRecyclerView(homeFragment.sliderRecyclerView);

                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (homeFragment.layoutManager.findLastCompletelyVisibleItemPosition()<(sliderAdapter.getItemCount()-1)){
                                            homeFragment.layoutManager.smoothScrollToPosition(homeFragment.sliderRecyclerView,new RecyclerView.State(),homeFragment.layoutManager.findLastCompletelyVisibleItemPosition()+1);
                                        }else if (homeFragment.layoutManager.findLastCompletelyVisibleItemPosition() == (sliderAdapter.getItemCount()-1)){
                                            homeFragment.layoutManager.smoothScrollToPosition(homeFragment.sliderRecyclerView,new RecyclerView.State(),0);
                                        }
                                    }
                                },0,3000);
                            }
                        });

                    }
                }

            }
        });

        loadNovels();
        loadSS();
        loadTranslations();
        loadOther();

    }

    private void loadOther() {
        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",3)
                .orderBy("AlbumID", Query.Direction.DESCENDING)
                .limit(4);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && (arrayList.get(3).getArrayList()==null || arrayList.get(3).getTimeRefreshed()<2)){

                    ArrayList<AlbumModel> array=new ArrayList<>();

                    for (QueryDocumentSnapshot data:value){

                        AlbumModel model=new AlbumModel(data.getString("AlbumName"),
                                data.getDouble("AlbumID"),
                                data.getDouble("CategoryID"),
                                data.getString("CoverURL"),
                                data.getDouble("ViewCount"),
                                data.getString("Tagline"),
                                data.getString("PreviewText"),
                                data.getDouble("EpiCount"),
                                data.getDouble("CreatedDate"),
                                data.getString("AuthorName"));

                        array.add(model);
                    }

                    arrayList.get(3).setArrayList(array);
                    arrayList.get(3).setTimeRefreshed(arrayList.get(3).getTimeRefreshed()+1);
                    mainAdapter.notifyItemChanged(3);

                }
            }
        });
    }

    private void loadTranslations() {
        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",2)
                .orderBy("AlbumID", Query.Direction.DESCENDING)
                .limit(4);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && (arrayList.get(2).getArrayList()==null || arrayList.get(2).getTimeRefreshed()<2)){

                    ArrayList<AlbumModel> array=new ArrayList<>();

                    for (QueryDocumentSnapshot data:value){

                        AlbumModel model=new AlbumModel(data.getString("AlbumName"),
                                data.getDouble("AlbumID"),
                                data.getDouble("CategoryID"),
                                data.getString("CoverURL"),
                                data.getDouble("ViewCount"),
                                data.getString("Tagline"),
                                data.getString("PreviewText"),
                                data.getDouble("EpiCount"),
                                data.getDouble("CreatedDate"),
                                data.getString("AuthorName"));

                        array.add(model);
                    }

                    arrayList.get(2).setArrayList(array);
                    arrayList.get(2).setTimeRefreshed(arrayList.get(2).getTimeRefreshed()+1);
                    mainAdapter.notifyItemChanged(2);

                }
            }
        });
    }

    private void loadSS() {
        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",1)
                .orderBy("AlbumID", Query.Direction.DESCENDING)
                .limit(4);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && (arrayList.get(1).getArrayList()==null || arrayList.get(1).getTimeRefreshed()<2)){

                    ArrayList<AlbumModel> array=new ArrayList<>();

                    for (QueryDocumentSnapshot data:value){

                        AlbumModel model=new AlbumModel(data.getString("AlbumName"),
                                data.getDouble("AlbumID"),
                                data.getDouble("CategoryID"),
                                data.getString("CoverURL"),
                                data.getDouble("ViewCount"),
                                data.getString("Tagline"),
                                data.getString("PreviewText"),
                                data.getDouble("EpiCount"),
                                data.getDouble("CreatedDate"),
                                data.getString("AuthorName"));

                        array.add(model);
                    }

                    arrayList.get(1).setArrayList(array);
                    arrayList.get(1).setTimeRefreshed(arrayList.get(1).getTimeRefreshed()+1);
                    mainAdapter.notifyItemChanged(1);

                }
            }
        });
    }

    private void loadNovels() {
        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",0.0)
                .orderBy("AlbumID", Query.Direction.DESCENDING)
                .limit(4);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && (arrayList.get(0).getArrayList()==null || arrayList.get(0).getTimeRefreshed()<2)){

                    ArrayList<AlbumModel> array=new ArrayList<>();

                    for (QueryDocumentSnapshot data:value){

                        AlbumModel model=new AlbumModel(data.getString("AlbumName"),
                                data.getDouble("AlbumID"),
                                data.getDouble("CategoryID"),
                                data.getString("CoverURL"),
                                data.getDouble("ViewCount"),
                                data.getString("Tagline"),
                                data.getString("PreviewText"),
                                data.getDouble("EpiCount"),
                                data.getDouble("CreatedDate"),
                                data.getString("AuthorName"));

                        array.add(model);
                    }

                    arrayList.get(0).setArrayList(array);
                    arrayList.get(0).setTimeRefreshed(arrayList.get(0).getTimeRefreshed()+1);
                    mainAdapter.notifyItemChanged(0);

                }
            }
        });
    }

}
