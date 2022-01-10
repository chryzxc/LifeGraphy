package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.potyvideo.library.AndExoPlayerView;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.nikartm.support.ImageBadgeView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Production extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    static List<ProductionMemberList> member_myLists;
    RecyclerView member_rv;
    ProductionMemberAdapter member_adapter;

    static List<ProductionRequestManageList> request_myLists;
    static ProductionRequestManageAdapter request_manage_adapter;

    static ProductionMemberManageAdapter member_manage_adapter;

    List<ProductionPhotosList> photos_myLists;
    RecyclerView photos_rv;
    ProductionPhotosAdapter photos_adapter;

    List<ProductionVideosList> videos_myLists;
    RecyclerView videos_rv;
    ProductionVideosAdapter videos_adapter;

    List<CoveredList> covered_myLists;
    RecyclerView covered_rv;
    CoveredAdapter covered_adapter;

    DatabaseReference databaseReference;
    static String productionID,productionName,creatorID,productionDescription,productionCovered,productionSkills,productionMinimum,productionMaximum;
    ValueEventListener productionListener;
    ValueEventListener memberListener;
    CardView productionManage,scheduleView;

    View manageLayout,photoUploadLayout,videoUploadLayout;
    androidx.appcompat.app.AlertDialog manageDialog,photoDialog,videoDialog;

    static RecyclerView member_manage_rv;
    static RecyclerView request_manage_rv;
    KProgressHUD hud;

    TextView productionUploadPhotos;
    TextView productionUploadVideos;

    CompactCalendarView calendarView;

    ValueEventListener bookingsListener;
    ConstraintLayout calendarLayout;
    ImageView editLogo;

    private Button bookButton;
    private View bookLayout,recommendedLayout;
    private LayoutInflater inflater;
    private androidx.appcompat.app.AlertDialog bookDialog,recommendDialog;
    Boolean hasConflict;
    static long reservationDate;

    Integer currentCount;
    static RecyclerView recommended_rv;

    static ArrayList<RecommenderList> recommenderLists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        calendarLayout = findViewById(R.id.calendarLayout);



        Intent intent = getIntent();
        if (null != intent) {
            productionID = intent.getStringExtra("production_id");
            loadProduction(productionID);

        }else{

            Toast.makeText(this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

        MaterialCardView productionTeamBack = findViewById(R.id.productionTeamBack);
        productionTeamBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Production.super.onBackPressed();
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


/*
        member_rv=(RecyclerView)findViewById(R.id.member_rec);
        member_rv.setHasFixedSize(true);
        member_rv.setLayoutManager(new GridLayoutManager(this,2));
        member_myLists=new ArrayList<>();
*/
        covered_rv = (RecyclerView) findViewById(R.id.covered_rec);
        covered_rv.setHasFixedSize(true);
        covered_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        covered_myLists = new ArrayList<>();

        member_rv = (RecyclerView) findViewById(R.id.member_rec);
        member_rv.setHasFixedSize(true);
        member_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        member_myLists = new ArrayList<>();

        request_myLists = new ArrayList<>();



        photos_rv = (RecyclerView) findViewById(R.id.photos_rec);
        photos_rv.setHasFixedSize(true);
        photos_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photos_myLists = new ArrayList<>();

        videos_rv = (RecyclerView) findViewById(R.id.videos_rec);
        videos_rv.setHasFixedSize(true);
        videos_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videos_myLists = new ArrayList<>();

        productionManage = findViewById(R.id.productionManage);
        scheduleView = findViewById(R.id.scheduleView);

        scheduleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLayout.setVisibility(VISIBLE);
                scheduleView.setVisibility(GONE);
            }
        });

        productionManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                manageLayout = LayoutInflater.from(Production.this).inflate(R.layout.manage_popup, null);

                TabLayout tabLayout = (TabLayout)manageLayout.findViewById(R.id.tabLayout);
                member_manage_rv = (RecyclerView) manageLayout.findViewById(R.id.member_manage_rec);
                request_manage_rv = (RecyclerView) manageLayout.findViewById(R.id.request_manage_rec);
                NestedScrollView manageSettings = (NestedScrollView)manageLayout.findViewById(R.id.manageSettings);


                member_manage_rv.setHasFixedSize(true);
                member_manage_rv.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));


                request_manage_rv.setHasFixedSize(true);
                request_manage_rv.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));


                member_manage_adapter = new ProductionMemberManageAdapter(member_myLists, Production.this);
                member_manage_rv.setAdapter(member_manage_adapter);
                member_manage_adapter.notifyDataSetChanged();

                request_manage_adapter = new ProductionRequestManageAdapter(request_myLists, Production.this);
                request_manage_rv.setAdapter(request_manage_adapter);
                request_manage_adapter.notifyDataSetChanged();




                TextInputEditText manageProductionName = (TextInputEditText)manageLayout.findViewById(R.id.manageProductionName);
                TextInputEditText manageDescription = (TextInputEditText)manageLayout.findViewById(R.id.manageDescription);
                TextInputEditText manageIndividualSkills = (TextInputEditText)manageLayout.findViewById(R.id.manageIndividualSkills);
                TextInputEditText manageCoveredEvents = (TextInputEditText)manageLayout.findViewById(R.id.manageCoveredEvents);
                TextInputEditText manageMinimum = (TextInputEditText)manageLayout.findViewById(R.id.manageMinimum);
                TextInputEditText manageMaximum = (TextInputEditText)manageLayout.findViewById(R.id.manageMaximum);
                Button manageSave = (Button)manageLayout.findViewById(R.id.manageSave);
                editLogo = (ImageView)manageLayout.findViewById(R.id.editLogo);
                Switch productionStatus = (Switch)manageLayout.findViewById(R.id.productionStatus);


                databaseReference.child("production_teams").child(productionID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Boolean.parseBoolean(task.getResult().child("active").getValue().toString()) == true){

                                productionStatus.setChecked(true);
                            }else{
                                productionStatus.setChecked(false);
                            }

                        }

                    }
                });

                productionStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){

                            Map<String, Object> data = new HashMap<>();
                            data.put("active",true);
                            databaseReference.child("production_teams").child(productionID).updateChildren(data);

                        }else{

                            Map<String, Object> data = new HashMap<>();
                            data.put("active",false);
                            databaseReference.child("production_teams").child(productionID).updateChildren(data);

                        }

                    }
                });

                manageProductionName.setText(productionName);
                manageDescription.setText(productionDescription);
                manageIndividualSkills.setText(productionSkills);
                manageCoveredEvents.setText(productionCovered);
                manageMinimum.setText(productionMinimum);
                manageMaximum.setText(productionMaximum);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("production_teams/"+productionID+"/logo/team_logo.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(Production.this).load(uri).centerCrop().into(editLogo);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



                editLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 400);

                    }
                });



                manageSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (manageProductionName.getText().toString().isEmpty()) {
                            manageProductionName.setError("Production name is required");
                            manageProductionName.requestFocus();
                            return;
                        } else if (manageDescription.getText().toString().isEmpty()) {
                            manageDescription.setError("Description is required");
                            manageDescription.requestFocus();
                            return;
                        } else if (manageIndividualSkills.getText().toString().isEmpty()) {
                            manageIndividualSkills.setError("Skills are required");
                            manageIndividualSkills.requestFocus();
                            return;
                        } else if (manageCoveredEvents.getText().toString().isEmpty()) {
                            manageCoveredEvents.setError("Covered events required");
                            manageCoveredEvents.requestFocus();
                            return;
                        } else if (manageMinimum.getText().toString().isEmpty()) {
                            manageMinimum.setError("Must have a minimum price");
                            manageMinimum.requestFocus();
                            return;
                        } else if (manageMaximum.getText().toString().isEmpty()) {
                            manageMaximum.setError("Must have a maximum price");
                            manageMaximum.requestFocus();
                            return;
                        } else if (Double.parseDouble(manageMinimum.getText().toString()) > Double.parseDouble(manageMaximum.getText().toString())) {
                            manageMinimum.setError("Minimum price must be lower than maximum price");
                            manageMinimum.requestFocus();
                            return;
                        } else if (editLogo.getDrawable() == null) {
                            Toast.makeText(Production.this, "Logo is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        hud = KProgressHUD.create(Production.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Updating details")
                                .setCancellable(false)
                                .setMaxProgress(100)
                                .show();


                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("production_teams/" + productionID + "/logo/team_logo.jpg");

                        editLogo.setDrawingCacheEnabled(true);
                        editLogo.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) editLogo.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        UploadTask uploadTask = parentRef.putBytes(bytes);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                hud.setLabel("Uploading logo: " + String.format("%.2f", progress) + "%");
                                hud.setProgress(Integer.valueOf((int) progress));
                                hud.setCancellable(false);
                                // Log.d(TAG, "Upload is " + progress + "% done");
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                //   Log.d(TAG, "Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(v.getContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                Map<String, Object> data = new HashMap<>();
                                data.put("production_name", manageProductionName.getText().toString());
                                data.put("price_minimum", Double.parseDouble(manageMinimum.getText().toString()));
                                data.put("price_maximum", Double.parseDouble(manageMaximum.getText().toString()));

                                data.put("description", manageDescription.getText().toString());
                                data.put("individual_skills", manageIndividualSkills.getText().toString());
                                data.put("events_covered", manageCoveredEvents.getText().toString());

                                databaseReference.child("production_teams").child(productionID).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(v.getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });


                    }
                });

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getText().toString()) {

                            case "Members":
                                member_manage_rv.setVisibility(VISIBLE);
                                request_manage_rv.setVisibility(View.INVISIBLE);
                                manageSettings.setVisibility(INVISIBLE);
                                member_manage_adapter = new ProductionMemberManageAdapter(member_myLists, Production.this);
                                member_manage_rv.setAdapter(member_manage_adapter);
                                member_manage_adapter.notifyDataSetChanged();
                                break;
                            case "Requests":
                                member_manage_rv.setVisibility(INVISIBLE);
                                request_manage_rv.setVisibility(View.VISIBLE);
                                manageSettings.setVisibility(INVISIBLE);
                                request_manage_adapter = new ProductionRequestManageAdapter(request_myLists, Production.this);
                                request_manage_rv.setAdapter(request_manage_adapter);
                                request_manage_adapter.notifyDataSetChanged();
                                break;
                            case "Settings":
                                member_manage_rv.setVisibility(INVISIBLE);
                                request_manage_rv.setVisibility(INVISIBLE);
                                manageSettings.setVisibility(VISIBLE);
                                break;

                            default:
                                break;
                        }



                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });



                builder.setView(manageLayout);
                manageDialog = builder.create();

                manageDialog.setCancelable(true);
                manageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                manageDialog.show();


            }
        });

        productionUploadPhotos = (TextView) findViewById(R.id.productionUploadPhotos);
        productionUploadVideos = (TextView) findViewById(R.id.productionUploadVideos);
        productionUploadPhotos.setPaintFlags(productionUploadPhotos.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        productionUploadVideos.setPaintFlags(productionUploadVideos.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        productionUploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                startActivityForResult(gallery, 200);


            }
        });

        productionUploadVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, 300);

            //    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
           //     startActivityForResult(gallery, 300);


            }
        });


        displayMembers();
        displayPhotos();
        displayVideos();
        loadBookings();

    }


    public void loadProduction(String productionID){
        TextView productionTeamName = (TextView) findViewById(R.id.productionTeamName);
        TextView productionTeamDescription = (TextView) findViewById(R.id.productionTeamDescription);
        TextView productionMembersCount = (TextView) findViewById(R.id.productionMembersCount);

        ImageView productionTeamLogo =(ImageView) findViewById(R.id.productionTeamLogo);


        productionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    covered_myLists.clear();

                    Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                    productionTeamName.setText(map.get("production_name").toString());
                    productionTeamDescription.setText(map.get("description").toString());

                    creatorID = map.get("creator_id").toString();
                    productionName = map.get("production_name").toString();
                    productionDescription = map.get("description").toString();
                    productionCovered=map.get("events_covered").toString();
                    productionSkills=map.get("individual_skills").toString();
                    productionMinimum=map.get("price_minimum").toString();
                    productionMaximum=map.get("price_maximum").toString();



                    if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getUid().matches(creatorID)){
                        productionManage.setVisibility(VISIBLE);
                    }else{
                        productionManage.setVisibility(GONE);
                    }

                    List<String> events_covered = Arrays.asList(map.get("events_covered").toString().split(","));


                    for (int x = 0; x < events_covered.size();x++){
                        covered_myLists.add(new CoveredList(events_covered.get(x)));
                    }
                    covered_adapter = new CoveredAdapter(covered_myLists, Production.this);
                    covered_rv.setAdapter(covered_adapter);

                }else{
                    Toast.makeText(Production.this, "Team doesnt exist", Toast.LENGTH_SHORT).show();
                    Map<String, Object> data = new HashMap<>();
                    data.put("production_name","None");
                    data.put("production_id","None");
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("production_team").setValue(data);
                    Production.super.onBackPressed();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        };
        databaseReference.child("production_teams").child(productionID).addValueEventListener(productionListener);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("production_teams/"+productionID+"/logo/team_logo.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(Production.this).load(uri).centerCrop().into(productionTeamLogo);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });



    }



    public void displayMembers(){


        Button productionJoin = (Button) findViewById(R.id.productionJoin);
        Button productionLeave= (Button) findViewById(R.id.productionLeave);
        Button productionBook= (Button) findViewById(R.id.productionBook);

        if (firebaseAuth.getCurrentUser() == null){
            productionJoin.setVisibility(GONE);
        }

        memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isMember = false;
                member_myLists.clear();
                request_myLists.clear();

                int count = 0;
                int notif = 0;

                for (DataSnapshot members : dataSnapshot.child("members").getChildren()) {
                    member_myLists.add(new ProductionMemberList(members.getKey()));
                    if (firebaseAuth.getCurrentUser() != null && members.getKey().matches(firebaseAuth.getCurrentUser().getUid())){
                        isMember = true;
                    }
                    count++;

                }


                productionJoin.setBackgroundColor((ContextCompat.getColor(Production.this, R.color.white)));
                productionJoin.setTextColor((ContextCompat.getColor(Production.this, R.color.black)));
                productionJoin.setText("Join");

                for (DataSnapshot request : dataSnapshot.child("member_request").getChildren()) {

                    if (firebaseAuth.getCurrentUser() != null && request.getKey().matches(firebaseAuth.getCurrentUser().getUid())){
                        productionJoin.setBackgroundColor((ContextCompat.getColor(Production.this, R.color.violet)));
                        productionJoin.setTextColor((ContextCompat.getColor(Production.this, R.color.white)));
                        productionJoin.setText("Cancel request");
                    }else{
                        notif++;
                        request_myLists.add(new ProductionRequestManageList(productionID,request.getKey()));
                    }

                }

                ImageBadgeView productionNotif = (ImageBadgeView) findViewById(R.id.productionNotif);
                productionNotif.setBadgeValue(notif);

                ConstraintLayout memberLayout = (ConstraintLayout) findViewById(R.id.memberLayout);
                ConstraintLayout nonmemberLayout = (ConstraintLayout) findViewById(R.id.nonmemberLayout);

                if (isMember == false){
                    memberLayout.setVisibility(GONE);
                    nonmemberLayout.setVisibility(VISIBLE);
                    productionUploadPhotos.setVisibility(GONE);
                    productionUploadVideos.setVisibility(GONE);
                }else{
                    productionUploadPhotos.setVisibility(VISIBLE);
                    productionUploadVideos.setVisibility(VISIBLE);
                    memberLayout.setVisibility(VISIBLE);
                    nonmemberLayout.setVisibility(GONE);
                }


                TextView productionMembersCount = (TextView) findViewById(R.id.productionMembersCount);
                if (count == 1){
                    productionMembersCount.setText("("+String.valueOf(count) + " member)");
                }else{
                    productionMembersCount.setText("("+String.valueOf(count) + " members)");
                }


                member_adapter = new ProductionMemberAdapter(member_myLists, Production.this);
                member_rv.setAdapter(member_adapter);
                member_adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("production_teams").child(productionID).orderByChild("production_name").addValueEventListener(memberListener);



        productionJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productionJoin.getText().toString().toLowerCase().matches("join")){

                    Map<String, Object> data = new HashMap<>();
                    data.put("date_requested", ServerValue.TIMESTAMP);

                    databaseReference.child("production_teams").child(productionID).child("member_request").child(firebaseAuth.getCurrentUser().getUid()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{

                    databaseReference.child("production_teams").child(productionID).child("member_request").child(firebaseAuth.getCurrentUser().getUid()).removeValue();


                }



            }
        });

        productionLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser().getUid().matches(creatorID)){


                    databaseReference.child("production_teams").child(productionID).removeEventListener(productionListener);
                    databaseReference.child("production_teams").child(productionID).orderByChild("production_name").removeEventListener(memberListener);

                    databaseReference.child("production_teams").child(productionID).removeValue();

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();


                    storageReference.child("production_teams/"+productionID+"/logo/team_logo.jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Production.super.onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Production.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }else{

                    databaseReference.child("production_teams").child(productionID).removeEventListener(productionListener);
                    databaseReference.child("production_teams").child(productionID).orderByChild("production_name").removeEventListener(memberListener);

                    databaseReference.child("production_teams").child(Production.productionID).child("members").child(firebaseAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Production.super.onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Production.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        });

        productionBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null){
                    View bookLayout;
                    recommenderLists = new ArrayList<>();

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                    bookLayout = LayoutInflater.from(Production.this).inflate(R.layout.book_popup, null);

                    SingleDateAndTimePicker bookPicker = bookLayout.findViewById(R.id.bookPicker);
                    TextView bookDate = bookLayout.findViewById(R.id.bookDate);
                    TextView bookTime = bookLayout.findViewById(R.id.bookTime);
                    Button bookCancel = bookLayout.findViewById(R.id.bookCancel);
                    Button bookConfirm = bookLayout.findViewById(R.id.bookConfirm);
                    TextInputEditText bookType=(TextInputEditText)bookLayout.findViewById(R.id.bookType);
                    TextInputEditText bookMessage=(TextInputEditText)bookLayout.findViewById(R.id.bookMessage);

                    bookDate.setText(DateFormat.format("MMM dd yyyy",new Date()));
                    bookTime.setText(DateFormat.format("hh:mm aa",new Date()));



                    bookPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(String displayed, Date date) {


                            reservationDate = date.getTime();


                            bookDate.setText(DateFormat.format("MMM dd yyyy",date));
                            bookTime.setText(DateFormat.format("hh:mm aa",date));

                        }
                    });

                    bookCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookDialog.dismiss();
                        }
                    });

                    bookConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {




                            if (bookDate.getText().toString().isEmpty()){
                                bookDate.setError("Date is required");
                                bookDate.requestFocus();
                                return;
                            }else if (bookTime.getText().toString().isEmpty()){
                                bookTime.setError("Time required");
                                bookTime.requestFocus();
                                return;
                            }else if (bookType.getText().toString().isEmpty()){
                                bookType.setError("Type required");
                                bookType.requestFocus();
                                return;
                            }else if (bookMessage.getText().toString().isEmpty()){
                                bookMessage.setError("Please input a message");
                                bookMessage.requestFocus();
                                return;
                            }


                            databaseReference.child("production_teams").child(productionID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (Boolean.parseBoolean(task.getResult().child("active").getValue().toString()) == true){



                                            if (new Date(reservationDate).after(new Date())){


                                                MainActivity.hud = KProgressHUD.create(Production.this)
                                                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                        .setLabel("Creating a reservation")
                                                        .setCancellable(false)
                                                        .show();

                                                String pushId = databaseReference.child("events").push().getKey();

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("booking_date", reservationDate);
                                                data.put("email", firebaseAuth.getCurrentUser().getEmail());
                                                data.put("type", bookType.getText().toString());
                                                data.put("message",bookMessage.getText().toString());
                                                data.put("client_id",firebaseAuth.getCurrentUser().getUid());
                                                data.put("production_id",productionID);
                                                data.put("status","ongoing");

                                                databaseReference.child("events").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        MainActivity.hud.dismiss();
                                                        bookDialog.dismiss();
                                                    }

                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        MainActivity.hud.dismiss();
                                                        Toast.makeText(Production.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            /*    hasConflict = false;


                                                databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if (!task.isSuccessful()) {

                                                        }
                                                        else {

                                                            for (DataSnapshot parent : task.getResult().getChildren()) {
                                                                Map<String, Object> data = (HashMap<String,Object>) parent.getValue();
                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");



                                                                if (productionID.matches(data.get("production_id").toString())){

                                                                    if (sdf.format(reservationDate).matches(sdf.format(data.get("booking_date")))){
                                                                        hasConflict = true;

                                                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                                                                        recommendedLayout = LayoutInflater.from(Production.this).inflate(R.layout.recommended_popup, null);
                                                                        recommended_rv = (RecyclerView)recommendedLayout.findViewById(R.id.recommended_rec);
                                                                        recommended_rv.setHasFixedSize(true);
                                                                        recommended_rv.setLayoutManager(new LinearLayoutManager(Production.this, LinearLayoutManager.VERTICAL, true));
                                                                        recommenderLists.clear();

                                                                        TextView bookError = (TextView)recommendedLayout.findViewById(R.id.bookError);
                                                                        bookError.setText("We are very sorry, The selected date is not available. We prepared you a list of available productions");
                                                                        currentCount = 0;

                                                                        for (int i = 0;i < MainActivity.productionList.size();i++){
                                                                            String productionId = MainActivity.productionList.get(i);
                                                                            currentCount +=1;
                                                                            Recommender recommender = new Recommender(productionId,0);

                                                                            if (!productionID.matches(productionId)){

                                                                                recommender.runRecommender(Production.this,currentCount,MainActivity.productionList.size(),true);

                                                                            }else{

                                                                            }



                                                                        }


                                                                        builder.setView(recommendedLayout);
                                                                        recommendDialog = builder.create();

                                                                        recommendDialog.setCancelable(true);
                                                                        recommendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                        recommendDialog.show();





                                                                        Toast.makeText(Production.this, "We are very sorry, The selected date is not available", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    }

                                                                }


                                                            }

                                                            if (hasConflict == false){

                                                                //     holder.bookButton.setText("Sent");
                                                                //     holder.bookButton.setBackgroundColor((ContextCompat.getColor(ct, R.color.violet)));
                                                                //    holder.bookButton.setTextColor((ContextCompat.getColor(ct, R.color.white)));



                                                                MainActivity.hud = KProgressHUD.create(Production.this)
                                                                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                                        .setLabel("Creating a reservation")
                                                                        .setCancellable(false)
                                                                        .show();

                                                                String pushId = databaseReference.child("events").push().getKey();

                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("booking_date", reservationDate);
                                                                data.put("email", firebaseAuth.getCurrentUser().getEmail());
                                                                data.put("type", bookType.getText().toString());
                                                                data.put("message",bookMessage.getText().toString());
                                                                data.put("client_id",firebaseAuth.getCurrentUser().getUid());
                                                                data.put("production_id",productionID);
                                                                data.put("status","ongoing");

                                                                databaseReference.child("events").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        MainActivity.hud.dismiss();
                                                                        bookDialog.dismiss();
                                                                    }

                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        MainActivity.hud.dismiss();
                                                                        Toast.makeText(Production.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }


                                                        }
                                                    }
                                                });

*/

                                            }else{
                                                Toast.makeText(Production.this, "Date must be in future", Toast.LENGTH_SHORT).show();
                                            }



                                        }else{

                                            // NOT ACTIVE


                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                                            recommendedLayout = LayoutInflater.from(Production.this).inflate(R.layout.recommended_popup, null);
                                            recommended_rv = (RecyclerView)recommendedLayout.findViewById(R.id.recommended_rec);
                                            recommended_rv.setHasFixedSize(true);
                                            recommended_rv.setLayoutManager(new LinearLayoutManager(Production.this, LinearLayoutManager.VERTICAL, true));
                                            recommenderLists.clear();

                                            TextView bookError = (TextView)recommendedLayout.findViewById(R.id.bookError);
                                            bookError.setText("Ops, We can't process your request right now because this production team won't accept booking request at the moment. Worry not and take a look on this list");
                                            currentCount = 0;

                                            for (int i = 0;i < MainActivity.productionList.size();i++){
                                                String productionIdGet = MainActivity.productionList.get(i);
                                                currentCount +=1;
                                                Recommender recommender = new Recommender(productionIdGet,0);

                                                if (!productionID.matches(productionIdGet)){

                                                    recommender.runRecommender(Production.this,currentCount,MainActivity.productionList.size(),true);

                                                }else{

                                                }

                                            }


                                            builder.setView(recommendedLayout);
                                            recommendDialog = builder.create();

                                            recommendDialog.setCancelable(true);
                                            recommendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            recommendDialog.show();



                                        }

                                    }

                                }
                            });



                        }
                    });


                    builder.setView(bookLayout);
                    bookDialog = builder.create();

                    bookDialog.setCancelable(true);
                    bookDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bookDialog.show();





                }else{
                    Toast.makeText(Production.this, "Please sign in to continue", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void displayPhotos(){

        TextView photosCount = findViewById(R.id.photosCount);
        photosCount.setText("(0)");

        TextView emptyPhotos = findViewById(R.id.emptyPhotos);


        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count =0;
                if (dataSnapshot.exists()){
                    photos_myLists.clear();
                    for (DataSnapshot photos : dataSnapshot.getChildren()) {
                        photos_myLists.add(new ProductionPhotosList(photos.getKey()));
                        count +=1;
                    }
                    photos_adapter = new ProductionPhotosAdapter(photos_myLists, Production.this);
                    photos_rv.setAdapter(photos_adapter);
                    photosCount.setText("("+String.valueOf(count)+")");
                    emptyPhotos.setVisibility(GONE);


                }else{
                    emptyPhotos.setVisibility(VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("production_teams").child(productionID).child("photos").addValueEventListener(imageListener);


    }

    public void displayVideos(){
        TextView videosCount = findViewById(R.id.videosCount);
        videosCount.setText("(0)");
        TextView emptyVideos = findViewById(R.id.emptyVideos);
        ValueEventListener videoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int count = 0;
                    for (DataSnapshot videos : dataSnapshot.getChildren()) {
                        videos_myLists.add(new ProductionVideosList(videos.getKey()));
                        count +=1;
                    }

                    videos_adapter = new ProductionVideosAdapter(videos_myLists, Production.this);
                    videos_adapter.notifyDataSetChanged();
                    videos_rv.setAdapter(videos_adapter);
                    videosCount.setText("("+String.valueOf(count)+")");
                    emptyVideos.setVisibility(GONE);
                }else{
                    emptyVideos.setVisibility(VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("production_teams").child(productionID).child("videos").addValueEventListener(videoListener);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                photoUploadLayout = LayoutInflater.from(Production.this).inflate(R.layout.upload_image_popup, null);
                ImageView photoView = (ImageView) photoUploadLayout.findViewById(R.id.photoView);
                Button photoUpload = (Button) photoUploadLayout.findViewById(R.id.photoUpload);

                photoView.setImageBitmap(selectedImage);

                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        gallery.setType("image/*");
                        startActivityForResult(gallery, 200);
                    }
                });

                photoUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        hud = KProgressHUD.create(Production.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Preparing")
                                .setMaxProgress(100)
                                .show();


                        String key = databaseReference.child("production_teams").child(productionID).child("photos").push().getKey();

                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("production_teams/"+productionID+"/photos/"+key);

                        photoView.setDrawingCacheEnabled(true);
                        photoView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        UploadTask uploadTask = parentRef.putBytes(bytes);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                hud.setLabel("Uploading photo: " + String.format("%.2f", progress) + "%");
                                hud.setProgress(Integer.valueOf((int) progress));
                                hud.setCancellable(false);
                                // Log.d(TAG, "Upload is " + progress + "% done");
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                //   Log.d(TAG, "Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                hud.dismiss();
                                Toast.makeText(v.getContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Map<String, Object> data = new HashMap<>();
                                data.put("date", ServerValue.TIMESTAMP);

                                databaseReference.child("production_teams").child(productionID).child("photos").child(key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hud.dismiss();
                                        photoDialog.dismiss();


                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(Production.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                        });


                    }
                });

                builder.setView(photoUploadLayout);
                photoDialog = builder.create();

                photoDialog.setCancelable(true);
                photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                photoDialog.show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Production.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 300 && resultCode == Activity.RESULT_OK){

            try {

                String path = data.getData().toString();
                Uri videoUri= data.getData();

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Production.this);

                videoUploadLayout = LayoutInflater.from(Production.this).inflate(R.layout.upload_video_popup, null);
                AndExoPlayerView videoView = (AndExoPlayerView)videoUploadLayout.findViewById(R.id.videoView);

                Button videoGallery = (Button) videoUploadLayout.findViewById(R.id.videoGallery);
                Button videoUpload = (Button) videoUploadLayout.findViewById(R.id.videoUpload);

                videoGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("video/*");
                        startActivityForResult(intent, 300);
                    }
                });

                videoUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        hud = KProgressHUD.create(Production.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Preparing")
                                .setMaxProgress(100)
                                .show();

                        String key = databaseReference.child("production_teams").child(productionID).child("videos").push().getKey();
                        Map<String, Object> data = new HashMap<>();
                        data.put("date", ServerValue.TIMESTAMP);

                        databaseReference.child("production_teams").child(productionID).child("videos").child(key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                StorageReference storageRef = firebaseStorage.getReference().child("production_teams/"+productionID+"/videos/"+key);
                                UploadTask uploadTask = storageRef.putFile(videoUri);

                                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        hud.setLabel("Uploading video: " + String.format("%.2f", progress) + "%");
                                        hud.setProgress(Integer.valueOf((int) progress));
                                        hud.setCancellable(false);

                                    }
                                });

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Production.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        hud.dismiss();
                                        videoDialog.dismiss();
                                        Toast.makeText(Production.this, "Video uploaded", Toast.LENGTH_SHORT).show();


                                    }
                                });



                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Production.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });

                HashMap<String , String> extraHeaders = new HashMap<>();
                extraHeaders.put("Lifegraphy","Video");
                videoView.setSource(path.toString(),extraHeaders);
                videoView.requestFocus();


                builder.setView(videoUploadLayout);
                videoDialog = builder.create();

                videoDialog.setCancelable(true);
                videoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                videoDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Production.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 400 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                editLogo.setImageBitmap(selectedImage);
                //   Glide.with(MainActivity.this).load(selectedImage).into(createLogo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Production.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }

    public void loadBookings(){


        TextView closeBookings = findViewById(R.id.closeBookings);

        closeBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLayout.setVisibility(GONE);
                scheduleView.setVisibility(VISIBLE);
            }
        });


        calendarView = findViewById(R.id.calendarView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        TextView calendarTitle = (TextView) findViewById(R.id.calendarTitle);

        if (bookingsListener != null){
            databaseReference.removeEventListener(bookingsListener);
        }


        bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;

                for (DataSnapshot parent : dataSnapshot.getChildren()) {

                    Map<String, Object> data = (HashMap<String,Object>) parent.getValue();
                    if (data.get("production_id").toString().matches(productionID)){

                        if (new Date(Long.parseLong(data.get("booking_date").toString())).after(new Date()) || new Date(Long.parseLong(data.get("booking_date").toString())).equals(new Date())){
                            count++;


                            Map<String, Object> details = new HashMap<>();
                            details.put("team_id",parent.getKey());
                            details.put("message",data.get("message").toString());
                            details.put("type",data.get("type").toString());
                            details.put("client_id",data.get("client_id").toString());


                            Event event = new Event(Color.RED, Long.parseLong(data.get("booking_date").toString()),details);
                            calendarView.addEvent(event);


                        }
                        ImageBadgeView appointmentNotif = (ImageBadgeView) findViewById(R.id.appointmentNotif);
                        appointmentNotif.setBadgeValue(count);


                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("events").orderByChild("booking_date").addValueEventListener(bookingsListener);

        MaterialCardView bookingCard = findViewById(R.id.bookingCard);

        SimpleDateFormat timeConverted = new SimpleDateFormat("HH:mm aa", Locale.getDefault());

        List<Event> event= calendarView.getEvents(calendarView.getFirstDayOfCurrentMonth());
        if (event.isEmpty()){
            bookingCard.setVisibility(GONE);
        }else{

            TextView bookCardClient = findViewById(R.id.bookCardClient);
            TextView bookCardTime = findViewById(R.id.bookCardTime);
            TextView bookCardMessage = findViewById(R.id.bookCardMessage);
            TextView bookCardType = findViewById(R.id.bookCardType);

            String data = event.toString();
            String[] splitted = data.replace("[Event{", "").replace("data={", "").replace("}", "").replace("}", "").replace("]", "").split(",");
            for (String item : splitted)
            {

                String[] separated = item.split("=");


                if (separated[0].trim().matches("timeInMillis")){
                    bookCardTime.setText(DateFormat.format("hh:mm aa",new Date(Long.parseLong(separated[1]))));



                }else if (separated[0].trim().matches("message")){

                    if (separated[1] == null){
                        bookCardMessage.setText("Message is empty");
                    }else{
                        bookCardMessage.setText(separated[1]);

                    }

                }
                else if (separated[0].trim().matches("type")){
                    bookCardType.setText(separated[1]);

                }else if (separated[0].trim().matches("client_id")){
                    String client_id = separated[1];

                    databaseReference.child("users").child(client_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {

                                Map<String, Object> map = (HashMap<String,Object>) task.getResult().getValue();

                                bookCardClient.setText(map.get("display_name").toString());



                            }

                        }
                    });
                }else if (separated[0].trim().matches("team_id")){


                }


            }


            bookingCard.setVisibility(VISIBLE);
        }

        calendarTitle.setText(simpleDateFormat.format(calendarView.getFirstDayOfCurrentMonth()));
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {


                List<Event> events = calendarView.getEvents(dateClicked);
                if (events.isEmpty()){
                    bookingCard.setVisibility(GONE);
                }else{


                    TextView bookCardClient = findViewById(R.id.bookCardClient);
                    TextView bookCardTime = findViewById(R.id.bookCardTime);
                    TextView bookCardMessage = findViewById(R.id.bookCardMessage);
                    TextView bookCardType = findViewById(R.id.bookCardType);

                    String data = events.toString();
                    String[] splitted = data.replace("[Event{", "").replace("data={", "").replace("}", "").replace("}", "").replace("]", "").split(",");
                    for (String item : splitted)
                    {

                        String[] separated = item.split("=");


                        if (separated[0].trim().matches("timeInMillis")){



                            bookCardTime.setText(DateFormat.format("hh:mm aa",new Date(Long.parseLong(separated[1]))));


                        }else if (separated[0].trim().matches("message")){
                            if (separated[1] == null){
                                bookCardMessage.setText("Message is empty");
                            }else{
                                bookCardMessage.setText(separated[1]);

                            }


                        }
                        else if (separated[0].trim().matches("type")){
                            bookCardType.setText(separated[1]);

                        }else if (separated[0].trim().matches("team_id")){

                           // newData.put("key", separated[1]);

                        }else if (separated[0].trim().matches("client_id")){

                            String client_id = separated[1];

                            databaseReference.child("users").child(client_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        Map<String, Object> map = (HashMap<String,Object>) task.getResult().getValue();

                                     bookCardClient.setText(map.get("display_name").toString());


                                        //    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                                    }

                                }
                            });

                        }




                    }




                    bookingCard.setVisibility(VISIBLE);
                }



            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTitle.setText(simpleDateFormat.format(firstDayOfNewMonth));

            }
        });






    }

    public static void setWindowFlag(Production activity, final int bits, boolean on) {
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