package sinhala.novels.ebooks.Adapters;

import static sinhala.novels.ebooks.MainActivity.isPremium;
import static sinhala.novels.ebooks.MainActivity.onTrial;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sinhala.novels.ebooks.BuyPremiumActivity;
import sinhala.novels.ebooks.Model.AlbumModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Handler;

import sinhala.novels.ebooks.MainActivity;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ViewAlbumActivity;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.viewHolder>{

    ArrayList<AlbumModel> arrayList;
    private final Context context;

    public SliderAdapter(Context context,ArrayList<AlbumModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int ps) {

        final int position=ps;

        holder.albumName.setText(arrayList.get(position).getAlbumName());
        holder.authorName.setText(arrayList.get(position).getAuthorName());

        if (arrayList.get(position).getCoverURL()!=null && !arrayList.get(position).getCoverURL().isEmpty()){
            Picasso.with(context).load(arrayList.get(position).getCoverURL()).into(holder.cover);
            Picasso.with(context).load(arrayList.get(position).getCoverURL()).into(holder.coverBack);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPremium || onTrial) {

                    Intent i = new Intent(context, ViewAlbumActivity.class)
                            .putExtra("albumName", arrayList.get(position).getAlbumName())
                            .putExtra("albumID", arrayList.get(position).getAlbumID())
                            .putExtra("categoryID", arrayList.get(position).getCategoryID())
                            .putExtra("coverURL", arrayList.get(position).getCoverURL())
                            .putExtra("viewCount", arrayList.get(position).getViewCount())
                            .putExtra("tagline", arrayList.get(position).getTagline())
                            .putExtra("previewText", arrayList.get(position).getPreviewText())
                            .putExtra("epiCount", arrayList.get(position).getEpiCount())
                            .putExtra("authorName", arrayList.get(position).getAuthorName());
                    context.startActivity(i);
                }else{
                    showPreviewDialog(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        TextView albumName,authorName;
        ImageView coverBack;
        ShapeableImageView cover;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            albumName=(TextView) itemView.findViewById(R.id.albumName);
            authorName=(TextView) itemView.findViewById(R.id.authorName);
            coverBack=(ImageView) itemView.findViewById(R.id.coverBack);
            cover=(ShapeableImageView) itemView.findViewById(R.id.cover1);

        }
    }


    private void showPreviewDialog(int position) {

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

        author.setText(arrayList.get(position).getAuthorName());
        albumName.setText(arrayList.get(position).getAlbumName());
        category.setText(getCategory(arrayList.get(position).getCategoryID()));
        epiCount.setText(convertEpiCount(arrayList.get(position).getEpiCount()));
        previewText.setText(arrayList.get(position).getPreviewText());
        tagline.setText(arrayList.get(position).getTagline());
        if (arrayList.get(position).getCoverURL()!=null && !arrayList.get(position).getCoverURL().isEmpty()){
            Picasso.with(context).load(arrayList.get(position).getCoverURL()).into(imageView);
        }

        previewText.setMovementMethod(new ScrollingMovementMethod());

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
