package sinhala.novels.ebooks.Adapters;

import static sinhala.novels.ebooks.MainActivity.isPremium;
import static sinhala.novels.ebooks.MainActivity.onTrial;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import sinhala.novels.ebooks.BuyPremiumActivity;
import sinhala.novels.ebooks.LocalDatabase.LocalFavModel;
import sinhala.novels.ebooks.Model.AlbumModel;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ViewAlbumActivity;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.viewHolder>{

    Context context;
    private final ArrayList<LocalFavModel> arrayList;
    private final Dialog dialog;
    private final FirebaseFirestore firebaseFirestore;

    public FavoriteAdapter(Context context, ArrayList<LocalFavModel> arrayList, Dialog dialog){
        this.arrayList=arrayList;
        this.context=context;
        this.dialog=dialog;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_album_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int psi) {

        final int position=psi;

        holder.albumName.setText(arrayList.get(position).getAlbumName());
        holder.artistName.setText(arrayList.get(position).getAuthorName());
        if (arrayList.get(position).getImageURL()!=null && !arrayList.get(position).getImageURL().isEmpty()){
            Picasso.with(context).load(arrayList.get(position).getImageURL()).into(holder.imageView);
        }

        holder.clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                DocumentReference reference=firebaseFirestore.collection("Albums").document(String.valueOf(arrayList.get(position).getAlbumID()));
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null && documentSnapshot.exists()){

                            AlbumModel model=new AlbumModel(documentSnapshot.getString("AlbumName"),
                                    documentSnapshot.getDouble("AlbumID"),
                                    documentSnapshot.getDouble("CategoryID"),
                                    documentSnapshot.getString("CoverURL"),
                                    documentSnapshot.getDouble("ViewCount"),
                                    documentSnapshot.getString("Tagline"),
                                    documentSnapshot.getString("PreviewText"),
                                    documentSnapshot.getDouble("EpiCount"),
                                    documentSnapshot.getDouble("CreatedDate"),
                                    documentSnapshot.getString("AuthorName"));

                            dialog.dismiss();

                            if (isPremium || onTrial) {

                                Intent i = new Intent(context, ViewAlbumActivity.class)
                                        .putExtra("albumName", documentSnapshot.getString("AlbumName"))
                                        .putExtra("albumID", documentSnapshot.getDouble("AlbumID"))
                                        .putExtra("categoryID", documentSnapshot.getDouble("CategoryID"))
                                        .putExtra("coverURL", documentSnapshot.getString("CoverURL"))
                                        .putExtra("viewCount", documentSnapshot.getDouble("ViewCount"))
                                        .putExtra("tagline", documentSnapshot.getString("Tagline"))
                                        .putExtra("previewText", documentSnapshot.getString("PreviewText"))
                                        .putExtra("epiCount", documentSnapshot.getDouble("EpiCount"))
                                        .putExtra("authorName", documentSnapshot.getString("AuthorName"));
                                context.startActivity(i);

                            }else{
                                showPreviewDialog(model);
                            }

                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView albumName,artistName;
        RelativeLayout clickLayout;
        ShapeableImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            clickLayout=(RelativeLayout)itemView.findViewById(R.id.clickLayout);
            albumName=(TextView)itemView.findViewById(R.id.albumName);
            artistName=(TextView)itemView.findViewById(R.id.artistName);
            imageView=(ShapeableImageView) itemView.findViewById(R.id.imageView);
        }
    }


    private void showPreviewDialog(AlbumModel model) {

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.preview_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView author,albumName,category,epiCount,previewText,tagline;
        ShapeableImageView imageView;
        Button upgradeBtn;

        author=dialog.findViewById(R.id.author);
        albumName=dialog.findViewById(R.id.albumName);
        category=dialog.findViewById(R.id.category);
        epiCount=dialog.findViewById(R.id.epiCount);
        previewText=dialog.findViewById(R.id.previewText);
        tagline=dialog.findViewById(R.id.tagline);
        imageView=dialog.findViewById(R.id.imageView);
        upgradeBtn=dialog.findViewById(R.id.upgradeBtn);

        author.setText(model.getAuthorName());
        albumName.setText(model.getAlbumName());
        category.setText(getCategory(model.getCategoryID()));
        epiCount.setText(convertEpiCount(model.getEpiCount()));
        previewText.setText(model.getPreviewText());
        tagline.setText(model.getTagline());
        if (model.getCoverURL()!=null && !model.getCoverURL().isEmpty()){
            Picasso.with(context).load(model.getCoverURL()).into(imageView);
        }

        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                context.startActivity(new Intent(context, BuyPremiumActivity.class));
            }
        });

        dialog.show();


    }

    private String getCategory(double categoryID) {
        if (categoryID==0){
            return String.valueOf("Novels");
        }else if (categoryID==1){
            return String.valueOf("Short Stories");
        }else if (categoryID==2){
            return String.valueOf("Translation");
        }else{
            return String.valueOf("Other");
        }
    }

    private String convertEpiCount(double episodesCount) {
        NumberFormat numberFormat=new DecimalFormat("#,##0");
        return String.valueOf("EP "+String.valueOf(numberFormat.format(episodesCount)));
    }


}
