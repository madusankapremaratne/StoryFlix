package sinhala.novels.ebooks.Threads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import sinhala.novels.ebooks.Adapters.EpiCommentAdapter;
import sinhala.novels.ebooks.Model.EpiCommentModel;
import sinhala.novels.ebooks.ReadEpiActivity;

public class EpiThread extends Thread{

    private final Context context;
    private ReadEpiActivity activity;
    private FirebaseFirestore firebaseFirestore;
    double epiID;
    private EpiCommentAdapter commentAdapter;

    public EpiThread(Context context,ReadEpiActivity activity,double epiID){
        this.context=context;
        this.activity=activity;
        this.epiID=epiID;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    public void run(){}

    public void loadComments(){

        if (activity.documentSnapshot==null) {

            Query query = firebaseFirestore.collection("EpisodeComments")
                    .whereEqualTo("EpiID", epiID)
                    .orderBy("ID", Query.Direction.DESCENDING)
                    .limit(8);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value!=null){

                        activity.commentsArray.clear();

                        for (QueryDocumentSnapshot data:value){

                            activity.commentsArray.add(new EpiCommentModel(
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

                        commentAdapter=new EpiCommentAdapter(activity.commentsArray,context);

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

            Query query = firebaseFirestore.collection("EpisodeComments")
                    .whereEqualTo("EpiID", epiID)
                    .orderBy("ID", Query.Direction.DESCENDING)
                    .startAfter(activity.documentSnapshot)
                    .limit(8);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value!=null){

                        for (QueryDocumentSnapshot data:value){

                            activity.commentsArray.add(new EpiCommentModel(
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

    public void addComment(EpiCommentModel model) {

        Map<String,Object> comment=new HashMap<>();
        comment.put("ID",model.getId());
        comment.put("Comment",model.getComment());
        comment.put("Reply","");
        comment.put("AlbumName",model.getAlbumName());
        comment.put("CoverURL",model.getCoverURL());
        comment.put("AlbumID",model.getAlbumID());
        comment.put("EpiID",model.getEpiID());
        comment.put("UserName",model.getUserName());
        comment.put("UserID",model.getUserID());
        comment.put("ProfileURL",model.getProfileURL());
        comment.put("Date",model.getDate());
        comment.put("ReplyDate","");

        DocumentReference documentReference=firebaseFirestore.collection("EpisodeComments").document(model.getId());
        documentReference.set(comment);

        activity.commentsArray.add(0,model);
        new Handler(Looper.myLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (commentAdapter==null){
                    commentAdapter=new EpiCommentAdapter(activity.commentsArray,context);
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
