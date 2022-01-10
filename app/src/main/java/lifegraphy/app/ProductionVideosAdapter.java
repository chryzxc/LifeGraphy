package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.potyvideo.library.AndExoPlayerView;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class ProductionVideosAdapter extends RecyclerView.Adapter<ProductionVideosAdapter.ViewHolder> {
    private List<ProductionVideosList> myListList;
    private Context ct;

    public ProductionVideosAdapter(List<ProductionVideosList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductionVideosList myList=myListList.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child("production_teams/"+Production.productionID+"/videos/"+myList.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).fitCenter().centerCrop().into(holder.production_portfolio_video_preview);
                holder.production_portfolio_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ct, VideoPlayer.class);
                        intent.putExtra("url","production_teams/"+Production.productionID+"/videos/"+myList.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ct.startActivity(intent);

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ct, "Load Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView production_portfolio_video_preview;
        private CardView production_portfolio_video_play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            production_portfolio_video_preview=(ImageView)itemView.findViewById(R.id.production_portfolio_video_preview);
            production_portfolio_video_play=(CardView)itemView.findViewById(R.id.production_portfolio_video_play);

        }
    }
}