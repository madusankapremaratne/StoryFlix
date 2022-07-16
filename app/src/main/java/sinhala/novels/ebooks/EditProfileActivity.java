package sinhala.novels.ebooks;

import static sinhala.novels.ebooks.MainActivity.mainActivity;
import static sinhala.novels.ebooks.MainActivity.profileDataChanged;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView profileImageEdit;
    RelativeLayout backBtn;
    EditText editText;
    Context context;
    ProgressBar progressBar;
    Button saveBtn;

    Bitmap imageBitmap;

    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private final int STORAGE_PERMISSION_CODE=100;

    String userID;
    Animation clickAnimation;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context=this;
        profileImageEdit=findViewById(R.id.profileImageEdit);
        backBtn=findViewById(R.id.backBtn);
        editText=findViewById(R.id.editText);
        progressBar=findViewById(R.id.progressBar);
        saveBtn=findViewById(R.id.saveBtn);
        userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        clickAnimation= AnimationUtils.loadAnimation(context,R.anim.click_animation);
        firebaseFirestore=FirebaseFirestore.getInstance();

        setUserData();

        someActivityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();

                            if (data!=null){
                                Uri uri=data.getData();
                                try {
                                    imageBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                                    int[] dimensions =convertImageDimensions(imageBitmap);
                                    imageBitmap=Bitmap.createScaledBitmap(imageBitmap,dimensions[0],dimensions[1],true);
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    imageBitmap.compress(Bitmap.CompressFormat.PNG,40,out);
                                    profileImageEdit.setImageBitmap(imageBitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
                });


        profileImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageEdit.startAnimation(clickAnimation);
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }else{
                    getImage();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.startAnimation(clickAnimation);
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {


                if (!MainActivity.isConnected){

                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(100);
                    }

                    Toast.makeText(context,"Please check your internet connection!",Toast.LENGTH_SHORT).show();

                }else{
                    saveBtn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    backBtn.setEnabled(false);
                    String name=editText.getText().toString();
                    if (name.trim().isEmpty()){
                        editText.setError("Please enter your name!");
                        saveBtn.setVisibility(View.VISIBLE);
                        backBtn.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{

                        if (imageBitmap!=null){

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            String child="profileImages/"+userID+"/"+convertImageName();
                            StorageReference mountainImagesRef = storageRef.child(child);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = mountainImagesRef.putBytes(data);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();

                                            DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
                                            Map<String,Object> userData=new HashMap<>();
                                            userData.put("Name",name);
                                            userData.put("ImageURL",imageUrl);


                                            SharedPreferences sharedPreferences=getSharedPreferences("UserData",MODE_PRIVATE);
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putString("userName",name);
                                            editor.putString("imageURL",imageUrl);
                                            editor.apply();

                                            documentReference.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){
                                                        profileDataChanged=true;
                                                        finish();
                                                    }
                                                }
                                            });

                                        }
                                    });



                                }
                            });

                        }else{

                            DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
                            Map<String,Object> userData=new HashMap<>();
                            userData.put("Name",name);

                            documentReference.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        SharedPreferences sharedPreferences=getSharedPreferences("UserData",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString("userName",name);
                                        editor.apply();

                                        profileDataChanged=true;

                                        finish();

                                    }
                                }
                            });


                        }

                    }

                }

            }
        });

    }

    private void setUserData() {

        editText.setText(mainActivity.sharedPreferences.getString("userName",""));

        if (!mainActivity.sharedPreferences.getString("imageURL","").isEmpty()){
            Picasso.with(context).load(mainActivity.sharedPreferences.getString("imageURL","")).placeholder(R.drawable.no_dp).into(profileImageEdit);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage();
            } else {
                Toast.makeText(context,"Without your permission, we can't access device storage!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getImage() {

        try {

            Intent objectIntent=new Intent();
            objectIntent.setType("image/*");
            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            someActivityResultLauncher.launch(objectIntent);

        }catch (Exception e){

            Toast.makeText(context,"Something went wrong!",Toast.LENGTH_SHORT).show();

        }

    }

    private int[] convertImageDimensions(Bitmap bitmap){

        int width=bitmap.getWidth();
        int height=bitmap.getHeight();

        if (width==height && width>250){
            width=250;
            height=250;
        }else if(width>height && width>250){

            double dHeight=Double.parseDouble(String.valueOf(height)+".00");
            double aR=dHeight/width;
            double newHeight=aR*250;
            height=convertToInt(newHeight);
            width=250;


        }else if(height>width && height>250){
            double dWidth=Double.parseDouble(String.valueOf(width)+".00");
            double aR=dWidth/height;
            double newWidth=aR*250;
            width=convertToInt(newWidth);
            height=250;
        }

        int[] dimensions={width,height};
        return dimensions;

    }

    private int convertToInt(double amount){
        NumberFormat formatter2 = new DecimalFormat("####00");
        String price=formatter2.format(amount);
        int ready=Integer.parseInt(price);
        int returnResult=ready;
        return returnResult;
    }

    private String convertImageName(){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyMMddHHmmss");
        return String.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())+".jpg");

    }

}