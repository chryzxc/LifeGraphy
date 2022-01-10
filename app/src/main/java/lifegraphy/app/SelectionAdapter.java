package lifegraphy.app;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    private List<SelectionList> myListList;
    private Context ct;

    public SelectionAdapter(List<SelectionList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.select_rec,parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectionList myList=myListList.get(position);




        //holder.selectionImage.setImageDrawable(ct.getResources().getDrawable(myList.getImage()));
        Glide.with(ct).load(myList.getImage()).into(holder.selectionImage);


        holder.select_title.setText(myList.getTitle());
        holder.select_description.setText(myList.getDescription());
        holder.select_identifier.setText(myList.getIdentifier());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MainActivity.chipNavigationBar.setItemSelected(R.id.production, true);
                if (position==0){
                    MainActivity.displayFind(ct);
                }
            }
        });



    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView selectionImage;
        private TextView select_title, select_identifier, select_description;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selectionImage=(ImageView)itemView.findViewById(R.id.selectionImage);
            select_title = (TextView) itemView.findViewById(R.id.select_title);
            select_identifier = (TextView) itemView.findViewById(R.id.select_identifier);
            select_description = (TextView) itemView.findViewById(R.id.select_description);



           // receiptName=(TextView)itemView.findViewById(R.id.receiptName);
           // receiptDate=(TextView)itemView.findViewById(R.id.receiptDate);




        }
    }
}