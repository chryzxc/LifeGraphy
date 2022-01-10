package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signin extends AppCompatActivity {

    FirebaseDatabase  firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    ImageView uploadImage;
    Activity activity;
    KProgressHUD hud;

    List<CategoryList> category_myLists;
    RecyclerView category_rv;
    CategoryAdapter category_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        activity = Signin.this;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        ConstraintLayout loginForm = (ConstraintLayout) findViewById(R.id.loginForm);
        ConstraintLayout registerForm = (ConstraintLayout) findViewById(R.id.registerForm);
        ConstraintLayout setupForm = (ConstraintLayout) findViewById(R.id.setupForm);



        if (firebaseAuth.getCurrentUser() != null){
            hud = KProgressHUD.create(Signin.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Checking last login details")
                    .setCancellable(false)
                    .show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                        hud.dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        hud.dismiss();
                        setupForm.setVisibility(View.VISIBLE);
                        loginForm.setVisibility(View.GONE);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hud.dismiss();
                }
            });


        }


        category_rv=(RecyclerView)findViewById(R.id.category_rec);
        category_rv.setHasFixedSize(true);
        category_rv.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false));
        category_myLists=new ArrayList<>();

        displayCategory();





        ImageView loginCover = (ImageView) findViewById(R.id.loginCover);
        ImageView registerCover = (ImageView) findViewById(R.id.registerCover);
        ImageView nameCover = (ImageView) findViewById(R.id.nameCover);
        ImageView profileCover = (ImageView) findViewById(R.id.profileCover);
        ImageView confirmCover = (ImageView) findViewById(R.id.confirmCover);
        uploadImage = (ImageView) findViewById(R.id.uploadImage);


        TextView registerBack = (TextView) findViewById(R.id.registerBack);
        TextView registerUser = (TextView) findViewById(R.id.registerUser);
        TextView setupUpload = (TextView) findViewById(R.id.setupUpload);


        EditText loginEmail = (EditText) findViewById(R.id.loginEmail);
        EditText loginPassword = (EditText) findViewById(R.id.loginPassword);
        EditText registerEmail = (EditText) findViewById(R.id.registerEmail);
        EditText registerPassword = (EditText) findViewById(R.id.registerPassword);
        EditText setupDisplayName = (EditText) findViewById(R.id.setupDisplayName);




        Button signinButton = (Button) findViewById(R.id.signinButton);
        Button guestButton = (Button) findViewById(R.id.guestButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        Button nameNext = (Button) findViewById(R.id.nameNext);
        Button profileBack = (Button) findViewById(R.id.profileBack);
        Button profileNext = (Button) findViewById(R.id.profileNext);
        Button confirmBack = (Button) findViewById(R.id.confirmBack);
        Button confirmDone = (Button) findViewById(R.id.confirmDone);

        CardView nameSetup = (CardView) findViewById(R.id.nameSetup);
        CardView profileSetup = (CardView) findViewById(R.id.profileSetup);
        CardView confirmationSetup = (CardView) findViewById(R.id.confirmationSetup);

        Glide.with(this).load(R.drawable.login_cover).centerCrop().into(loginCover);
        Glide.with(this).load(R.drawable.login_cover).centerCrop().into(registerCover);
        Glide.with(this).load(R.drawable.name_setup).centerCrop().into(nameCover);
        Glide.with(this).load(R.drawable.profile_setup).centerCrop().into(profileCover);
        Glide.with(this).load(R.drawable.confirm_setup).centerCrop().into(confirmCover);

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
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(loginEmail.getText().toString().isEmpty()){
                    loginEmail.setError("Email is required");
                    loginEmail.requestFocus();
                    return;
                }

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\..+[a-z]+";

                if(!loginEmail.getText().toString().matches(emailPattern)){
                    loginEmail.setError("Invalid email");
                    loginEmail.requestFocus();
                    return;
                }

                if(loginPassword.getText().toString().isEmpty()){
                    loginPassword.setError("Password is required");
                    loginPassword.requestFocus();
                    return;
                }

                if (hud !=null){
                    hud.dismiss();
                }
                hud = KProgressHUD.create(Signin.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Signing in")
                        .setCancellable(false)
                        .show();

                hideKeyboard();


                firebaseAuth.signInWithEmailAndPassword(loginEmail.getText().toString().trim(),loginPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            if (firebaseAuth.getCurrentUser().isEmailVerified()){

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                            hud.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }else{
                                            hud.dismiss();
                                            setupForm.setVisibility(View.VISIBLE);
                                            loginForm.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        hud.dismiss();
                                    }
                                });


                            }else{
                                hud.dismiss();
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                Toast.makeText(Signin.this, "Check your email for verification", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            hud.dismiss();
                            Toast.makeText(Signin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(registerEmail.getText().toString().isEmpty()){
                    registerEmail.setError("Email is required");
                    registerEmail.requestFocus();
                    return;
                }

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\..+[a-z]+";

                if(!registerEmail.getText().toString().matches(emailPattern)){
                    registerEmail.setError("Invalid email");
                    registerEmail.requestFocus();
                    return;
                }

                if(registerPassword.getText().toString().isEmpty()){
                    registerPassword.setError("Password is required");
                    registerPassword.requestFocus();
                    return;
                }

                hud = KProgressHUD.create(Signin.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setCancellable(false)
                        .show();

                hideKeyboard();

                firebaseAuth.createUserWithEmailAndPassword(registerEmail.getText().toString().trim(),registerPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){


                            firebaseAuth.signInWithEmailAndPassword(registerEmail.getText().toString().trim(),registerPassword.getText().toString());

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Signin.this, "An email verification link has been sent to " +registerEmail.getText().toString().trim(), Toast.LENGTH_LONG).show();
                                    loginForm.setVisibility(View.VISIBLE);
                                    registerForm.setVisibility(View.GONE);
                                    FirebaseAuth.getInstance().signOut();
                                    hud.dismiss();
                                }


                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hud.dismiss();
                                    Toast.makeText(Signin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            });



                        }else{
                            Toast.makeText(Signin.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            hud.dismiss();
                        }

                    }
                });



            }

        });



        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForm.setVisibility(View.VISIBLE);
                loginForm.setVisibility(View.GONE);
            }
        });

        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginForm.setVisibility(View.VISIBLE);
                registerForm.setVisibility(View.GONE);
            }
        });

        setupUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);

            }
        });


        nameNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileSetup.setVisibility(View.VISIBLE);
                nameSetup.setVisibility(View.INVISIBLE);
                hideKeyboard();

            }
        });


        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameSetup.setVisibility(View.VISIBLE);
                profileSetup.setVisibility(View.INVISIBLE);
            }
        });

        profileNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmationSetup.setVisibility(View.VISIBLE);
                profileSetup.setVisibility(View.INVISIBLE);

            }
        });

        confirmBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileSetup.setVisibility(View.VISIBLE);
                confirmationSetup.setVisibility(View.INVISIBLE);

            }
        });

        confirmDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hud = KProgressHUD.create(Signin.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Finishing setup")
                        .setCancellable(false)
                        .show();

                ArrayList<String> categories = new ArrayList<>();


                for (int x = 0; x < category_myLists.size(); x++){
                    if (category_myLists.get(x).getChecked() == true){
                        categories.add(category_myLists.get(x).getCategory().toString());
                    }
                }

                Map<String, Object> production = new HashMap<>();
                production.put("production_id","None");
                production.put("production_name","None");

                Map<String, Object> data = new HashMap<>();
                data.put("display_name", setupDisplayName.getText().toString().trim());
                data.put("email", firebaseAuth.getCurrentUser().getEmail());
                data.put("date_registered", ServerValue.TIMESTAMP);
                data.put("info","Info has not been setup yet");
                data.put("production_team", production);
                data.put("posts", "None");
                data.put("categories", categories);

                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("users/"+firebaseAuth.getCurrentUser().getUid()+ "/profile/profile_picture.jpg");

                        uploadImage.setDrawingCacheEnabled(true);
                        uploadImage.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) uploadImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = parentRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                hud.dismiss();
                                Toast.makeText(Signin.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                hud.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hud.dismiss();
                        Toast.makeText(Signin.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

              //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // startActivity(intent);

            }
        });
    }

    public void hideKeyboard() {
        activity.getCurrentFocus();
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    public static void setWindowFlag(Signin activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void displayCategory(){
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

        category_adapter = new CategoryAdapter(category_myLists, Signin.this);
        category_rv.setAdapter(category_adapter);
        category_adapter.notifyDataSetChanged();




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                uploadImage.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Signin.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }


}