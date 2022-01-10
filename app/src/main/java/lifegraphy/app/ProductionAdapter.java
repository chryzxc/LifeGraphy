package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionAdapter extends RecyclerView.Adapter<ProductionAdapter.ViewHolder> {
    private List<ProductionList> myListList;
    private Context ct;

    public ProductionAdapter(List<ProductionList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    public void updateList(List<ProductionList> list){
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
        ProductionList myList=myListList.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, Production.class);
                intent.putExtra("production_id",myList.getProduction_id());
                ct.startActivity(intent);
            }
        });


        holder.productionName.setText(myList.getProduction_name());
        if (myList.getNumOfMembers().matches("1")){
            holder.numOfMembers.setText(myList.getNumOfMembers()+ " member");
        }else{
            holder.numOfMembers.setText(myList.getNumOfMembers()+ " members");
        }

        holder.productionSkills.setText(myList.getDescription());
        holder.productionPrice.setText("₱"+myList.getMinimum().toString() + " - " + "₱"+myList.getMaximum().toString());

        if (MainActivity.hasATeam == true){
            holder.productionPrice.setVisibility(View.VISIBLE);
        }else{
            holder.productionPrice.setVisibility(View.GONE);
        }
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

        if (firebaseAuth.getCurrentUser() != null){
            if (MainActivity.profileTeamId.toLowerCase().matches("none")){

                holder.bookButton.setVisibility(View.VISIBLE);

            }else{
                holder.bookButton.setVisibility(View.GONE);

            }
        }






        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.bookButton.getText().equals("Book")){




                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);

                    holder.bookLayout = LayoutInflater.from(ct).inflate(R.layout.book_popup, null);

                    SingleDateAndTimePicker bookPicker = holder.bookLayout.findViewById(R.id.bookPicker);
                    TextView bookDate = holder.bookLayout.findViewById(R.id.bookDate);
                    TextView bookTime = holder.bookLayout.findViewById(R.id.bookTime);
                    Button bookCancel = holder.bookLayout.findViewById(R.id.bookCancel);
                    Button bookConfirm = holder.bookLayout.findViewById(R.id.bookConfirm);
                    TextInputEditText bookType=(TextInputEditText)holder.bookLayout.findViewById(R.id.bookType);
                    TextInputEditText bookMessage=(TextInputEditText)holder.bookLayout.findViewById(R.id.bookMessage);

                    bookDate.setText(DateFormat.format("MMM dd yyyy",new Date()));
                    bookTime.setText(DateFormat.format("hh:mm aa",new Date()));



                    bookPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(String displayed, Date date) {


                            holder.reservationDate = date.getTime();


                            bookDate.setText(DateFormat.format("MMM dd yyyy",date));
                            bookTime.setText(DateFormat.format("hh:mm aa",date));

                        }
                    });

                    bookCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.bookDialog.dismiss();
                        }
                    });

                    bookConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.currentProductionId = myList.getProduction_id();


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


                            databaseReference.child("production_teams").child(myList.getProduction_id()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (Boolean.parseBoolean(task.getResult().child("active").getValue().toString()) == true){


                                            if (new Date(holder.reservationDate).after(new Date())){

                                                MainActivity.hud = KProgressHUD.create(ct)
                                                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                        .setLabel("Creating a reservation")
                                                        .setCancellable(false)
                                                        .show();

                                                String pushId = databaseReference.child("events").push().getKey();

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("booking_date", holder.reservationDate);
                                                data.put("email", firebaseAuth.getCurrentUser().getEmail());
                                                data.put("type", bookType.getText().toString());
                                                data.put("message",bookMessage.getText().toString());
                                                data.put("client_id",firebaseAuth.getCurrentUser().getUid());
                                                data.put("production_id",myList.getProduction_id());
                                                data.put("status","ongoing");

                                                databaseReference.child("events").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        MainActivity.hud.dismiss();
                                                        holder.bookDialog.dismiss();
                                                    }

                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        MainActivity.hud.dismiss();
                                                        Toast.makeText(ct, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });



                                             /*   holder.hasConflict = false;

                                                databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if (!task.isSuccessful()) {

                                                        }
                                                        else {

                                                            for (DataSnapshot parent : task.getResult().getChildren()) {
                                                                Map<String, Object> data = (HashMap<String,Object>) parent.getValue();
                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");



                                                                if (myList.getProduction_id().matches(data.get("production_id").toString())){

                                                                if (sdf.format(holder.reservationDate).matches(sdf.format(data.get("booking_date")))){
                                                                    holder.hasConflict = true;

                                                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);

                                                                    holder.recommendedLayout = LayoutInflater.from(ct).inflate(R.layout.recommended_popup, null);
                                                                    holder.recommended_rv = (RecyclerView)holder.recommendedLayout.findViewById(R.id.recommended_rec);
                                                                    holder.recommended_rv.setHasFixedSize(true);
                                                                    holder.recommended_rv.setLayoutManager(new LinearLayoutManager(ct, LinearLayoutManager.VERTICAL, true));
                                                                    holder.recommenderLists.clear();

                                                                    TextView bookError = (TextView)holder.recommendedLayout.findViewById(R.id.bookError);
                                                                    bookError.setText("We are very sorry, The selected date is not available. We prepared you a list of available productions");
                                                                    holder.currentCount = 0;

                                                                    for (int i = 0;i < MainActivity.productionList.size();i++){
                                                                        String productionId = MainActivity.productionList.get(i);
                                                                        holder.currentCount +=1;
                                                                        Recommender recommender = new Recommender(productionId,0);

                                                                        if (!myList.getProduction_id().matches(productionId)){

                                                                            recommender.runRecommender(ct,holder.currentCount,MainActivity.productionList.size(),false);

                                                                        }else{

                                                                        }



                                                                    }


                                                                    builder.setView(holder.recommendedLayout);
                                                                    holder.recommendDialog = builder.create();

                                                                    holder.recommendDialog.setCancelable(true);
                                                                    holder.recommendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                    holder.recommendDialog.show();





                                                                    Toast.makeText(ct, "We are very sorry, The selected date is not available", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }

                                                                }


                                                            }

                                                            if (holder.hasConflict == false){

                                                                //     holder.bookButton.setText("Sent");
                                                                //     holder.bookButton.setBackgroundColor((ContextCompat.getColor(ct, R.color.violet)));
                                                                //    holder.bookButton.setTextColor((ContextCompat.getColor(ct, R.color.white)));



                                                                MainActivity.hud = KProgressHUD.create(ct)
                                                                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                                        .setLabel("Creating a reservation")
                                                                        .setCancellable(false)
                                                                        .show();

                                                                String pushId = databaseReference.child("events").push().getKey();

                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("booking_date", holder.reservationDate);
                                                                data.put("email", firebaseAuth.getCurrentUser().getEmail());
                                                                data.put("type", bookType.getText().toString());
                                                                data.put("message",bookMessage.getText().toString());
                                                                data.put("client_id",firebaseAuth.getCurrentUser().getUid());
                                                                data.put("production_id",myList.getProduction_id());
                                                                data.put("status","ongoing");

                                                                databaseReference.child("events").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        MainActivity.hud.dismiss();
                                                                        holder.bookDialog.dismiss();
                                                                    }

                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        MainActivity.hud.dismiss();
                                                                        Toast.makeText(ct, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                           }


                                                        }
                                                    }
                                                });
*/


                                            }else{
                                                Toast.makeText(ct, "Date must be in future", Toast.LENGTH_SHORT).show();
                                            }



                                        }else{

                                            // NOT ACTIVE


                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);

                                            holder.recommendedLayout = LayoutInflater.from(ct).inflate(R.layout.recommended_popup, null);
                                            holder.recommended_rv = (RecyclerView)holder.recommendedLayout.findViewById(R.id.recommended_rec);
                                            holder.recommended_rv.setHasFixedSize(true);
                                            holder.recommended_rv.setLayoutManager(new LinearLayoutManager(ct, LinearLayoutManager.VERTICAL, true));
                                            holder.recommenderLists.clear();

                                            TextView bookError = (TextView)holder.recommendedLayout.findViewById(R.id.bookError);
                                            bookError.setText("Ops, We can't process your request right now because this production won't accept booking request at the moment. Worry not and take a look on the list");
                                            holder.currentCount = 0;

                                            for (int i = 0;i < MainActivity.productionList.size();i++){
                                                String productionId = MainActivity.productionList.get(i);
                                                holder.currentCount +=1;
                                                Recommender recommender = new Recommender(productionId,0);

                                                if (!myList.getProduction_id().matches(productionId)){

                                                    recommender.runRecommender(ct,holder.currentCount,MainActivity.productionList.size(),false);

                                                }else{

                                                }

                                            }


                                            builder.setView(holder.recommendedLayout);
                                            holder.recommendDialog = builder.create();

                                            holder.recommendDialog.setCancelable(true);
                                            holder.recommendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            holder.recommendDialog.show();



                                        }

                                    }

                                }
                            });



                        }
                    });


                    builder.setView(holder.bookLayout);
                    holder.bookDialog = builder.create();

                    holder.bookDialog.setCancelable(true);
                    holder.bookDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    holder.bookDialog.show();



                }else{
                    holder.bookButton.setText("Book");
                    holder.bookButton.setBackgroundColor((ContextCompat.getColor(ct, R.color.cardview_dark_background)));
                    holder.bookButton.setTextColor(Color.WHITE);

                }




            }


        });



    }

    public void displayRecommendList(){


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private Button bookButton;
        private View bookLayout,recommendedLayout;
        private LayoutInflater inflater;
        private androidx.appcompat.app.AlertDialog bookDialog,recommendDialog;
        private TextView productionName,numOfMembers,productionSkills,productionPrice;
        private ImageView productionLogo;
        Boolean hasConflict;
        static long reservationDate;
        static  String currentProductionId;
        Integer currentCount;

        static RecyclerView recommended_rv;

        static ArrayList<RecommenderList> recommenderLists;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recommenderLists = new ArrayList<>();

            productionLogo=(ImageView)itemView.findViewById(R.id.productionLogo);
            bookButton=(Button)itemView.findViewById(R.id.bookButton);
            productionName=(TextView)itemView.findViewById(R.id.productionName);
            productionPrice=(TextView)itemView.findViewById(R.id.productionPrice);
            numOfMembers=(TextView)itemView.findViewById(R.id.numOfMembers);
            productionSkills=(TextView)itemView.findViewById(R.id.productionSkills);





        }
    }
}