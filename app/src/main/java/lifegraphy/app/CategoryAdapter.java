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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryList> myListList;
    private Context ct;

    public CategoryAdapter(List<CategoryList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_rec,parent,false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryList myList=myListList.get(position);


        holder.categoryName.setText(myList.getCategory());
        Glide.with(ct).load(myList.getImage()).centerCrop().into(holder.categoryImage);

        if (myList.getChecked() == false){
            holder.categoryCheck.setVisibility(View.GONE);
            holder.categoryLayout.setStrokeColor(ContextCompat.getColor(ct,R.color.black));
            holder.categoryLayout.setStrokeWidth(0);
            myList.setChecked(false);
        }else{
            holder.categoryCheck.setVisibility(View.VISIBLE);
            holder.categoryLayout.setStrokeColor(ContextCompat.getColor(ct,R.color.teal_200));
            holder.categoryLayout.setStrokeWidth(3);
            myList.setChecked(true);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myList.getChecked() == false){
                    holder.categoryCheck.setVisibility(View.VISIBLE);
                    holder.categoryLayout.setStrokeColor(ContextCompat.getColor(ct,R.color.teal_200));
                    holder.categoryLayout.setStrokeWidth(3);
                    myList.setChecked(true);
                }else{
                    holder.categoryCheck.setVisibility(View.GONE);
                    holder.categoryLayout.setStrokeColor(ContextCompat.getColor(ct,R.color.black));
                    holder.categoryLayout.setStrokeWidth(0);
                    myList.setChecked(false);
                }
            }
        });

    }




    @Override
    public int getItemCount() {
        return myListList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryImage,categoryCheck;
        private TextView categoryName;
        private MaterialCardView categoryLayout;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = (TextView)itemView.findViewById(R.id.categoryName);
            categoryImage = (ImageView)itemView.findViewById(R.id.categoryImage);
            categoryCheck = (ImageView)itemView.findViewById(R.id.categoryCheck);
            categoryLayout = (MaterialCardView)itemView.findViewById(R.id.categoryLayout);



        }
    }
}