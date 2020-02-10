package com.helloworld.cookeat.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.snapshot.TimeIntervalsResponse;
import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.helloworld.cookeat.Constant;
import com.helloworld.cookeat.R;
import com.helloworld.cookeat.fragment.RecipeCard;
import com.helloworld.cookeat.fragment.RecipeDetailActivity;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class RecipeFragment extends Fragment {

    public static final String LOG = "LOG";
    public static final String RECOMMENDED = "recommended";
    public static final String DEFAULT = "default";
    public static final String FAVORITE = "favorite";
    private ArrayList<String> recipeList;
    private int currentIndex;
    public static final String FAVORITE_FILE = "favorite.txt";
    private ArrayList<String> favorites;
    private boolean favoritePressed = false;
    private String selectedList = DEFAULT;
    private TextView mWeatherTextView;

    private ArrayList<String> recommendedList;
    private ArrayList<String> recommendedTag;

    private String mealType;
    private String hasTime;

    private Button addFavoriteButton;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_recipe, container, false);
        this.initRecipeList();
        this.readFavorite();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.readFavorite();

        getView().findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedList){
                    case DEFAULT :
                        if(currentIndex == recipeList.size() - 1){
                            currentIndex = 0;
                        }else{
                            currentIndex++;
                        }
                        break;

                    case FAVORITE :
                        if(currentIndex == favorites.size() - 1){
                            currentIndex = 0;
                        }else{
                            currentIndex++;
                        }
                        break;

                    case RECOMMENDED:
                        if(currentIndex == recommendedList.size() - 1){
                            currentIndex = 0;
                        }else{
                            currentIndex++;
                        }
                        break;

                    default:
                        break;
                }
                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedList){
                    case DEFAULT :
                        if(currentIndex == 0){
                            currentIndex = recipeList.size() -1;
                        }else{
                            currentIndex--;
                        }
                        break;

                    case FAVORITE :
                        if(currentIndex == 0){
                            currentIndex = favorites.size() -1;
                        }else{
                            currentIndex--;
                        }
                        break;

                    case RECOMMENDED:
                        if(currentIndex == 0){
                            currentIndex = recommendedList.size() -1;
                        }else{
                            currentIndex--;
                        }
                        break;

                    default:
                        break;
                }
                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.button_add_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedList){
                    case DEFAULT :
                        if(favorites.contains(recipeList.get(currentIndex))){
                            for (int i = 0; i < favorites.size(); i++) {
                                if(favorites.get(i).equals(recipeList.get(currentIndex))  ){
                                    favorites.remove(i);
                                }
                            }
                            if(favorites.size() == 0){
                                selectedList = DEFAULT;
                                Toast toast = Toast.makeText(getContext(),"Vous n'avez plus de favoris",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            currentIndex = 0;
                        }else{
                            favorites.add(recipeList.get(currentIndex));
                        }
                        break;

                    case FAVORITE :
                        if(favorites.contains(favorites.get(currentIndex))){
                            for (int i = 0; i < favorites.size(); i++) {
                                if(favorites.get(i).equals(favorites.get(currentIndex))  ){
                                    favorites.remove(i);
                                }
                            }
                            if(favorites.size() == 0){
                                selectedList = DEFAULT;
                                Toast toast = Toast.makeText(getContext(),"Vous n'avez plus de favoris",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            currentIndex = 0;
                        }else{
                            favorites.add(favorites.get(currentIndex));
                        }
                        break;

                    case RECOMMENDED:
                        if(favorites.contains(recommendedList.get(currentIndex))){
                            for (int i = 0; i < favorites.size(); i++) {
                                if(favorites.get(i).equals(recommendedList.get(currentIndex))  ){
                                    favorites.remove(i);
                                }
                            }
                            if(favorites.size() == 0){
                                selectedList = DEFAULT;
                                Toast toast = Toast.makeText(getContext(),"Vous n'avez plus de favoris",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            currentIndex = 0;
                        }else{
                            favorites.add(recommendedList.get(currentIndex));
                        }
                        break;

                    default:
                        break;
                }
                updateRecipeFragmentId();
                updateFavoriteButton();
                writeFavorite();
            }
        });

        getView().findViewById(R.id.recipe_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorites.size() == 0){
                    Toast toast = Toast.makeText(getContext(),"Vous n'avez aucun favoris",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    if(!selectedList.equals(FAVORITE)){
                        selectedList = FAVORITE;
                    }else{
                        selectedList = DEFAULT;
                    }
                    currentIndex = 0;
                    updateRecipeFragmentId();
                }
            }
        });

        getView().findViewById(R.id.recipe_random).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = DEFAULT;
                currentIndex = 0;
                Collections.shuffle(recipeList);
                updateRecipeFragmentId();
            }
        });

        getView().findViewById(R.id.recipe_advice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = RECOMMENDED;
                currentIndex = 0;
                getAwareness();
            }
        });

        getView().findViewById(R.id.recipe_card_fragment).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                switch (selectedList){
                    case DEFAULT :
                        intent.putExtra(Constant.RECIPE_ID, recipeList.get(currentIndex));
                        break;

                    case FAVORITE :
                        intent.putExtra(Constant.RECIPE_ID, favorites.get(currentIndex));
                        break;

                    case RECOMMENDED:
                        intent.putExtra(Constant.RECIPE_ID, recommendedList.get(currentIndex));
                        break;

                    default:
                        break;
                }
                startActivity(intent);
                return true;
            }
        });
    }

    private void getRecommendedTag() {
        recommendedTag = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tag").whereIn("name", Arrays.asList(hasTime,mealType))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //recipeList.add(document.getId());
                                recommendedTag.add(document.getData().get("id").toString());
                            }
                            getRecommendedRecipe();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getRecommendedRecipe() {
        recommendedList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.v("HERE",recommendedTag.toString());
        db.collection("recipeTag").whereIn("idTag", recommendedTag)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recommendedList.add(document.getData().get("idRecipe").toString());
                            }
                            updateRecipeFragmentId();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getAwareness(){
        Awareness.getSnapshotClient(getActivity()).getTimeIntervals()
                .addOnSuccessListener(new OnSuccessListener<TimeIntervalsResponse>() {
                    @Override
                    public void onSuccess(TimeIntervalsResponse timeIntervalsResponse) {
                        TimeIntervals intervals = timeIntervalsResponse.getTimeIntervals();

                        if(intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_WEEKDAY)){
                            hasTime = "Fast";
                        }else if( intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_WEEKEND)){
                            hasTime = "Slow";
                        }

                        if (intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_HOLIDAY)) {
                            hasTime = "Slow";
                        }

                        if (intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_MORNING)) {
                            mealType = "Breakfast";
                        } else if (intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_AFTERNOON)) {
                            mealType = "Lunch";
                        } else if (intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_EVENING)) {
                            mealType = "Diner";
                        } else if (intervals.hasTimeInterval(TimeFence.TIME_INTERVAL_NIGHT)) {
                            mealType = "Snack";
                        } else {
                            mealType = "Snack";
                        }
                        getRecommendedTag();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(LOG,e.toString());
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
        switch (selectedList){
            case DEFAULT :
                rc.setIdRecipe(this.recipeList.get(this.currentIndex));
                break;

            case FAVORITE :
                rc.setIdRecipe(this.favorites.get(this.currentIndex));
                break;

            case RECOMMENDED:
                rc.setIdRecipe(this.recommendedList.get(this.currentIndex));
                break;

            default:
                break;
        }
        rc.updateDisplay();
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        addFavoriteButton = getView().findViewById(R.id.button_add_favorite);
        switch (selectedList){
            case DEFAULT :
                if(favorites.contains(this.recipeList.get(this.currentIndex))){
                    addFavoriteButton.setText(R.string.remove_favorite);
                }else{
                    addFavoriteButton.setText(R.string.add_favorite);
                }
                break;

            case FAVORITE :
                if(favorites.contains(this.favorites.get(this.currentIndex))){
                    addFavoriteButton.setText(R.string.remove_favorite);
                }else{
                    addFavoriteButton.setText(R.string.add_favorite);
                }
                break;

            case RECOMMENDED:
                if(favorites.contains(this.recommendedList.get(this.currentIndex))){
                    addFavoriteButton.setText(R.string.remove_favorite);
                }else{
                    addFavoriteButton.setText(R.string.add_favorite);
                }
                break;

            default:
                break;
        }
    }

    public void readFavorite() {
        this.favorites = new ArrayList<>();
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(FAVORITE_FILE);
            read(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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