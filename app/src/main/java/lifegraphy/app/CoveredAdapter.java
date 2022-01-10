package lifegraphy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.util.Date;
import java.util.List;

public class CoveredAdapter extends RecyclerView.Adapter<CoveredAdapter.ViewHolder> {
    private List<CoveredList> myListList;
    private Context ct;

    public CoveredAdapter(List<CoveredList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.covered_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoveredList myList=myListList.get(position);

        holder.eventType.setText(myList.getEvent());

    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView eventType;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            eventType=(TextView)itemView.findViewById(R.id.eventType);



        }
    }
}