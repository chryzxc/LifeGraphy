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

import static android.view.View.VISIBLE;

public class ProductionMemberManageAdapter extends RecyclerView.Adapter<ProductionMemberManageAdapter.ViewHolder> {
    private List<ProductionMemberList> myListList;
    private Context ct;

    public ProductionMemberManageAdapter(List<ProductionMemberList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_manage_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionMemberList myList=myListList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (Production.creatorID.matches(myList.getId())){
            holder.teamMemberManageRemove.setVisibility(View.GONE);
        }


        ValueEventListener member = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                holder.teamMemberManageDisplayName.setText(map.get("display_name").toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ct, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // Getting Post failed, log a message

            }
        };
        databaseReference.child("users").child(myList.getId()).addValueEventListener(member);


        storageReference.child("users/"+myList.getId()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Glide.with(ct).load(uri).centerCrop().placeholder(R.drawable.loading).into(holder.teamMemberManageProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ct, exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.teamMemberManageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseReference.child("production_teams").child(Production.productionID).child("members").child(myList.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Production.member_manage_rv.setVisibility(View.GONE);
                        Production.member_manage_rv.setVisibility(VISIBLE);

                        Production.member_manage_adapter = new ProductionMemberManageAdapter(Production.member_myLists, ct);
                        Production.member_manage_rv.setAdapter(Production.member_manage_adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ct, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView teamMemberManageProfile;
        private TextView teamMemberManageDisplayName;
        private Button teamMemberManageRemove;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamMemberManageDisplayName = (TextView)itemView.findViewById(R.id.teamMemberManageDisplayName);
            teamMemberManageProfile = (ImageView)itemView.findViewById(R.id.teamMemberManageProfile);
            teamMemberManageRemove = (Button)itemView.findViewById(R.id.teamMemberManageRemove);


        }
    }
}