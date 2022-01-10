package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VisitProfile extends AppCompatActivity {

    List<VisitPostList> visitpost_myLists;
    RecyclerView visitpost_rv;
    VisitPostAdapter visitpost_adapter;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String userId;
    String productionId;

    TextView visitDisplayName;
    TextView visitInfo;
    TextView visitTeam;
    TextView visitPostCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        visitDisplayName = (TextView) findViewById(R.id.visitDisplayName) ;
        visitInfo = (TextView) findViewById(R.id.visitInfo) ;
        visitTeam = (TextView) findViewById(R.id.visitTeam) ;
        visitPostCount = (TextView) findViewById(R.id.visitPostCount) ;





        visitpost_rv=(RecyclerView)findViewById(R.id.visitpost_rec);
        visitpost_rv.setHasFixedSize(true);
        visitpost_rv.setLayoutManager(new GridLayoutManager(this,2,LinearLayoutManager.VERTICAL,true));
        visitpost_myLists=new ArrayList<>();

        Intent intent = getIntent();
        if (null != intent) {
            String userID= intent.getStringExtra("id");
            loadProfile(userID);
            displayPost(userID);
        }else{
            Toast.makeText(this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }



        MaterialCardView visitBack = (MaterialCardView) findViewById(R.id.visitBack);
        visitBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitProfile.super.onBackPressed();
            }
        });



        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    public void loadProfile(String userID){
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                visitDisplayName.setText(map.get("display_name").toString());
                visitInfo.setText(map.get("info").toString());
                userId = dataSnapshot.getKey();



                for (DataSnapshot prod : dataSnapshot.child("production_team").getChildren()) {
                    if (prod.getKey().matches("production_name")){
                        visitTeam.setText(prod.getValue().toString());
                    }
                    if (prod.getKey().matches("production_id")){
                        productionId = prod.getValue().toString();
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        };
        databaseReference.child("users").child(userID).addValueEventListener(profileListener);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users/"+userID+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                ImageView coverPhoto = (ImageView) findViewById(R.id.visitPhoto);

                Glide.with(VisitProfile.this).load(uri).centerCrop().into(coverPhoto);

                coverPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VisitProfile.this, ImageViewer.class);
                        intent.putExtra("url", "users/"+userID+"/profile/profile_picture.jpg");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        VisitProfile.this.startActivity(intent);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }

    public void displayPost(String userID){


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                visitpost_myLists.clear();
                int count= 0;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    Map<String, Object> mypost = (HashMap<String,Object>) dsp.getValue();
                    if (mypost.get("user_id").toString().matches(userID)){
                        count++;
                        visitpost_myLists.add(new VisitPostList(dsp.getKey(),userID));

                    }

                }

                if (count == 0){

                    visitpost_rv.setVisibility(View.GONE);
                }else{
                    visitpost_rv.setVisibility(View.VISIBLE);
                }

                if (String.valueOf(count).matches("1")){
                    visitPostCount.setText(String.valueOf(count)+" Post");
                }else{
                    visitPostCount.setText(String.valueOf(count)+" Posts");
                }

                visitpost_adapter = new VisitPostAdapter(visitpost_myLists, VisitProfile.this);
                visitpost_rv.setAdapter(visitpost_adapter);
                visitpost_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("posts").orderByChild("date").addValueEventListener(postListener);

    }

    public static void setWindowFlag(VisitProfile activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}