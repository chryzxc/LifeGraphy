package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProductionMemberAdapter extends RecyclerView.Adapter<ProductionMemberAdapter.ViewHolder> {
    private List<ProductionMemberList> myListList;
    private Context ct;

    public ProductionMemberAdapter(List<ProductionMemberList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionMemberList myList=myListList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        ValueEventListener member = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                holder.teamMemberDisplayName.setText(map.get("display_name").toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ct, databaseError.getMessage(), Toast.LENGTH_SHORT).show();


            }
        };
        databaseReference.child("users").child(myList.getId()).addValueEventListener(member);


        storageReference.child("users/"+myList.getId()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Glide.with(ct).load(uri).centerCrop().placeholder(R.drawable.loading).into(holder.teamMemberProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ct, exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.teamMemberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null && myList.getId().matches(firebaseAuth.getCurrentUser().getUid())){
                    Intent intent = new Intent(ct, Profile.class);
                    ct.startActivity(intent);
                }else{
                    Intent intent = new Intent(ct, VisitProfile.class);
                    intent.putExtra("id", myList.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ct.startActivity(intent);
                }
            }
        });



    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView teamMemberProfile;
        private TextView teamMemberDisplayName;
        private Button teamMemberView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamMemberDisplayName = (TextView)itemView.findViewById(R.id.teamMemberDisplayName);
            teamMemberProfile = (ImageView)itemView.findViewById(R.id.teamMemberProfile);
            teamMemberView = (Button)itemView.findViewById(R.id.teamMemberView);


        }
    }
}