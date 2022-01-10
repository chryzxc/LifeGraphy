package lifegraphy.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
    private List<MyPostList> myListList;
    private Context ct;

    public MyPostAdapter(List<MyPostList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mypost_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyPostList myList=myListList.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        holder.mypostCaption.setText(myList.getCaption());
        holder.mypostDate.setText(DateFormat.format("MMM dd yyyy hh:mm aa",new Date(Long.parseLong(myList.getDate()))));


        holder.mypostEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ct);
                final int[] checkedItem = {-1};

                alertDialog.setTitle("Choose an action");

                final String[] listItems = new String[]{"Edit", "Delete"};


                alertDialog.setSingleChoiceItems(listItems, checkedItem[0], new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        checkedItem[0] = which;

                        switch (listItems[which]){
                            case "Edit":

                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                                holder.myLayout = Profile.inflater.inflate(R.layout.edit_caption_popup, null);


                                TextInputEditText editCaption = (TextInputEditText)holder.myLayout.findViewById(R.id.editCaption);
                                Button captionSave = (Button)holder.myLayout.findViewById(R.id.captionSave);

                                editCaption.setText(myList.getCaption());

                                captionSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (editCaption.getText().toString().isEmpty()){
                                            editCaption.setError("Field cannot be empty");
                                            editCaption.requestFocus();
                                            return;
                                        }

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("caption", editCaption.getText().toString());

                                        databaseReference.child("posts").child(myList.getId()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ct, "Saved", Toast.LENGTH_SHORT).show();
                                                Profile.editCaption.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                Profile.editCaption.dismiss();

                                            }
                                        });

                                    }
                                });

                                builder.setView(holder.myLayout);
                                Profile.editCaption = builder.create();

                                Profile.editCaption.setCancelable(true);
                                Profile.editCaption.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                Profile.editCaption.show();

                                dialog.dismiss();
                                break;
                            case "Delete":
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                databaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("posts").child(myList.getId());
                                databaseReference.removeValue();
                                Toast.makeText(ct, "Successfully deleted", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();


                                break;
                        }



                        dialog.dismiss();
                    }
                });



                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                holder.customAlertDialog = alertDialog.create();


                holder.customAlertDialog.show();
            }
        });

        storageReference.child("posts/"+myList.getId()+"/post_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).centerCrop().placeholder(R.drawable.loading).dontAnimate().into(holder.mypostImage);

                holder.mypostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ct, ImageViewer.class);
                        intent.putExtra("url", "posts/"+myList.getId()+"/post_image.jpg");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ct.startActivity(intent);
                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ct).load(uri).fitCenter().into(holder.mypostProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> user = (HashMap<String,Object>) dataSnapshot.getValue();
                holder.mypostName.setText(user.get("display_name").toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        };
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(profileListener);


      //  Glide.with(ct).load(myList.getImage()).fitCenter().centerCrop().into(holder.mypostImage);






    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mypostProfile,mypostImage,mypostEdit;
        private TextView mypostCaption,mypostName,mypostDate;
        AlertDialog customAlertDialog;
        View myLayout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mypostProfile=(ImageView)itemView.findViewById(R.id.mypostProfile);
            mypostImage=(ImageView)itemView.findViewById(R.id.mypostImage);
            mypostName=(TextView)itemView.findViewById(R.id.mypostName);
            mypostCaption=(TextView)itemView.findViewById(R.id.mypostCaption);
            mypostDate=(TextView)itemView.findViewById(R.id.mypostDate);
            mypostEdit = (ImageView)itemView.findViewById(R.id.mypostEdit);





        }
    }
}