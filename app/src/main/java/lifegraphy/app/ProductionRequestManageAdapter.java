package lifegraphy.app;

import android.content.Context;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionRequestManageAdapter extends RecyclerView.Adapter<ProductionRequestManageAdapter.ViewHolder> {
    private List<ProductionRequestManageList> myListList;
    private Context ct;

    public ProductionRequestManageAdapter(List<ProductionRequestManageList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.request_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionRequestManageList myList=myListList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        ValueEventListener member = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                holder.teamRequestDisplayName.setText(map.get("display_name").toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ct, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // Getting Post failed, log a message

            }
        };
        databaseReference.child("users").child(myList.getUser_id()).addValueEventListener(member);


        storageReference.child("users/"+myList.getUser_id()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Glide.with(ct).load(uri).centerCrop().placeholder(R.drawable.loading).into(holder.teamRequestProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ct, exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.teamRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("production_teams").child(myList.getProduction_id()).child("member_request").child(myList.getUser_id()).removeValue();
                Production.request_manage_adapter = new ProductionRequestManageAdapter(Production.request_myLists, ct);
                Production.request_manage_rv.setAdapter(Production.request_manage_adapter);
                Production.request_manage_adapter.notifyDataSetChanged();
            }
        });

        holder.teamRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("production_teams").child(myList.getProduction_id()).child("member_request").child(myList.getUser_id()).removeValue();
                Production.request_manage_adapter = new ProductionRequestManageAdapter(Production.request_myLists, ct);
                Production.request_manage_rv.setAdapter(Production.request_manage_adapter);
                Production.request_manage_adapter.notifyDataSetChanged();


                Map<String, Object> data = new HashMap<>();
                data.put("date", ServerValue.TIMESTAMP);

                databaseReference.child("production_teams").child(myList.getProduction_id()).child("members").child(myList.getUser_id()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String, Object> data = new HashMap<>();
                        data.put("production_name", Production.productionName);
                        data.put("production_id",Production.productionID);
                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("production_team").setValue(data);
                        Toast.makeText(ct, "Success", Toast.LENGTH_SHORT).show();



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



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView teamRequestProfile;
        private TextView teamRequestDisplayName;
        private Button teamRequestReject,teamRequestAccept;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamRequestDisplayName = (TextView)itemView.findViewById(R.id.teamRequestDisplayName);
            teamRequestProfile = (ImageView)itemView.findViewById(R.id.teamRequestProfile);
            teamRequestAccept = (Button)itemView.findViewById(R.id.teamRequestAccept);
            teamRequestReject = (Button)itemView.findViewById(R.id.teamRequestReject);


        }
    }
}