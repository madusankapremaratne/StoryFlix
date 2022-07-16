package sinhala.novels.ebooks.Threads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import sinhala.novels.ebooks.Adapters.AlbumAdapter;
import sinhala.novels.ebooks.Adapters.CategoryAlbumAdapter;
import sinhala.novels.ebooks.Model.AlbumModel;
import sinhala.novels.ebooks.ViewCategoryActivity;

public class CategoryThread extends Thread{

    private final Context context;
    private final ViewCategoryActivity activity;
    private CategoryAlbumAdapter adapter;
    private final FirebaseFirestore firebaseFirestore;
    private final double id;

    public CategoryThread(Context context, ViewCategoryActivity activity,double id){
        this.context=context;
        this.activity=activity;
        this.id=id;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    public void run(){

        ArrayList<AlbumModel> albumArray=new ArrayList<>();

        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",id)
                .orderBy("AlbumID", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){

                    albumArray.clear();

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
                        albumArray.add(model);

                    }

                    adapter=new CategoryAlbumAdapter(context, albumArray);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            activity.progressBar.setVisibility(View.GONE);
                            activity.recyclerView.setAdapter(adapter);
                        }
                    });

                }else{
                    activity.progressBar.setVisibility(View.GONE);
                }
            }
        });




    }

}
