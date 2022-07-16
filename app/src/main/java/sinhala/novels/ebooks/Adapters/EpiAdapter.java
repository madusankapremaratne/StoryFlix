package sinhala.novels.ebooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sinhala.novels.ebooks.LocalDatabase.DbHandler;
import sinhala.novels.ebooks.Model.EpiModel;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ReadEpiActivity;

public class EpiAdapter extends RecyclerView.Adapter<EpiAdapter.viewHolder>{

    private final ArrayList<EpiModel> arrayList;
    private final Context context;
    private final DbHandler dbHandler;
    private final String albumName,coverURL;

    public EpiAdapter(ArrayList<EpiModel> arrayList, Context context, String albumName, String coverURL){
        this.arrayList=arrayList;
        this.context=context;
        dbHandler=new DbHandler(context);
        this.albumName=albumName;
        this.coverURL=coverURL;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.epi_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int psi) {
        final int position =psi;

        if (dbHandler.isBookmarked(arrayList.get(position).getEpiID())){
            holder.bookmark.setVisibility(View.VISIBLE);
        }else{
            holder.bookmark.setVisibility(View.INVISIBLE);
        }

        holder.clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ReadEpiActivity.class)
                        .putExtra("position",position)
                        .putExtra("max",arrayList.size()-1)
                        .putExtra("albumName",albumName)
                        .putExtra("coverURL",coverURL));
            }
        });
        holder.epiName.setText(arrayList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        RelativeLayout clickLayout;
        TextView epiName;
        ImageView bookmark;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            clickLayout=(RelativeLayout) itemView.findViewById(R.id.clickLayout);
            epiName=(TextView) itemView.findViewById(R.id.epiName);
            bookmark=(ImageView) itemView.findViewById(R.id.bookmark);
        }
    }

}
