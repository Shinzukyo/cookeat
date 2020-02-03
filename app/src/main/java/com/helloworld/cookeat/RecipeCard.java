package com.helloworld.cookeat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.logging.Logger;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class RecipeCard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public TextView pictureName;
    public ImageView recipePicture;

    // TODO: Rename and change types of parameters
    private String idRecipe = "sFQ2JOZGPORmW048IkpW";
    public RecipeCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recipe_card, container, false);
        //this.updateDisplay();
        return view;
    }

    public void setIdRecipe(String idRecipe) {
        this.idRecipe = idRecipe;
    }

    public void updateDisplay(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipe").whereEqualTo("id",this.idRecipe)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pictureName = getView().findViewById(R.id.recipeName);
                                pictureName.setText(document.getData().get("name").toString());
                                recipePicture = getView().findViewById(R.id.recipePicture);
                                Picasso.get().load(document.getData().get("imageUrl").toString()).into(recipePicture);
                            }
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
