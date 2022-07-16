package sinhala.novels.ebooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import sinhala.novels.ebooks.Model.AlbumModel;
import sinhala.novels.ebooks.Model.CategoryModel;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ViewCategoryActivity;


import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder>{

    Context context;
    ArrayList<CategoryModel> arrayList;
    private final FirebaseFirestore firebaseFirestore;

    public CategoryAdapter(Context context,ArrayList<CategoryModel> arrayList,FirebaseFirestore firebaseFirestore){
        this.context=context;
        this.arrayList=arrayList;
        this.firebaseFirestore=firebaseFirestore;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder hl, int psi) {
        final int position=psi;
        final viewHolder holder=hl;
        holder.textView.setText(arrayList.get(position).getName());

        ArrayList<AlbumModel > albumArray=new ArrayList<>();

        Query query=firebaseFirestore.collection("Albums")
                .whereEqualTo("CategoryID",arrayList.get(position).getCategoryID())
                .orderBy("AlbumID", Query.Direction.DESCENDING)
                .limit(4);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){

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

                    if (albumArray.size()==0){
                        holder.progressBar.setVisibility(View.GONE);
                        holder.noDataAvailable.setVisibility(View.VISIBLE);
                    }else{
                        holder.progressBar.setVisibility(View.GONE);
                        holder.noDataAvailable.setVisibility(View.GONE);
                        AlbumAdapter albumAdapter=new AlbumAdapter(context,albumArray);
                        holder.recyclerView.setAdapter(albumAdapter);
                    }

                }else{
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        });

        holder.viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewAllBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(new Intent(context, ViewCategoryActivity.class).putExtra("categoryName",arrayList.get(position).getName()).putExtra("CategoryID",arrayList.get(position).getCategoryID()));
                    }
                },100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView,viewAllBtn,noDataAvailable;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
            textView=(TextView) itemView.findViewById(R.id.textView);
            viewAllBtn=(TextView) itemView.findViewById(R.id.viewAllBtn);
            noDataAvailable=(TextView) itemView.findViewById(R.id.noDataAvailable);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

}
