package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.kaopiz.kprogresshud.KProgressHUD;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccy.focuslayoutmanager.FocusLayoutManager;


import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static ccy.focuslayoutmanager.FocusLayoutManager.dp2px;

public class MainActivity extends AppCompatActivity {
    Toolbar mActionBarToolbar;
    static Boolean hasATeam;

    static List<PostList> post_myLists;
    static RecyclerView post_rv;
    static PostAdapter post_adapter;

    List<SelectionList> selection_myLists;
    RecyclerView selection_rv;
    SelectionAdapter selection_adapter;

    List<ProductionList> production_myLists;
    RecyclerView production_rv;
    ProductionAdapter production_adapter;

    static List<FindList> find_production_myLists;
    static RecyclerView find_production_rv;
    static FindAdapter find_production_adapter;

    static List<BookingsList> bookings_myLists;
    static RecyclerView bookings_rv;
    static BookingsAdapter bookings_adapter;
    Switch postFilter;
    TextView filterText;
    ValueEventListener postListener;
    ConstraintLayout userLayout;



    View parentLayout;

    CardView postCard;
    LinearLayout linearUpload,linearCreate;
    EditText postStatus;

    static LayoutInflater inflater;
    static View myLayout;
    androidx.appcompat.app.AlertDialog postDialog,createDialog,categoryDialog;
    static androidx.appcompat.app.AlertDialog findDialog;
    Context context;

    NestedScrollView homeView,productionView,bookingsView;
    static ChipNavigationBar chipNavigationBar;

    FirebaseFirestore db;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    static String profileID,profileDisplayName,profileTeamName,profileTeamId;

    ImageView popupImage;
    ImageView createLogo;

    ConstraintLayout popupImageContainer;
    static KProgressHUD hud;
    static List<String> productionList,userCategories;
    static Long findSelectedDate;

    static TagsAdapter tags_adapter;
    static List<TagsList> tags_myLists;
    static RecyclerView tags_rv;
    List<String> tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        post_rv = (RecyclerView) findViewById(R.id.post_rec);
        post_rv.setHasFixedSize(true);
        post_rv.setLayoutManager(linearLayoutManager);
        post_myLists = new ArrayList<>();


    //    selection_rv = (RecyclerView) findViewById(R.id.selection_rec);
     //   selection_rv.setHasFixedSize(true);
    //    selection_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        selection_myLists = new ArrayList<>();


        production_rv = (RecyclerView) findViewById(R.id.production_rec);
        production_rv.setHasFixedSize(true);
        production_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        production_myLists = new ArrayList<>();


        bookings_rv = (RecyclerView) findViewById(R.id.bookings_rec);
        bookings_rv.setHasFixedSize(true);
        bookings_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        bookings_myLists = new ArrayList<>();

        homeView = findViewById(R.id.homeView);
        productionView = findViewById(R.id.productionView);
        bookingsView = findViewById(R.id.bookingsView);

        chipNavigationBar = findViewById(R.id.menu);

        MaterialCardView signoutUser = (MaterialCardView) findViewById(R.id.signoutUser);
        signoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Signin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        SearchView searchTeam;
        ConstraintLayout searchLayout = (ConstraintLayout) findViewById(R.id.searchLayout);
        ConstraintLayout teamLayout = (ConstraintLayout) findViewById(R.id.teamLayout);
        ImageView searchButton = (ImageView) findViewById(R.id.searchButton);
        searchTeam = findViewById(R.id.searchTeam);
        BottomAppBar bottom_app_bar = (BottomAppBar) findViewById(R.id.bottom_app_bar);

        searchTeam.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchLayout.setVisibility(GONE);
                if (firebaseAuth.getCurrentUser() != null){
                    teamLayout.setVisibility(VISIBLE);
                }

                searchButton.setVisibility(VISIBLE);
                return false;
            }
        });



        searchTeam.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                if (text.matches("[a-zA-Z- .]+")){
                    filterByName(text);

                }else {
                    filterByName(text);
                }
                return true;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchLayout.setVisibility(VISIBLE);
                teamLayout.setVisibility(GONE);
                searchButton.setVisibility(GONE);
                searchTeam.setIconified(false);

            }
        });

        postFilter = (Switch) findViewById(R.id.postFilter);
        filterText = (TextView) findViewById(R.id.filterText);

        if (firebaseAuth.getCurrentUser() != null){
            postFilter.setVisibility(VISIBLE);
            filterText.setVisibility(VISIBLE);
        }else{
            postFilter.setVisibility(GONE);
            filterText.setVisibility(GONE);
        }

        postFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    displayPost();

                }else{
                    displayPost();

                }
            }
        });

        parentLayout = findViewById(android.R.id.content);

        MaterialCardView homePhoto = (MaterialCardView) findViewById(R.id.homePhoto);
        homePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });


        final CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar_layout.setTitle("LifeGraphy");
                    if (firebaseAuth.getCurrentUser() != null){
                        homePhoto.setVisibility(INVISIBLE);
                    }else{
                        homePhoto.setVisibility(GONE);
                    }

                    isShow = true;
                } else if(isShow) {
                    toolbar_layout.setTitle("LifeGraphy");

                    toolbar_layout.setExpandedTitleColor(Color.WHITE);
                    if (firebaseAuth.getCurrentUser() != null){
                        homePhoto.setVisibility(VISIBLE);
                    }else{
                        homePhoto.setVisibility(GONE);
                    }



                    isShow = false;
                }
            }
        });
        userLayout = findViewById(R.id.userLayout);



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


        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        ImageView image_toolbar = (ImageView) findViewById(R.id.image_toolbar);
        setSupportActionBar(mActionBarToolbar);

    //    userLayout.setVisibility(VISIBLE);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);



      //  Glide.with(this).load(R.drawable.background).centerCrop().into(new SimpleTarget<Drawable>() {
      //      @Override
      //      public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
      //          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      //              coordinatorLayout.setBackground(resource);
      //          }
      //      }
      //  });


        Glide.with(this).load(R.drawable.cover).centerCrop().into(image_toolbar);

        FloatingActionButton userFab = (FloatingActionButton) findViewById(R.id.userFab);
        userFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });


        CardView postText = findViewById(R.id.postText);
        postCard = findViewById(R.id.postCard);
        postStatus = (EditText) findViewById(R.id.postStatus);
        linearCreate = findViewById(R.id.linearCreate);
        linearUpload = findViewById(R.id.linearUpload);
        inflater = getLayoutInflater();


        postCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayPopupPost();

            }
        });

        linearCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupPost();

            }
        });

        linearUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupPost();

            }
        });

        postStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupPost();
            }
        });



        chipNavigationBar.setItemSelected(R.id.home, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i) {

                    case R.id.home:

                        homeView.setVisibility(VISIBLE);
                        productionView.setVisibility(INVISIBLE);
                        bookingsView.setVisibility(INVISIBLE);



                        break;
                    case R.id.production:

                        homeView.setVisibility(INVISIBLE);
                        productionView.setVisibility(VISIBLE);
                        bookingsView.setVisibility(INVISIBLE);
                        break;

                    case R.id.bookings:

                        homeView.setVisibility(INVISIBLE);
                        productionView.setVisibility(INVISIBLE);
                        bookingsView.setVisibility(VISIBLE);
                        break;

                    default:
                        break;
                }
            }
        });


        MaterialCardView createTeam = (MaterialCardView) findViewById(R.id.createTeam);
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                myLayout = inflater.inflate(R.layout.create_popup, null);


                TextInputEditText createProductionName = (TextInputEditText)myLayout.findViewById(R.id.createProductionName);
                TextInputEditText createDescription = (TextInputEditText)myLayout.findViewById(R.id.createDescription);
                TextInputEditText createIndividualSkills = (TextInputEditText)myLayout.findViewById(R.id.createIndividualSkills);
                TextInputEditText createCoveredEvents = (TextInputEditText)myLayout.findViewById(R.id.createCoveredEvents);
                TextInputEditText createMinimum = (TextInputEditText)myLayout.findViewById(R.id.createMinimum);
                TextInputEditText createMaximum = (TextInputEditText)myLayout.findViewById(R.id.createMaximum);
                Button createSave = (Button)myLayout.findViewById(R.id.createSave);
                createLogo = (ImageView)myLayout.findViewById(R.id.createLogo);

                createLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 300);

                    }
                });



                createSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (createProductionName.getText().toString().isEmpty()){
                            createProductionName.setError("Production name is required");
                            createProductionName.requestFocus();
                            return;
                        }else if (createDescription.getText().toString().isEmpty()){
                            createDescription.setError("Description is required");
                            createDescription.requestFocus();
                            return;
                        }else if (createIndividualSkills.getText().toString().isEmpty()){
                            createIndividualSkills.setError("Skills are required");
                            createIndividualSkills.requestFocus();
                            return;
                        }else if (createCoveredEvents.getText().toString().isEmpty()){
                            createCoveredEvents.setError("Covered events required");
                            createCoveredEvents.requestFocus();
                            return;
                        }else if (createMinimum.getText().toString().isEmpty()){
                            createMinimum.setError("Must have a minimum price");
                            createMinimum.requestFocus();
                            return;
                        }else if (createMaximum.getText().toString().isEmpty()){
                            createMaximum.setError("Must have a maximum price");
                            createMaximum.requestFocus();
                            return;
                        }else if (Double.parseDouble(createMinimum.getText().toString()) > Double.parseDouble(createMaximum.getText().toString())){
                            createMinimum.setError("Minimum price must be lower than maximum price");
                            createMinimum.requestFocus();
                            return;
                        }else if (createLogo.getDrawable() == null){
                            Toast.makeText(MainActivity.this, "Logo is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        hud = KProgressHUD.create(MainActivity.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Creating your team")
                                .setCancellable(false)
                                .setMaxProgress(100)
                                .show();


                        String pushId = databaseReference.child("posts").push().getKey();


                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("production_teams/"+pushId+"/logo/team_logo.jpg");

                        createLogo.setDrawingCacheEnabled(true);
                        createLogo.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) createLogo.getDrawable()).getBitmap();
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
                                data.put("production_name", createProductionName.getText().toString());
                                data.put("creator_id",firebaseAuth.getCurrentUser().getUid());
                                data.put("date", ServerValue.TIMESTAMP);
                                data.put("events", null);
                                data.put("active",true);

                                data.put("price_minimum",Double.parseDouble(createMinimum.getText().toString()));
                                data.put("price_maximum", Double.parseDouble(createMaximum.getText().toString()));

                                data.put("photos", null);
                                data.put("description", createDescription.getText().toString());
                                data.put("individual_skills", createIndividualSkills.getText().toString());
                                data.put("events_covered", createCoveredEvents.getText().toString());

                                databaseReference.child("production_teams").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("date", ServerValue.TIMESTAMP);

                                        databaseReference.child("production_teams").child(pushId).child("members").child(firebaseAuth.getCurrentUser().getUid()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("production_name", createProductionName.getText().toString());
                                                data.put("production_id",pushId);
                                                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("production_team").setValue(data);
                                                createDialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


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


                builder.setView(myLayout);
                createDialog = builder.create();

                createDialog.setCancelable(true);
                createDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                createDialog.show();


            }
        });

        if (firebaseAuth.getCurrentUser() != null){
            loadProfile();


        }else{
            userLayout.setVisibility(GONE);
            postCard.setVisibility(GONE);
            postText.setVisibility(GONE);
            teamLayout.setVisibility(GONE);
            displayProduction();
        }
        displayPost();
        displaySelection();


}


    public void loadProfile(){
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                profileDisplayName = map.get("display_name").toString();
                TextView homeName = findViewById(R.id.homeName);
                homeName.setText(profileDisplayName);

                if (map.get("categories") == null){
                    displayCategories();
                }else{
                    userCategories = new ArrayList<>();
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("categories").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DataSnapshot dsp : task.getResult().getChildren()) {
                                    userCategories.add(dsp.getValue().toString().trim());
                                }

                            }

                        }
                    });

                }

                    for (DataSnapshot child : dataSnapshot.child("production_team").getChildren()) {
                        if (child.getKey().toString().matches("production_id")) {
                           profileTeamId = child.getValue().toString();
                        }

                    }





                displayProduction();
                userLayout.setVisibility(VISIBLE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(profileListener);

        ImageView homeProfile = findViewById(R.id.homeProfile);
        ImageView postProfile = findViewById(R.id.postProfile);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(MainActivity.this).load(uri).fitCenter().into(homeProfile);
                Glide.with(MainActivity.this).load(uri).fitCenter().into(postProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });



    }

    public void displayProduction(){
        productionList = new ArrayList<>();
        ConstraintLayout create_team_layout = (ConstraintLayout) findViewById(R.id.create_team_layout);
        ConstraintLayout my_team_layout = (ConstraintLayout) findViewById(R.id.my_team_layout);
        TextView my_team_name = (TextView)findViewById(R.id.my_team_name);
        TextView my_team_description = (TextView)findViewById(R.id.my_team_description);
        TextView my_team_designation = (TextView)findViewById(R.id.my_team_designation);
        ImageView my_team_logo = (ImageView) findViewById(R.id.my_team_logo);

        ValueEventListener productionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productionList.clear();
                production_myLists.clear();

                my_team_layout.setVisibility(GONE);
                create_team_layout.setVisibility(VISIBLE);

                Boolean userGroup = false;
                hasATeam = false;

           //     ArrayList<String> membersList = new ArrayList<>();




                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    int count = 0;
                    userGroup = false;
                   // membersList.clear();

                    Map<String, Object> production = (HashMap<String,Object>) dsp.getValue();

                    if (dsp.child("active").getValue() != null && Boolean.parseBoolean(dsp.child("active").getValue().toString()) == true){
                        productionList.add(dsp.getKey());
                    }


                    for (DataSnapshot members : dsp.child("members").getChildren()) {
                      //  membersList.add(members.getKey());


                        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getUid().matches(members.getKey())){
                            hasATeam = true;

                            profileTeamId = dsp.getKey();
                            profileTeamName = production.get("production_name").toString();

                            my_team_layout.setVisibility(VISIBLE);
                            MaterialCardView myTeam = (MaterialCardView) findViewById(R.id.myTeam);
                            my_team_description.setText(production.get("description").toString());
                            my_team_name.setText(production.get("production_name").toString());

                            create_team_layout.setVisibility(GONE);
                            userGroup = true;


                            myTeam.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, Production.class);
                                    intent.putExtra("production_id",dsp.getKey());
                                    MainActivity.this.startActivity(intent);
                                }
                            });



                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            storageReference.child("production_teams/"+dsp.getKey()+"/logo/team_logo.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Glide.with(MainActivity.this).load(uri).centerCrop().placeholder(R.drawable.loading).dontAnimate().into(my_team_logo);

                                    my_team_logo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MainActivity.this, ImageViewer.class);
                                            intent.putExtra("url", "production_teams/"+dsp.getKey()+"/logo/team_logo.jpg");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            MainActivity.this.startActivity(intent);
                                        }
                                    });



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });



                        }
                        count++;

                    }
                    if (firebaseAuth.getCurrentUser() != null){
                        if (userGroup == false){
                            production_myLists.add(new ProductionList(dsp.getKey(),production.get("production_name").toString(),String.valueOf(count),production.get("individual_skills").toString(),production.get("price_minimum").toString(),production.get("price_maximum").toString()));
                        }
                    }else{
                        production_myLists.add(new ProductionList(dsp.getKey(),production.get("production_name").toString(),String.valueOf(count),production.get("individual_skills").toString(),production.get("price_minimum").toString(),production.get("price_maximum").toString()));

                    }



                }
                if (hasATeam == true){
                    Map<String, Object> data = new HashMap<>();
                    data.put("production_name",profileTeamName);
                    data.put("production_id",profileTeamId);
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("production_team").setValue(data);
                }


                production_adapter = new ProductionAdapter(production_myLists, MainActivity.this);
                production_rv.setAdapter(production_adapter);
                production_adapter.notifyDataSetChanged();
                displayBookings(productionList,hasATeam.booleanValue());

                ConstraintLayout productions_empty_state = (ConstraintLayout) findViewById(R.id.productions_empty_state);

                if (production_myLists.size() == 0){
                    productions_empty_state.setVisibility(VISIBLE);
                }else{
                    productions_empty_state.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("production_teams").orderByChild("production_name").addValueEventListener(productionListener);



    }

    public void displayBookings(List<String> productionList,Boolean hasATeam){


        ValueEventListener bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                bookings_myLists.clear();




                for (DataSnapshot parent : dataSnapshot.getChildren()) {


                    Map<String, Object> data = (HashMap<String,Object>) parent.getValue();

                    if (hasATeam == true){


                        if (data.get("production_id").toString().matches(profileTeamId)){
                            if (productionList.contains(data.get("production_id").toString().trim())){
                                if (new Date(Long.parseLong(data.get("booking_date").toString())).after(new Date()) || new Date(Long.parseLong(data.get("booking_date").toString())).equals(new Date())){
                                    count++;

                                    bookings_myLists.add(new BookingsList(parent.getKey(),data.get("client_id").toString(),data.get("production_id").toString(),data.get("booking_date").toString(),data.get("email").toString(),data.get("message").toString(),data.get("status").toString(),data.get("type").toString()));
                                }

                            }

                        }


                    }else{

                        if (firebaseAuth.getCurrentUser() != null && data.get("client_id").toString().matches(firebaseAuth.getCurrentUser().getUid())){

                            if (productionList.contains(data.get("production_id").toString().trim())){
                                if (new Date(Long.parseLong(data.get("booking_date").toString())).after(new Date()) || new Date(Long.parseLong(data.get("booking_date").toString())).equals(new Date())){
                                    count++;

                                    bookings_myLists.add(new BookingsList(parent.getKey(),data.get("client_id").toString(),data.get("production_id").toString(),data.get("booking_date").toString(),data.get("email").toString(),data.get("message").toString(),data.get("status").toString(),data.get("type").toString()));
                                }

                            }

                        }

                    }

                }
                bookings_adapter = new BookingsAdapter(bookings_myLists, MainActivity.this);
                bookings_rv.setAdapter(bookings_adapter);
                bookings_adapter.notifyDataSetChanged();
                chipNavigationBar.showBadge(R.id.bookings,count);

                ConstraintLayout bookings_empty_state = (ConstraintLayout) findViewById(R.id.bookings_empty_state);
                if (bookings_myLists.size() == 0){
                    bookings_empty_state.setVisibility(VISIBLE);
                }else{
                    bookings_empty_state.setVisibility(GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("events").orderByChild("booking_date").addValueEventListener(bookingsListener);

    }



    public void displayPost(){
        if (postListener != null){
            databaseReference.removeEventListener(postListener);
        }

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post_myLists.clear();


                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    tags = new ArrayList<>();
                    tags.clear();
                    Map<String, Object> post = (HashMap<String,Object>) dsp.getValue();

                    if (postFilter.isChecked()){

                        if (post.get("tags") != null){
                            tags = Arrays.asList(post.get("tags").toString().replace("[","").replace("]","").split(","));
                            for (String tag : userCategories){
                                if (tags.toString().trim().contains(tag.trim())){
                                    post_myLists.add(new PostList(dsp.getKey(),post.get("caption").toString(),post.get("user_id").toString(),post.get("date").toString(),tags));
                                   // break;
                                }
                            }

                        }

                    }else{

                        if (post.get("tags") != null){
                            tags = Arrays.asList(post.get("tags").toString().replace("[","").replace("]","").split(","));
                            post_myLists.add(new PostList(dsp.getKey(),post.get("caption").toString(),post.get("user_id").toString(),post.get("date").toString(),tags));

                        }else{

                            if (post.get("caption") != null && post.get("user_id") !=null && post.get("date")!=null){
                                post_myLists.add(new PostList(dsp.getKey(),post.get("caption").toString(),post.get("user_id").toString(),post.get("date").toString(),null));

                            }
                        }

                    }





                }

                post_adapter = new PostAdapter(post_myLists, MainActivity.this);
                post_rv.setAdapter(post_adapter);
                post_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("posts").orderByChild("date").addValueEventListener(postListener);

    }

    public void displaySelection(){
        selection_rv = (RecyclerView) findViewById(R.id.selection_rec);

        FocusLayoutManager focusLayoutManager =
                new FocusLayoutManager.Builder()
                        .layerPadding(dp2px(this, 100))
                        .normalViewGap(dp2px(this, 5))
                        .focusOrientation(FocusLayoutManager.FOCUS_LEFT)
                        .isAutoSelect(true)
                        .maxLayerCount(1)
                        .setOnFocusChangeListener(new FocusLayoutManager.OnFocusChangeListener() {
                            @Override
                            public void onFocusChanged(int focusdPosition, int lastFocusdPosition) {

                            }
                        })
                        .build();
        selection_rv.setLayoutManager(focusLayoutManager);

        selection_myLists.add(new SelectionList("Book now","Wedding? Debut?","We've got you covered, Just relax and watch it happen",R.drawable.weddings));
        selection_myLists.add(new SelectionList("Create now","Solo?","Create a production and accept reservations",R.drawable.photographers));
        selection_myLists.add(new SelectionList("Invite others","Form a team","Build your team and recruit individual talents",R.drawable.writers));
        selection_myLists.add(new SelectionList("Join now","Join a Production team","Do you have the talent? Join and show us what you've got",R.drawable.highlighters));
        selection_adapter = new SelectionAdapter(selection_myLists, this);

        selection_rv.setAdapter(selection_adapter);




      //  selection_rv.setLayoutManager(layoutManager);
      //  selection_rv.setHasFixedSize(true);


        // selection_myLists.add(new SelectionList("Book now","Do you have an event? Book a production now","A team composed of Photographers, Videographers, Writers, SDE Editor and Highlighters",R.drawable.writers));


    }

    static void displayFind(Context context){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        myLayout = inflater.inflate(R.layout.advanced_book_popup, null);

        find_production_rv = (RecyclerView) myLayout.findViewById(R.id.find_production_rec);
        find_production_rv.setHasFixedSize(true);
        find_production_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        find_production_myLists = new ArrayList<>();

        ArrayList<String> find_prodList = new ArrayList<>();




        SingleDateAndTimePicker findPicker = myLayout.findViewById(R.id.findPicker);
        TextView findDate = myLayout.findViewById(R.id.findDate);
        TextView findTime = myLayout.findViewById(R.id.findTime);
        TextView findResult = myLayout.findViewById(R.id.findResult);
        Button findProduction = myLayout.findViewById(R.id.findProduction);
        TextInputEditText findPrice = myLayout.findViewById(R.id.findPrice);


        findDate.setText(DateFormat.format("MMM dd yyyy",new Date()));
        findTime.setText(DateFormat.format("hh:mm aa",new Date()));


        findPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {


                findSelectedDate = date.getTime();


                findDate.setText(DateFormat.format("MMM dd yyyy",date));
                findTime.setText(DateFormat.format("hh:mm aa",date));

            }
        });


        findProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  if (findPrice.getText().toString().isEmpty()){
              //      findPrice.setError("Minimum is required");
              //      findPrice.requestFocus();
              //      return;
              //  }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("production_teams").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            find_production_myLists.clear();
                            find_prodList.clear();
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                Map<String, Object> map = (HashMap<String,Object>) child.getValue();

                                String prodId = child.getKey();
                                find_production_myLists.add(new FindList(prodId));


                            }

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        if (task.getResult().exists()){

                                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {

                                                Map<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();

                                                    if (data.get("booking_date").toString().matches(String.valueOf(findSelectedDate))){
                                                        if (!find_prodList.contains(data.get("production_id").toString())){
                                                            find_prodList.add(data.get("production_id").toString());

                                                        }
                                                    }

                                            }

                                            for (int i = 0; i < find_production_myLists.size();i++){
                                                if (find_prodList.contains(find_production_myLists.get(i).getProduction_id())){
                                                    find_production_myLists.remove(i);
                                                }
                                            }
                                            findResult.setVisibility(VISIBLE);
                                            findResult.setText("Result: " + String.valueOf(find_production_myLists.size()) + " production(s) found");

                                            find_production_adapter = new FindAdapter(find_production_myLists, context);
                                            find_production_rv.setAdapter(find_production_adapter);
                                            find_production_adapter.notifyDataSetChanged();

                                        }

                                    }

                                }
                            });


                        }
                    }
                });






/*

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().exists()){
                                find_prodList.clear();

                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                    Map<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();


                                        if (data.get("booking_date").toString().matches(String.valueOf(findSelectedDate))){

                                        }else{
                                            if (!find_prodList.contains(data.get("production_id").toString())){

                                                find_prodList.add(data.get("production_id").toString());
                                                find_production_myLists.add(new FindList(data.get("production_id").toString()));

                                            }

                                        }
                                    }



                                find_production_adapter = new FindAdapter(find_production_myLists, context);
                                find_production_rv.setAdapter(find_production_adapter);
                                find_production_adapter.notifyDataSetChanged();

                            }

                        }

                    }
                });


// ==
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("production_teams").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            find_production_myLists.clear();
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                Map<String, Object> map = (HashMap<String,Object>) child.getValue();
                                if (Integer.parseInt(findPrice.getText().toString()) <= Integer.parseInt(map.get("price_maximum").toString())  && Integer.parseInt(findPrice.getText().toString()) >= Integer.parseInt(map.get("price_minimum").toString())){
                                    String prodId = child.getKey();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                if (task.getResult().exists()){

                                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                                        Map<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();
                                                        if(prodId.matches(data.get("production_id").toString())){


                                                            if (data.get("booking_date").toString().matches(String.valueOf(findSelectedDate))){
                                                            }else{
                                                                find_production_myLists.add(new FindList(data.get("production_id").toString()));

                                                            }
                                                        }



                                                    }
                                                    find_production_adapter = new FindAdapter(find_production_myLists, context);
                                                    find_production_rv.setAdapter(find_production_adapter);
                                                    find_production_adapter.notifyDataSetChanged();

                                                }

                                            }

                                        }
                                    });


                                }else{
                                    Toast.makeText(context, "No production team is in range with your current price offer", Toast.LENGTH_SHORT).show();
                                }


                            }

                            find_production_adapter = new FindAdapter(find_production_myLists, context);
                            find_production_rv.setAdapter(find_production_adapter);
                            find_production_adapter.notifyDataSetChanged();

                        }
                    }
                });


 */
            }
        });



        builder.setView(myLayout);
        findDialog = builder.create();

        findDialog.setCancelable(true);
        findDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        findDialog.show();

    }

    public void displayPopupPost(){


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        myLayout = inflater.inflate(R.layout.post_popup, null);

        popupImage = (ImageView)myLayout.findViewById(R.id.popupImage);

        Button popupPost = (Button)myLayout.findViewById(R.id.popupPost);
        Button popupUpload = (Button)myLayout.findViewById(R.id.popupUpload);
        EditText popupCaption = (EditText)myLayout.findViewById(R.id.popupCaption);
        TextView popupSelect = (TextView)myLayout.findViewById(R.id.popupSelect);




        tags_rv = (RecyclerView) myLayout.findViewById(R.id.tags_rec);
        tags_rv.setHasFixedSize(true);

        tags_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        tags_myLists = new ArrayList<>();
        tags_myLists.clear();


        ImageView popupRemove = (ImageView)myLayout.findViewById(R.id.popupRemove);
        popupImageContainer = (ConstraintLayout) myLayout.findViewById(R.id.popupImageContainer);

        popupSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<CategoryList> category_myLists;
                CategoryAdapter category_adapter;
                tags_myLists.clear();

                if (categoryDialog != null){
                    categoryDialog.dismiss();
                }

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                myLayout = inflater.inflate(R.layout.category_popup, null);

                RecyclerView category_rv = (RecyclerView)myLayout.findViewById(R.id.category_rec);
                Button categoryDone = (Button)myLayout.findViewById(R.id.categoryDone);
                TextView categoryText = (TextView)myLayout.findViewById(R.id.categoryText);
                categoryText.setVisibility(GONE);

                category_rv.setHasFixedSize(true);
                category_rv.setLayoutManager(new GridLayoutManager(context,2, LinearLayoutManager.VERTICAL,false));
                category_myLists=new ArrayList<>();

                category_myLists.add(new CategoryList("Landscape",R.drawable.landscape,false));
                category_myLists.add(new CategoryList("Wildlife",R.drawable.wildlife,false));
                category_myLists.add(new CategoryList("Macro",R.drawable.macro,false));
                category_myLists.add(new CategoryList("Underwater",R.drawable.underwater,false));
                category_myLists.add(new CategoryList("Astrophotography",R.drawable.astrophotography,false));
                category_myLists.add(new CategoryList("Aerial Photography",R.drawable.aerial,false));
                category_myLists.add(new CategoryList("Scientific",R.drawable.scientific,false));

                category_myLists.add(new CategoryList("Portraits",R.drawable.portraits,false));
                category_myLists.add(new CategoryList("Weddings",R.drawable.weddings,false));
                category_myLists.add(new CategoryList("Documentary",R.drawable.documentary,false));
                category_myLists.add(new CategoryList("Sports",R.drawable.sports,false));
                category_myLists.add(new CategoryList("Fashion",R.drawable.fashion,false));
                category_myLists.add(new CategoryList("Commercial",R.drawable.commercial,false));
                category_myLists.add(new CategoryList("Street Photography",R.drawable.street_photography,false));
                category_myLists.add(new CategoryList("Event Photography",R.drawable.event_photography,false));
                category_myLists.add(new CategoryList("Travel",R.drawable.travel,false));
                category_myLists.add(new CategoryList("Pet Photography",R.drawable.pet_photography,false));

                category_myLists.add(new CategoryList("Product Photography",R.drawable.street_photography,false));
                category_myLists.add(new CategoryList("Food",R.drawable.food,false));
                category_myLists.add(new CategoryList("Still Life Photography",R.drawable.still_life_photography,false));
                category_myLists.add(new CategoryList("Architecture",R.drawable.architecture,false));

                category_adapter = new CategoryAdapter(category_myLists, MainActivity.this);
                category_rv.setAdapter(category_adapter);
                category_adapter.notifyDataSetChanged();

                categoryDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        for (int x = 0; x < category_myLists.size(); x++){
                            if (category_myLists.get(x).getChecked() == true){

                                tags_myLists.add(new TagsList(category_myLists.get(x).getCategory().toString()));
                            }
                        }
                        categoryDialog.dismiss();
                        tags_adapter = new TagsAdapter(tags_myLists, context,"create");
                        tags_rv.setAdapter(tags_adapter);
                        tags_adapter.notifyDataSetChanged();
                    }
                });




                builder.setView(myLayout);
                categoryDialog = builder.create();

                categoryDialog.setCancelable(true);
                categoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                categoryDialog.show();


            }
        });

        popupUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 200);
            }
        });

        popupRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupImage.setImageBitmap(null);
                popupImageContainer.setVisibility(GONE);

            }
        });

        popupPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupCaption.getText().toString().isEmpty()){
                    popupCaption.setError("Field cannot be empty");
                    popupCaption.requestFocus();
                    return;
                }else if (tags_myLists.isEmpty()){
                    Toast.makeText(context, "Must include tags", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (popupImageContainer.getVisibility() == GONE){
                    Toast.makeText(MainActivity.this, "Upload an image first", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (hud !=null){
                    hud.dismiss();
                }
                hud = KProgressHUD.create(MainActivity.this)
                        .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                        .setMaxProgress(100)
                        .show();


                String pushId = databaseReference.child("posts").push().getKey();

                StorageReference storageRef = firebaseStorage.getReference();
                StorageReference parentRef = storageRef.child("posts/"+pushId+"/post_image.jpg");

                popupImage.setDrawingCacheEnabled(true);
                popupImage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) popupImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                UploadTask uploadTask = parentRef.putBytes(bytes);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        hud.setLabel("Uploading file: " + String.format("%.2f", progress) + "%");
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
                        ArrayList<String> tags = new ArrayList<>();


                        for (int x = 0; x < tags_myLists.size(); x++){
                            tags.add(tags_myLists.get(x).getTag().toString());
                        }

                        Map<String, Object> data = new HashMap<>();
                        data.put("user_id",firebaseAuth.getCurrentUser().getUid());
                        data.put("date", ServerValue.TIMESTAMP);
                        data.put("caption", popupCaption.getText().toString());
                        data.put("tags", tags);

                        databaseReference.child("posts").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                postDialog.dismiss();

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

        builder.setView(myLayout);
        postDialog = builder.create();

        postDialog.setCancelable(true);
        postDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        postDialog.show();


    }


    public void displayCategories(){


        List<CategoryList> category_myLists;
        CategoryAdapter category_adapter;

        if (categoryDialog == null){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            myLayout = inflater.inflate(R.layout.category_popup, null);

            RecyclerView category_rv = (RecyclerView)myLayout.findViewById(R.id.category_rec);
            Button categoryDone = (Button)myLayout.findViewById(R.id.categoryDone);

            category_rv.setHasFixedSize(true);
            category_rv.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false));
            category_myLists=new ArrayList<>();

            category_myLists.add(new CategoryList("Landscape",R.drawable.landscape,false));
            category_myLists.add(new CategoryList("Wildlife",R.drawable.wildlife,false));
            category_myLists.add(new CategoryList("Macro",R.drawable.macro,false));
            category_myLists.add(new CategoryList("Underwater",R.drawable.underwater,false));
            category_myLists.add(new CategoryList("Astrophotography",R.drawable.astrophotography,false));
            category_myLists.add(new CategoryList("Aerial Photography",R.drawable.aerial,false));
            category_myLists.add(new CategoryList("Scientific",R.drawable.scientific,false));

            category_myLists.add(new CategoryList("Portraits",R.drawable.portraits,false));
            category_myLists.add(new CategoryList("Weddings",R.drawable.weddings,false));
            category_myLists.add(new CategoryList("Documentary",R.drawable.documentary,false));
            category_myLists.add(new CategoryList("Sports",R.drawable.sports,false));
            category_myLists.add(new CategoryList("Fashion",R.drawable.fashion,false));
            category_myLists.add(new CategoryList("Commercial",R.drawable.commercial,false));
            category_myLists.add(new CategoryList("Street Photography",R.drawable.street_photography,false));
            category_myLists.add(new CategoryList("Event Photography",R.drawable.event_photography,false));
            category_myLists.add(new CategoryList("Travel",R.drawable.travel,false));
            category_myLists.add(new CategoryList("Pet Photography",R.drawable.pet_photography,false));

            category_myLists.add(new CategoryList("Product Photography",R.drawable.street_photography,false));
            category_myLists.add(new CategoryList("Food",R.drawable.food,false));
            category_myLists.add(new CategoryList("Still Life Photography",R.drawable.still_life_photography,false));
            category_myLists.add(new CategoryList("Architecture",R.drawable.architecture,false));

            category_adapter = new CategoryAdapter(category_myLists, MainActivity.this);
            category_rv.setAdapter(category_adapter);
            category_adapter.notifyDataSetChanged();

            categoryDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hud = KProgressHUD.create(MainActivity.this)
                            .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                            .setLabel("Updating your info")
                            .setCancellable(false)
                            .setMaxProgress(100)
                            .show();

                    ArrayList<String> categories = new ArrayList<>();
                    for (int x = 0; x < category_myLists.size(); x++){
                        if (category_myLists.get(x).getChecked() == true){
                            categories.add(category_myLists.get(x).getCategory().toString());
                        }
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("categories", categories);

                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hud.dismiss();
                            categoryDialog.dismiss();
                            hud.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hud.dismiss();
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            builder.setView(myLayout);
            categoryDialog = builder.create();

            categoryDialog.setCancelable(false);
            categoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            categoryDialog.show();


        }



    }

    public void filterByName(String text){
        List<ProductionList> temp = new ArrayList();
        for(ProductionList d: production_myLists){



            if(d.getProduction_name().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }

        }

        production_adapter.updateList(temp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                popupImageContainer.setVisibility(VISIBLE);
              //  Glide.with(MainActivity.this).load(selectedImage).centerInside().into(popupImage);
                popupImage.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 300 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                createLogo.setImageBitmap(selectedImage);
             //   Glide.with(MainActivity.this).load(selectedImage).into(createLogo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }





    public static void setWindowFlag(MainActivity activity, final int bits, boolean on) {
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