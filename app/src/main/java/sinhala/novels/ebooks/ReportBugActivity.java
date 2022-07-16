package sinhala.novels.ebooks;

import static sinhala.novels.ebooks.MainActivity.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class ReportBugActivity extends AppCompatActivity {

    private Context context;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        context=this;
        RelativeLayout backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },100);
            }
        });

        firebaseFirestore=FirebaseFirestore.getInstance();

        Button submitBtn=findViewById(R.id.submitBtn);
        EditText editText=findViewById(R.id.editText);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(context,"You are an unidentified user! Please register to continue",Toast.LENGTH_SHORT).show();
                }else{
                    String text=editText.getText().toString();
                    if (text.trim().isEmpty()){
                        editText.setError("Please describe your problem!");
                    }else{

                        String id=generateID();
                        DocumentReference documentReference=firebaseFirestore.collection("Bugs").document(id);

                        HashMap<String,Object> data=new HashMap<>();
                        data.put("ID",id);
                        data.put("Date",getDate());
                        data.put("Text",text);
                        data.put("UserID",mainActivity.userID);
                        data.put("DeviceName",getDeviceName());
                        data.put("DeviceOS",getOS());

                        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context,"Thank you for notifying us!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(context,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }
            }
        });

    }

    private String getDeviceName() {
        return String.valueOf(Build.MANUFACTURER+"  "+Build.MODEL);
    }

    private String getOS(){
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    private String getDate() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private String generateID() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd.HHmmss");
        String firstPart=dateFormat.format(Calendar.getInstance().getTime());
        return String.valueOf(firstPart+String.valueOf(UUID.randomUUID().toString()));
    }

}