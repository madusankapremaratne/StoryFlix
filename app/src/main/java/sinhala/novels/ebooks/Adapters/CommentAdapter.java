package sinhala.novels.ebooks.Adapters;

import static sinhala.novels.ebooks.MainActivity.mainActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import sinhala.novels.ebooks.Model.AlbumCommentModel;
import sinhala.novels.ebooks.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{

    private final ArrayList<AlbumCommentModel> arrayList;
    SimpleDateFormat previousFormat=new SimpleDateFormat("yyyyMMdd.0");
    SimpleDateFormat newFormat=new SimpleDateFormat("dd/MM/yyyy");
    private final Context context;

    public CommentAdapter(ArrayList<AlbumCommentModel> arrayList,Context context){
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int pi) {

        final int position=pi;

        holder.comment.setText(arrayList.get(position).getComment());
        holder.userName.setText(arrayList.get(position).getUserName());
        holder.date.setText(arrayList.get(position).getDate());

        if (arrayList.get(position).getProfileURL()!=null && !arrayList.get(position).getProfileURL().isEmpty()){
            Picasso.with(context).load(arrayList.get(position).getProfileURL()).into(holder.profileImage);
        }

        if (arrayList.get(position).getReply()!=null && !arrayList.get(position).getReply().isEmpty()){

            //TODO reply available

            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.reply.setText(arrayList.get(position).getReply());
            holder.replyDate.setText(arrayList.get(position).getReplyDate());

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (arrayList.get(position).getUserID().equals(mainActivity.userID)){
                    showLongClickDialog(position);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        TextView userName,comment,reply,date,replyDate;
        CircleImageView profileImage;
        RelativeLayout replyLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            userName=(TextView) itemView.findViewById(R.id.userName);
            comment=(TextView) itemView.findViewById(R.id.comment);
            reply=(TextView) itemView.findViewById(R.id.reply);
            date=(TextView) itemView.findViewById(R.id.date);
            replyDate=(TextView) itemView.findViewById(R.id.replyDate);
            profileImage=(CircleImageView) itemView.findViewById(R.id.profilePicture);
            replyLayout=(RelativeLayout) itemView.findViewById(R.id.replyLayout);

        }
    }

    private void showLongClickDialog(int position) {

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.comment_long_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        Button editBtn=dialog.findViewById(R.id.editBtn);
        Button deleteBtn=dialog.findViewById(R.id.deleteBtn);
        CircleImageView profileImage=dialog.findViewById(R.id.profilePicture);
        TextView userName=dialog.findViewById(R.id.userName);
        TextView comment=dialog.findViewById(R.id.comment);
        TextView date=dialog.findViewById(R.id.date);

        userName.setText(arrayList.get(position).getUserName());
        comment.setText(arrayList.get(position).getComment());
        date.setText(arrayList.get(position).getDate());
        if (arrayList.get(position).getProfileURL()!=null && !arrayList.get(position).getProfileURL().isEmpty()){
            Picasso.with(context).load(arrayList.get(position).getProfileURL()).into(profileImage);
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(position,dialog);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DocumentReference documentReference= FirebaseFirestore.getInstance().collection("AlbumComments").document(String.valueOf(arrayList.get(position).getID()));
                documentReference.delete();
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,arrayList.size());
            }
        });

        dialog.show();

    }

    private void showEditDialog(int position, Dialog mainDialog){

        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.edit_comment_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        EditText editText=dialog.findViewById(R.id.comment);
        Button saveBtn=dialog.findViewById(R.id.saveBtn);

        editText.setText(arrayList.get(position).getComment());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser()!=null){

                    String comment=editText.getText().toString();
                    if (comment.trim().isEmpty()){
                        editText.setError("Please enter your comment!");
                    }else{

                        DocumentReference documentReference=FirebaseFirestore.getInstance().collection("AlbumComments").document(String.valueOf(arrayList.get(position).getID()));
                        documentReference.update("Comment",comment);

                        arrayList.get(position).setComment(comment);
                        notifyItemChanged(position);

                        dialog.dismiss();
                        mainDialog.dismiss();
                    }

                }else{
                    Toast.makeText(context,"Please login first!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

}
