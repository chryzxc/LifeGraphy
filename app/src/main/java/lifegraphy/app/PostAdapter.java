package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostList> myListList;
    private Context ct;

    public PostAdapter(List<PostList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostList myList=myListList.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        holder.postCaption.setText(myList.getPost_caption());
        holder.postDate.setText("● "+DateFormat.format("MMM dd yyyy hh:mm aa",new Date(Long.parseLong(myList.getDate())))+ " ●");


        if (myList.getTags() != null){

            for (String tag : myList.getTags()){
                holder.post_tags_myLists.add(new TagsList(tag));

            }
            holder.post_tags_adapter = new TagsAdapter(holder.post_tags_myLists, ct,"photo");
            holder.post_tags_rv.setAdapter(holder.post_tags_adapter);

        }

        storageReference.child("posts/"+myList.getPost_id()+"/post_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).fitCenter().placeholder(R.drawable.loading).dontAnimate().into(holder.postImage);

                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ct, ImageViewer.class);
                        intent.putExtra("url", "posts/"+myList.getPost_id()+"/post_image.jpg");
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

        storageReference.child("users/"+myList.getUser_id()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ct).load(uri).fitCenter().placeholder(R.drawable.loading).into(holder.postProfile);

                holder.postProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ct, ImageViewer.class);
                        intent.putExtra("url", "users/"+myList.getUser_id()+"/profile/profile_picture.jpg");
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

        holder.postUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null && myList.getUser_id().matches(firebaseAuth.getCurrentUser().getUid())){
                    Intent intent = new Intent(ct, Profile.class);
                    ct.startActivity(intent);
                }else{
                    Intent intent = new Intent(ct, VisitProfile.class);
                    intent.putExtra("id", myList.getUser_id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ct.startActivity(intent);
                }

            }
        });





        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();

                holder.postUser.setText(map.get("display_name").toString());
                holder.postUser.setPaintFlags(holder.postUser.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                holder.postUserDetails.setText(map.get("info").toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        };
        databaseReference.child("users").child(myList.getUser_id()).addValueEventListener(profileListener);


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView postProfile,postImage;
        private TextView postCaption,postUser,postUserDetails,postDate;

        private List<TagsList> post_tags_myLists;
        private RecyclerView post_tags_rv;
        private TagsAdapter post_tags_adapter;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postProfile=(ImageView)itemView.findViewById(R.id.postProfile);
            postImage=(ImageView)itemView.findViewById(R.id.postImage);
            postCaption=(TextView) itemView.findViewById(R.id.postCaption);
            postUser=(TextView) itemView.findViewById(R.id.postUser);
            postUserDetails=(TextView) itemView.findViewById(R.id.postUserDetails);
            postDate=(TextView) itemView.findViewById(R.id.postDate);

            post_tags_rv = (RecyclerView) itemView.findViewById(R.id.post_tags_rec);
            post_tags_rv.setHasFixedSize(true);
            post_tags_rv.setLayoutManager(new LinearLayoutManager(ct, LinearLayoutManager.HORIZONTAL, false));
            post_tags_myLists = new ArrayList<>();


           // receiptName=(TextView)itemView.findViewById(R.id.receiptName);
           // receiptDate=(TextView)itemView.findViewById(R.id.receiptDate);




        }
    }
}