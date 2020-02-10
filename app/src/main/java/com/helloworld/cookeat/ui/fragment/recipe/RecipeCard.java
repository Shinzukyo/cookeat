package com.helloworld.cookeat.ui.fragment.recipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.helloworld.cookeat.R;
import com.squareup.picasso.Picasso;


public class RecipeCard extends Fragment {
    public TextView pictureName;
    public ImageView recipePicture;

    private String idRecipe;
    public RecipeCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recipe_card, container, false);
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
