package lifegraphy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    private List<TagsList> myListList;
    private Context ct;
    private String loadType;

    public TagsAdapter(List<TagsList> myListList, Context ct,String loadType) {
        this.myListList = myListList;
        this.ct = ct;
        this.loadType = loadType;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_rec,parent,false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagsList myList=myListList.get(position);

        holder.imageTag.setText(myList.getTag());

        if (loadType.matches("photo")){
            holder.tagsRemove.setVisibility(View.GONE);

        }else if (loadType.matches("create")){
            holder.tagsRemove.setVisibility(View.VISIBLE);
        }


        holder.tagsRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadType.matches("create")){

                    MainActivity.tags_myLists.remove(position);
                    MainActivity.tags_adapter = new TagsAdapter(MainActivity.tags_myLists, ct,loadType);
                    MainActivity.tags_rv.setAdapter(MainActivity.tags_adapter);
                    MainActivity.tags_adapter.notifyDataSetChanged();

                }else if (loadType.matches("profile")){

                    Profile.tags_myLists.remove(position);
                    Profile.tags_adapter = new TagsAdapter(Profile.tags_myLists, ct,loadType);
                    Profile.tags_rv.setAdapter(Profile.tags_adapter);
                    Profile.tags_adapter.notifyDataSetChanged();

                }





            }
        });


    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView imageTag;
        private CardView tagsRemove;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTag = (TextView)itemView.findViewById(R.id.imageTag);
            tagsRemove = (CardView)itemView.findViewById(R.id.tagsRemove);


        }
    }
}