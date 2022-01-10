package lifegraphy.app;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private List<BookingsList> myListList;
    private Context ct;

    public BookingsAdapter(List<BookingsList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingsList myList=myListList.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        if (firebaseAuth.getCurrentUser().getUid().matches(myList.getClient_id())){
            holder.cancelBook.setVisibility(View.GONE);

        }

        holder.dateBook.setText(DateFormat.format("MMM dd, yyyy",new Date(Long.parseLong(myList.getBooking_date()))));
        holder.dayBook.setText(DateFormat.format("EEE",new Date(Long.parseLong(myList.getBooking_date()))));
        holder.timeBook.setText(DateFormat.format("hh:mm aa",new Date(Long.parseLong(myList.getBooking_date()))));

        holder.emailBook.setText(myList.getEmail());
        holder.typeBook.setText(myList.getType());

        if (position == 0){
            holder.currentBooking.setVisibility(View.VISIBLE);
            holder.nextBooking.setVisibility(View.GONE);
        }else{
            holder.currentBooking.setVisibility(View.GONE);
            holder.nextBooking.setVisibility(View.VISIBLE);
        }

        if (MainActivity.profileTeamId.toLowerCase().matches("none")){

            ValueEventListener bookListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ArrayList<String> membersList = new ArrayList<>();

                        for (DataSnapshot members : dataSnapshot.child("members").getChildren()) {
                              membersList.add(members.getKey());
                        }
                        Map<String, Object> user = (HashMap<String,Object>) dataSnapshot.getValue();
                        holder.clientBook.setText(user.get("production_name").toString());
                        holder.emailBook.setVisibility(View.GONE);
                        holder.emailBookImage.setVisibility(View.GONE);

                        if (membersList.contains(firebaseAuth.getCurrentUser().getDisplayName())){
                            holder.cancelBook.setVisibility(View.VISIBLE);
                        }else{
                            holder.cancelBook.setVisibility(View.GONE);
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            };
            databaseReference.child("production_teams").child(myList.getProduction_id()).addValueEventListener(bookListener);

        }else{

            ValueEventListener profileListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> user = (HashMap<String,Object>) dataSnapshot.getValue();
                    holder.clientBook.setText(user.get("display_name").toString());
                    holder.emailBook.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            };
            databaseReference.child("users").child(myList.getClient_id()).addValueEventListener(profileListener);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        holder.cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("events").child(myList.getEvent_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ct, "Canceled", Toast.LENGTH_SHORT).show();
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


        private TextView dateBook,dayBook,timeBook,clientBook,emailBook,typeBook;
        private ConstraintLayout currentBooking,nextBooking;
        private Button cancelBook;
        ImageView emailBookImage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateBook=(TextView)itemView.findViewById(R.id.dateBook);
            dayBook=(TextView)itemView.findViewById(R.id.dayBook);
            timeBook=(TextView)itemView.findViewById(R.id.timeBook);
            clientBook=(TextView)itemView.findViewById(R.id.clientBook);
            emailBook=(TextView)itemView.findViewById(R.id.emailBook);

            emailBookImage=(ImageView)itemView.findViewById(R.id.emailBookImage);

            typeBook=(TextView)itemView.findViewById(R.id.typeBook);

            cancelBook=(Button)itemView.findViewById(R.id.cancelBook);

            currentBooking=(ConstraintLayout)itemView.findViewById(R.id.currentBooking);
            nextBooking=(ConstraintLayout)itemView.findViewById(R.id.nextBooking);


        }
    }
}