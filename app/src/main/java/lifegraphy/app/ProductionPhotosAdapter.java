package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductionPhotosAdapter extends RecyclerView.Adapter<ProductionPhotosAdapter.ViewHolder> {
    private List<ProductionPhotosList> myListList;
    private Context ct;

    public ProductionPhotosAdapter(List<ProductionPhotosList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionPhotosList myList=myListList.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child("production_teams/"+Production.productionID+"/photos/"+myList.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Glide.with(ct).load(uri).fitCenter().centerCrop().into(holder.production_portfolio_photo);

                holder.production_portfolio_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ct, ImageViewer.class);
                        intent.putExtra("url","production_teams/"+Production.productionID+"/photos/"+myList.getId());
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



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView production_portfolio_photo;
        private TextView postCaption,postUser,postUserDetails,postDate;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            production_portfolio_photo=(ImageView)itemView.findViewById(R.id.production_portfolio_photo);

        }
    }
}