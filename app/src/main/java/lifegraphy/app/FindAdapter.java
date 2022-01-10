package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.ViewHolder> {
    private List<FindList> myListList;
    private Context ct;

    public FindAdapter(List<FindList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    public void updateList(List<FindList> list){
        myListList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.production_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FindList myList=myListList.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        holder.numOfMembers.setVisibility(GONE);
        holder.numOfMembersIcon.setVisibility(GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, Production.class);
                intent.putExtra("production_id",myList.getProduction_id());
                ct.startActivity(intent);
            }
        });
        if (MainActivity.hasATeam){
            holder.productionPrice.setVisibility(GONE);
        }else{
            holder.productionPrice.setVisibility(VISIBLE);
        }



        ValueEventListener productionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Map<String, Object> production = (HashMap<String,Object>) dataSnapshot.getValue();
                    holder.productionName.setText(production.get("production_name").toString());
                    holder.productionSkills.setText(production.get("individual_skills").toString());
                    holder.productionPrice.setText("₱"+production.get("price_minimum").toString() + " - " + "₱"+production.get("price_maximum").toString());

                }else{
                    MainActivity.find_production_myLists.remove(position);
                    MainActivity.find_production_adapter = new FindAdapter(MainActivity.find_production_myLists, ct);
                    MainActivity.find_production_rv.setAdapter(MainActivity.find_production_adapter);
                    MainActivity.find_production_adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("production_teams").child(myList.getProduction_id()).addValueEventListener(productionListener);

        storageReference.child("production_teams/"+myList.getProduction_id()+"/logo/team_logo.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).centerCrop().placeholder(R.drawable.loading).dontAnimate().into(holder.productionLogo);

                holder.productionLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ct, ImageViewer.class);
                        intent.putExtra("url", "production_teams/"+myList.getProduction_id()+"/logo/team_logo.jpg");
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



    }

    public void displayRecommendList(){


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Button bookButton;
        private View bookLayout,recommendedLayout;
        private LayoutInflater inflater;
        private androidx.appcompat.app.AlertDialog bookDialog,recommendDialog;
        private TextView productionName,numOfMembers,productionSkills,productionPrice;
        private ImageView productionLogo,numOfMembersIcon;
        Boolean hasConflict;
        long reservationDate;
        Integer currentCount;

        RecyclerView recommended_rv;
        RecommenderAdapter production_adapter;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numOfMembersIcon=(ImageView)itemView.findViewById(R.id.numOfMembersIcon);
            productionLogo=(ImageView)itemView.findViewById(R.id.productionLogo);
            bookButton=(Button)itemView.findViewById(R.id.bookButton);
            productionName=(TextView)itemView.findViewById(R.id.productionName);
            productionPrice=(TextView)itemView.findViewById(R.id.productionPrice);
            numOfMembers=(TextView)itemView.findViewById(R.id.numOfMembers);
            productionSkills=(TextView)itemView.findViewById(R.id.productionSkills);







        }
    }
}