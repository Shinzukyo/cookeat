package com.helloworld.cookeat.ui.recipe;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.helloworld.cookeat.R;
import com.helloworld.cookeat.RecipeCard;


import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private ArrayList<String> recipeList;
    private int currentIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_recipe, container, false);
        this.initRecipeList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == recipeList.size() - 1){
                    currentIndex = 0;
                }else{
                    currentIndex++;
                }
                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == 0){
                    currentIndex = currentIndex = recipeList.size() -1;
                }else{
                    currentIndex--;
                }
                updateRecipeFragmentId();
            }
        });
    }

    public void initRecipeList(){
        recipeList = new ArrayList<>();
        currentIndex = 0;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recipeList.add(document.getId());
                            }
                            updateRecipeFragmentId();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void updateRecipeFragmentId(){
        FragmentManager fm = getChildFragmentManager();
        List<Fragment> fragmentList = fm.getFragments();
        RecipeCard rc = (RecipeCard) fragmentList.get(0);
        rc.setIdRecipe(this.recipeList.get(this.currentIndex));
        rc.updateDisplay();
    }
}