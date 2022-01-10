package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
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


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Profile extends AppCompatActivity {

    List<MyPostList> mypost_myLists;
    RecyclerView mypost_rv;
    MyPostAdapter mypost_adapter;
    LinearLayout edit_profile;

    View editLayout;
    static LayoutInflater inflater;

    static androidx.appcompat.app.AlertDialog editDialog,editCaption,categoryDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String userId;
    String productionId;

    TextView profileDisplayName;
    TextView profileInfo;
    TextView profileTeam;
    TextView profilePostCount;
    ImageView editPhoto;
    KProgressHUD hud;

    static TagsAdapter tags_adapter;
    static List<TagsList> tags_myLists;
    static RecyclerView tags_rv;
    List<String> tags;
    static View myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_profile);
        inflater = getLayoutInflater();

        databaseReference = FirebaseDatabase.getInstance().getReference();



        profileDisplayName = (TextView) findViewById(R.id.profileDisplayName);
        profileInfo = (TextView) findViewById(R.id.profileInfo);
        profileTeam = (TextView) findViewById(R.id.profileTeam);
        profileTeam.setPaintFlags(profileTeam.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        profilePostCount = (TextView) findViewById(R.id.profilePostCount);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        mypost_rv = (RecyclerView) findViewById(R.id.mypost_rec);
        mypost_rv.setHasFixedSize(true);
        mypost_rv.setLayoutManager(linearLayoutManager);
        mypost_myLists = new ArrayList<>();

        edit_profile = (LinearLayout) findViewById(R.id.edit_profile);

        MaterialCardView profileBack = (MaterialCardView) findViewById(R.id.profileBack);
        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.super.onBackPressed();
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

        final CollapsingToolbarLayout toolbar_layout_profile = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_profile);
        AppBarLayout app_bar_profile = (AppBarLayout) findViewById(R.id.app_bar_profile);
        app_bar_profile.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar_layout_profile.setTitle("Profile");
                    isShow = true;
                } else if(isShow) {
                    toolbar_layout_profile.setTitle("Profile");
                    toolbar_layout_profile.setExpandedTitleColor(ContextCompat.getColor(Profile.this,R.color.transparent));


                    isShow = false;
                }
            }
        });

        profileTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!productionId.toLowerCase().matches("none")){
                    Intent intent = new Intent(Profile.this, Production.class);
                    intent.putExtra("production_id",productionId);
                    startActivity(intent);
                }


            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
                editLayout = inflater.inflate(R.layout.profile_edit_popup, null);

                TextInputEditText editName = (TextInputEditText) editLayout.findViewById(R.id.editName);
                TextInputEditText editInfo = (TextInputEditText) editLayout.findViewById(R.id.editInfo);
                Button editSave = (Button) editLayout.findViewById(R.id.editSave);
                editPhoto =  (ImageView) editLayout.findViewById(R.id.editPhoto);
                TextView editInterest = (TextView)editLayout.findViewById(R.id.editInterest);

                editName.setText(profileDisplayName.getText().toString());
                editInfo.setText(profileInfo.getText().toString());


                tags_rv = (RecyclerView) editLayout.findViewById(R.id.interest_rec);
                tags_rv.setHasFixedSize(true);

                tags_rv.setLayoutManager(new LinearLayoutManager(Profile.this, LinearLayoutManager.VERTICAL, false));
                tags_myLists = new ArrayList<>();
                tags_myLists.clear();

                for (String tag : MainActivity.userCategories){
                    tags_myLists.add(new TagsList(tag));
                }
                tags_adapter = new TagsAdapter(tags_myLists, Profile.this,"profile");
                tags_rv.setAdapter(tags_adapter);
                tags_adapter.notifyDataSetChanged();

                editPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 100);
                    }
                });

                editInterest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        List<CategoryList> category_myLists;
                        CategoryAdapter category_adapter;
                        tags_myLists.clear();

                        if (categoryDialog != null){
                            categoryDialog.dismiss();
                        }

                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
                        myLayout = inflater.inflate(R.layout.category_popup, null);

                        RecyclerView category_rv = (RecyclerView)myLayout.findViewById(R.id.category_rec);
                        Button categoryDone = (Button)myLayout.findViewById(R.id.categoryDone);
                        TextView categoryText = (TextView)myLayout.findViewById(R.id.categoryText);
                        categoryText.setVisibility(GONE);

                        category_rv.setHasFixedSize(true);
                        category_rv.setLayoutManager(new GridLayoutManager(Profile.this,2, LinearLayoutManager.VERTICAL,false));
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

                        category_adapter = new CategoryAdapter(category_myLists, Profile.this);
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
                                tags_adapter = new TagsAdapter(tags_myLists, Profile.this,"profile");
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


                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(Profile.this).load(uri).fitCenter().into(editPhoto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



                editSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hud != null){
                            hud.dismiss();
                        }

                        hud = KProgressHUD.create(Profile.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Updating profile")
                                .setCancellable(false)
                                .show();



                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference parentRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+ "/profile/profile_picture.jpg");

                        editPhoto.setDrawingCacheEnabled(true);
                        editPhoto.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) editPhoto.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        UploadTask uploadTask = parentRef.putBytes(bytes);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                hud.setLabel("Updating photo: " + String.format("%.2f", progress) + "%");
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

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/users/" + userId + "/display_name", editName.getText().toString());
                                childUpdates.put("/users/" + userId + "/info", editInfo.getText().toString());

                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hud.dismiss();
                                        editDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hud.dismiss();
                                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ArrayList<String> categories = new ArrayList<>();
                                for (int x = 0; x < tags_myLists.size(); x++){
                                        categories.add(tags_myLists.get(x).getTag().toString());
                                }

                                Map<String, Object> data = new HashMap<>();
                                data.put("categories", categories);

                                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hud.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hud.dismiss();
                                        Toast.makeText(Profile.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                    }
                });



                builder.setView(editLayout);
                editDialog = builder.create();

                editDialog.setCancelable(true);
                editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                editDialog.show();

            }
        });









      //  Glide.with(this).load(R.drawable.download)
     //           .into(profilePhoto);

        loadProfile();
        displayMyPost();



    }

    public void loadProfile(){
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                profileDisplayName.setText(map.get("display_name").toString());
                profileInfo.setText(map.get("info").toString());
                userId = dataSnapshot.getKey();



                for (DataSnapshot prod : dataSnapshot.child("production_team").getChildren()) {
                    if (prod.getKey().matches("production_name")){
                        profileTeam.setText(prod.getValue().toString());
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
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(profileListener);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                ImageView profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
                ImageView coverPhoto = (ImageView) findViewById(R.id.coverPhoto);

                Glide.with(Profile.this).load(uri)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                        .into(coverPhoto);

                Glide.with(Profile.this).load(uri).fitCenter().into(profilePhoto);

                profilePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Profile.this, ImageViewer.class);
                        intent.putExtra("url", "users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Profile.this.startActivity(intent);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }

    public void displayMyPost(){


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mypost_myLists.clear();
                int count= 0;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    Map<String, Object> mypost = (HashMap<String,Object>) dsp.getValue();
                    if (mypost.get("user_id").toString().matches(firebaseAuth.getCurrentUser().getUid())){
                        count++;
                        mypost_myLists.add(new MyPostList(dsp.getKey(),mypost.get("caption").toString(),mypost.get("date").toString()));

                    }

                }

                if (count == 0){

                    mypost_rv.setVisibility(View.GONE);
                }else{
                    mypost_rv.setVisibility(View.VISIBLE);
                }

                profilePostCount.setText(String.valueOf(count));

                mypost_adapter = new MyPostAdapter(mypost_myLists, Profile.this);
                mypost_rv.setAdapter(mypost_adapter);
                mypost_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("posts").orderByChild("date").addValueEventListener(postListener);


    }


    public static void setWindowFlag(Profile activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                editPhoto.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }



}