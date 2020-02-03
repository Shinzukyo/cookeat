package com.helloworld.cookeat.ui.recipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeFragment extends Fragment {

    private ArrayList<String> recipeList;
    private int currentIndex;
    public static final String FAVORITE_FILE = "favorite.txt";
    private ArrayList<String> favorites;
    private boolean favoritePressed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_recipe, container, false);
        this.initRecipeList();
        this.readFavorite();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.readFavorite();

        getView().findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoritePressed){
                    if(currentIndex == favorites.size() - 1){
                        currentIndex = 0;
                    }else{
                        currentIndex++;
                    }
                }else{
                    if(currentIndex == recipeList.size() - 1){
                        currentIndex = 0;
                    }else{
                        currentIndex++;
                    }
                }

                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoritePressed){
                    if(currentIndex == 0){
                        currentIndex = favorites.size() -1;
                    }else{
                        currentIndex--;
                    }
                }else{
                    if(currentIndex == 0){
                        currentIndex = recipeList.size() -1;
                    }else{
                        currentIndex--;
                    }
                }

                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.button_add_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorites.contains(recipeList.get(currentIndex))){
                    for (int i = 0; i < favorites.size(); i++) {
                        if(favorites.get(i).equals(recipeList.get(currentIndex))  ){
                            favorites.remove(i);
                        }
                    }
                }else{
                    favorites.add(recipeList.get(currentIndex));
                }
                Log.v("AFTER PRESS ADD",favorites.toString());
                writeFavorite();
            }
        });

        getView().findViewById(R.id.recipe_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePressed = !favoritePressed;
                currentIndex = 0;
                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.recipe_advice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFavorite();
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
        if(favoritePressed){
            rc.setIdRecipe(this.favorites.get(this.currentIndex));
        }else{
            rc.setIdRecipe(this.recipeList.get(this.currentIndex));
        }
        rc.updateDisplay();
    }

    public void readFavorite() {
        this.favorites = new ArrayList<>();
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(FAVORITE_FILE);
            read(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.v("AFTER READ",favorites.toString());
    }

    public void writeFavorite() {
        this.deleteFavorites();
        try {
            FileOutputStream fileOutputStream = getActivity().openFileOutput(FAVORITE_FILE, Context.MODE_APPEND);
            write(fileOutputStream, TextUtils.join("\n",favorites));
        } catch (FileNotFoundException e ) {
            e.printStackTrace();
        }
        this.readFavorite();
    }

    public void deleteFavorites(){
        File dir = getActivity().getFilesDir();
        File file = new File(dir, FAVORITE_FILE);
        boolean deleted = file.delete();
    }

    private void read(FileInputStream fis) {
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                favorites.add(line);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(FileOutputStream fos, String toWrite) {
        try {
            fos.write(toWrite.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}