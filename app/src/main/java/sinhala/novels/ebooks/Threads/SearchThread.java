package sinhala.novels.ebooks.Threads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import sinhala.novels.ebooks.Adapters.CategoryAlbumAdapter;
import sinhala.novels.ebooks.Adapters.SearchAdapter;
import sinhala.novels.ebooks.Model.AlbumModel;
import sinhala.novels.ebooks.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;

import sinhala.novels.ebooks.MainActivity;

public class SearchThread extends Thread{

    private final Context context;
    private final SearchActivity activity;
    private final FirebaseFirestore firebaseFirestore;

    public SearchThread(Context context, SearchActivity activity){
        this.context=context;
        this.activity=activity;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    public void run(){}

    public void getData(String text){

        if (text.trim().isEmpty()){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    activity.recyclerView.setAdapter(null);
                    activity.noDataAvailable.setVisibility(View.VISIBLE);
                    activity.progressBar.setVisibility(View.GONE);
                    activity.noDataAvailable.setText("Search by album's name or author's name");
                }
            });

        }else{

            ArrayList<AlbumModel> arrayList=new ArrayList<>();

            Query query=firebaseFirestore.collection("Albums")
                    .whereArrayContainsAny("SearchTags", Arrays.asList(text))
                    .limit(10);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value!=null){

                        arrayList.clear();

                        for (QueryDocumentSnapshot data : value) {

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
                            arrayList.add(model);

                        }

                        SearchAdapter adapter=new SearchAdapter(context,arrayList);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (arrayList.size()>0){
                                    activity.recyclerView.setAdapter(adapter);
                                    activity.noDataAvailable.setVisibility(View.GONE);
                                }else{
                                    activity.recyclerView.setAdapter(null);
                                    activity.noDataAvailable.setVisibility(View.VISIBLE);
                                    activity.noDataAvailable.setText("No Results Found!");
                                }
                                activity.progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            });

        }

    }

}
