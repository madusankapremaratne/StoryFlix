package sinhala.novels.ebooks.Threads;

import static sinhala.novels.ebooks.ViewAlbumActivity.epiArray;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import sinhala.novels.ebooks.Adapters.CommentAdapter;
import sinhala.novels.ebooks.Adapters.EpiAdapter;
import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.Model.AlbumCommentModel;
import sinhala.novels.ebooks.Model.EpiModel;
import sinhala.novels.ebooks.ViewAlbumActivity;

public class ViewAlbumThread extends Thread{

    public EpiAdapter adapter;
    ViewAlbumActivity activity;
    private final Context context;
    private final FirebaseFirestore firebaseFirestore;
    private final double albumID;
    private DbHandler dbHandler;
    private CommentAdapter commentAdapter;

    public ViewAlbumThread(double albumID,ViewAlbumActivity activity,Context context){
        this.albumID=albumID;
        this.activity=activity;
        this.context=context;
        firebaseFirestore=FirebaseFirestore.getInstance();
        dbHandler=new DbHandler(context);
    }

    public void run(){

        epiArray.clear();

        Query query=firebaseFirestore.collection("Episodes")
                .whereEqualTo("AlbumID",albumID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){

                    epiArray.clear();

                    for (QueryDocumentSnapshot data:value){

                        EpiModel model=new EpiModel(data.getDouble("EpiID"),
                                data.getDouble("AlbumID"),
                                data.getString("Content"),
                                data.getDouble("CreatedDate"),
                                data.getString("Title"));

                        epiArray.add(model);

                    }

                    if (epiArray.size()>0){
                        if (adapter==null) {
                            adapter = new EpiAdapter(epiArray, context, activity.model.getAlbumName(), activity.model.getCoverURL());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    activity.recyclerView.setAdapter(adapter);
                                }
                            });
                        }else{
                            adapter.notifyDataSetChanged();
                        }

                    }

                }
            }
        });

    }

    public void checkCount() {

        if (!dbHandler.isCounted(albumID)){

            double co=dbHandler.getReadCount(albumID);

            if (activity.model.getEpiCount()!=0 && (co == activity.model.getEpiCount())){
                activity.model.setViewCount(activity.model.getViewCount()+1);
                dbHandler.setNewCounted(albumID);

                new Handler(Looper.myLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        activity.viewCount.setText(convertViewCount(activity.model.getViewCount()));
                    }
                });

                DocumentReference documentReference=firebaseFirestore.collection("Albums").document(String.valueOf(albumID));
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null && task.getResult().exists()){

                            if (task.getResult().getDouble("ViewCount")!=null) {
                                double count = task.getResult().getDouble("ViewCount");
                                documentReference.update("ViewCount", count + 1);
                            }

                        }
                    }
                });

            }
        }

    }

    private String convertViewCount(double viewCount){
        NumberFormat numberFormat=new DecimalFormat("#,##0");
        return String.valueOf(numberFormat.format(viewCount));
    }

    public void loadComments(){

        if (activity.documentSnapshot==null) {

            Query query = firebaseFirestore.collection("AlbumComments")
                    .whereEqualTo("AlbumID", albumID)
                    .orderBy("ID", Query.Direction.DESCENDING)
                    .limit(8);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value!=null){

                        activity.commentsArray.clear();

                        for (QueryDocumentSnapshot data:value){

                            activity.commentsArray.add(new AlbumCommentModel(
                                    data.getString("ID"),
                                    data.getString("UserID"),
                                    data.getString("Comment"),
                                    data.getString("Reply"),
                                    data.getString("UserName"),
                                    data.getString("ProfileURL"),
                                    data.getString("Date"),
                                    data.getString("ReplyDate")));

                            activity.documentSnapshot=data;

                        }

                        commentAdapter=new CommentAdapter(activity.commentsArray,context);

                        activity.isLoading=false;
                        activity.startLoading=false;

                        new Handler(Looper.myLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                activity.cRecyclerView.setAdapter(commentAdapter);
                                activity.swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    }else{
                        //TODO Values are null
                        System.out.println("Values are null");
                        new Handler(Looper.myLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                activity.swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }
            });

        }else{

            Query query = firebaseFirestore.collection("AlbumComments")
                    .whereEqualTo("AlbumID", albumID)
                    .orderBy("ID", Query.Direction.DESCENDING)
                    .startAfter(activity.documentSnapshot)
                    .limit(8);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value!=null){

                        for (QueryDocumentSnapshot data:value){

                            activity.commentsArray.add(new AlbumCommentModel(
                                    data.getString("ID"),
                                    data.getString("UserID"),
                                    data.getString("Comment"),
                                    data.getString("Reply"),
                                    data.getString("UserName"),
                                    data.getString("ProfileURL"),
                                    data.getString("Date"),
                                    data.getString("ReplyDate")));

                            activity.documentSnapshot=data;

                        }

                        activity.isLoading=false;

                        new Handler(Looper.myLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                commentAdapter.notifyDataSetChanged();
                                activity.swipeRefreshLayout.setRefreshing(false);
                            }
                        });



                    }else{
                        //TODO Values are null
                        System.out.println("Values are null query");
                        new Handler(Looper.myLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                activity.swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }
            });

        }

    }

    public void addComment(AlbumCommentModel model) {

        Map<String,Object> comment=new HashMap<>();
        comment.put("ID",model.getID());
        comment.put("Comment",model.getComment());
        comment.put("Reply","");
        comment.put("AlbumName",model.getAlbumName());
        comment.put("CoverURL",model.getCoverURL());
        comment.put("AlbumID",model.getAlbumID());
        comment.put("UserName",model.getUserName());
        comment.put("UserID",model.getUserID());
        comment.put("ProfileURL",model.getProfileURL());
        comment.put("Date",model.getDate());
        comment.put("ReplyDate","");

        DocumentReference documentReference=firebaseFirestore.collection("AlbumComments").document(model.getID());
        documentReference.set(comment);

        activity.commentsArray.add(0,model);
        new Handler(Looper.myLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (commentAdapter==null){
                    commentAdapter=new CommentAdapter(activity.commentsArray,context);
                    activity.cRecyclerView.setAdapter(commentAdapter);
                }else{
                    commentAdapter.notifyItemInserted(0);
                    commentAdapter.notifyItemRangeChanged(0,activity.commentsArray.size());
                    activity.cRecyclerView.smoothScrollToPosition(0);
                }
            }
        });


    }
}
