package com.helloworld.cookeat.ui.fragment.home;

import android.content.Intent;
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
import com.helloworld.cookeat.service.Constant;
import com.helloworld.cookeat.ui.activity.RecipeDetailActivity;
import com.helloworld.cookeat.ui.fragment.recipe.RecipeCard;

import java.util.List;

public class HomeFragment extends Fragment {
    private String teamSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        
        this.initTeamSelection();
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.homeRecipeCard).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(Constant.RECIPE_ID, teamSelection);
                startActivity(intent);
                return true;
            }
        });
    }

    public void initTeamSelection(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipe").whereEqualTo("teamSelection",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                teamSelection  = document.getId();
                            }
                            updateRecipeCard();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateRecipeCard() {
        FragmentManager fm = getChildFragmentManager();
        List<Fragment> fragmentList = fm.getFragments();
        RecipeCard rc = (RecipeCard) fragmentList.get(0);
        rc.setIdRecipe(teamSelection);
        rc.updateDisplay();
    }

}