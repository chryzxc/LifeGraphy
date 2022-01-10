package lifegraphy.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommender {
    int memberCount;
    int memberCountVerifier;
    FirebaseFirestore db;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    ArrayList<String> memberList = new ArrayList<>();
    List<RecommenderList> recommender_myList;

    private String productionId;
    private int score;

    public Recommender(String productionId ,int score) {
        this.productionId = productionId;
        this.score  = score;

    }
    public void runRecommender(Context context,int currentCount,int productionListSize,Boolean fromProduction){
        recommender_myList = new ArrayList<>();
        databaseReference.child("production_teams").child(productionId).child("members").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    memberCount = 0;

                    for (DataSnapshot dsp : task.getResult().getChildren()) {
                        memberCount +=1;
                    }
                    loopMembers(context,currentCount,productionListSize,fromProduction);

                }
            }
        });


    }


    public void loopMembers(Context context,int currentCount,int productionListSize,Boolean fromProduction){

            databaseReference.child("production_teams").child(productionId).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {


                        memberCountVerifier = 0;

                        for (DataSnapshot dsp : task.getResult().getChildren()) {
                            memberList.add(dsp.getKey());

                        }

                        loopCategories(context,memberList.get(memberCountVerifier),currentCount, productionListSize,fromProduction);

                    }
                }
            });
        }

    public void loopCategories(Context context, String member,int currentCount,int productionListSize,Boolean fromProduction){

            databaseReference.child("users").child(member).child("categories").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {

                        for (DataSnapshot dsp : task.getResult().getChildren()) {

                            if (MainActivity.userCategories != null){

                                if (MainActivity.userCategories.contains(dsp.getValue().toString().trim())){
                                    score += 1;
                                }
                            }else{

                            }

                        }

                     //   setScore(score);


                        if (memberCountVerifier != memberCount){
                            loopCategories(context,memberList.get(memberCountVerifier),currentCount,productionListSize,fromProduction);
                            memberCountVerifier +=1;

                        }else{


                            //final loop

                            databaseReference.child("events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {

                                    }
                                    else {

                                    Boolean hasConflict = false;

                                        for (DataSnapshot parent : task.getResult().getChildren()) {
                                            Map<String, Object> data = (HashMap<String,Object>) parent.getValue();
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


                                            if (fromProduction == false){
                                                if (!ProductionAdapter.ViewHolder.currentProductionId.matches(data.get("production_id").toString())){

                                                    if (sdf.format(ProductionAdapter.ViewHolder.reservationDate).matches(sdf.format(data.get("booking_date")))){
                                                        hasConflict = true;
                                                        break;
                                                    }

                                                }
                                            }else{
                                                if (!Production.productionID.matches(data.get("production_id").toString())){


                                                    if (sdf.format(Production.reservationDate).matches(sdf.format(data.get("booking_date")))){
                                                        hasConflict = true;
                                                        break;
                                                    }

                                                }
                                            }



                                        }

                                        if (hasConflict == false){
                                          //  if (score != 0){
                                            if (fromProduction == false){
                                                ProductionAdapter.ViewHolder.recommenderLists.add(new RecommenderList(productionId,score));
                                            }else{
                                                Production.recommenderLists.add(new RecommenderList(productionId,score));
                                            }





                                                //    recommender_myList.add(new RecommenderList(productionId,score));

                                                //    Toast.makeText(context, productionId+ " : "+ String.valueOf(score), Toast.LENGTH_SHORT).show();
                                                setProductionId(productionId);
                                                setScore(score);


                                                //      if (productionListSize -1  == currentCount ){


                                                if (fromProduction == false){
                                                    Collections.sort(ProductionAdapter.ViewHolder.recommenderLists);
                                                 //   Collections.sort(ProductionAdapter.ViewHolder.recommenderLists, new Comparator< RecommenderList >() {
                                                 //       @Override public int compare(RecommenderList p1, RecommenderList p2) {
                                                 //           return p1.getScore()- p2.getScore(); // Ascending
                                                 //       }
                                                 //   });
/*
                                                    Collections.sort(ProductionAdapter.ViewHolder.recommenderLists, new Comparator<RecommenderList>() {

                                                        @Override
                                                        public int compare(RecommenderList o1, RecommenderList o2) {
                                                            return Double.compare(o1.getScore(), o2.getScore());
                                                        }

                                                    });




 */

                                                    RecommenderAdapter recommender_adapter = new RecommenderAdapter(ProductionAdapter.ViewHolder.recommenderLists, context);
                                                    ProductionAdapter.ViewHolder.recommended_rv.setAdapter(recommender_adapter);
                                                    recommender_adapter.notifyDataSetChanged();


                                                }else{
                                                    Collections.sort(Production.recommenderLists);
                                                 //   Collections.sort(Production.recommenderLists, new Comparator< RecommenderList >() {
                                                 //       @Override public int compare(RecommenderList p1, RecommenderList p2) {
                                                //            return p1.getScore()- p2.getScore(); // Ascending
                                                //        }
                                                //    });
/*
                                                    Collections.sort(Production.recommenderLists, new Comparator<RecommenderList>() {

                                                        @Override
                                                        public int compare(RecommenderList o1, RecommenderList o2) {
                                                            return Double.compare(o1.getScore(), o2.getScore());
                                                        }

                                                    });



 */
                                                    RecommenderAdapter recommender_adapter = new RecommenderAdapter(Production.recommenderLists, context);
                                                    Production.recommended_rv.setAdapter(recommender_adapter);
                                                    recommender_adapter.notifyDataSetChanged();

                                                }


                                          //  Toast.makeText(context, String.valueOf(score) + productionId, Toast.LENGTH_SHORT).show();




                                            }



                                    }
                                }
                            });

                        }

                    }

                }
            });

        }



    public void setProductionId(String productionId){
        this.productionId = productionId;
    }

    public void setScore(int score){
        this.score = score;
    }

    public String getProductionId() {

        return productionId;
    }

    public int getScore() {

        return score;
    }



}
