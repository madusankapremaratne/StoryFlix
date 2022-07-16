package sinhala.novels.ebooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sinhala.novels.ebooks.Model.MainModel;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.ViewCategoryActivity;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<MainModel> arrayList;

    public MainAdapter(Context context,ArrayList<MainModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int psi) {
        final int position=psi;
        if (arrayList.get(position).getArrayList()!=null){
            holder.recyclerView.setAdapter(new AlbumAdapter(context,arrayList.get(position).getArrayList()));
            holder.viewAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ViewCategoryActivity.class).putExtra("categoryName",arrayList.get(position).getName()).putExtra("CategoryID",(double) arrayList.get(position).getId()));
                }
            });
        }
        holder.categoryName.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView categoryName,viewAllBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView=(RecyclerView) itemView.findViewById(R.id.recyclerView);
            categoryName=(TextView) itemView.findViewById(R.id.categoryName);
            viewAllBtn=(TextView) itemView.findViewById(R.id.viewAllBtn);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
        }
    }

}
