package com.helloworld.cookeat.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.helloworld.cookeat.Constant;
import com.helloworld.cookeat.R;
import com.helloworld.cookeat.StepDetail;
import com.helloworld.cookeat.model.Ingredient;
import com.helloworld.cookeat.model.Recipe;
import com.helloworld.cookeat.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private String idRecipe;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private Recipe recipe;
    TextView pictureName;
    ImageView recipePicture;
    TextView recipeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        this.idRecipe = getIntent().getStringExtra(Constant.RECIPE_ID);

        this.initRecipe();
        this.initSteps();
        this.initIngredients();

        findViewById(R.id.ingredientButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIngredientDisplay();
            }
        });

        findViewById(R.id.stepButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepDisplay();
            }
        });

        findViewById(R.id.beginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StepDetail.class);
                intent.putExtra(Constant.RECIPE_ID, idRecipe);
                intent.putExtra(Constant.RECIPE_NAME, recipe.getName());
                startActivity(intent);
            }
        });

    }

    private void initRecipe() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipe").whereEqualTo("id",this.idRecipe)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recipe = new Recipe(document.getData().get("id").toString(),
                                        document.getData().get("imageUrl").toString(),
                                        document.getData().get("name").toString());
                            }
                            updateRecipeDisplay();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateRecipeDisplay() {
        pictureName = findViewById(R.id.recipeName);
        pictureName.setText(recipe.getName());
        recipePicture = findViewById(R.id.recipePicture);
        Picasso.get().load(recipe.getImageUrl()).into(recipePicture);
    }

    private void initSteps() {
        this.steps = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("step").whereEqualTo("idRecipe",this.idRecipe)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Step step = new Step(document.getData().get("id").toString(),
                                                     document.getData().get("description").toString(),
                                                     Integer.parseInt(document.getData().get("stepNumber").toString()),
                                                     Integer.parseInt(document.getData().get("timer").toString()));
                                steps.add(step);
                            }
                            updateStepDisplay();
                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateStepDisplay() {
        String details = "";
        for(int i = 1; i <= steps.size(); i++){
            for(Step step : steps){
                if(step.getStepNumber() == i){
                    details += i +") " + step.getDescription() + "\n";
                }
            }
            // details += " -" + step.getDescription() + "\n";
        }
        recipeDetail = findViewById(R.id.recipeDetails);
        recipeDetail.setText("");
        recipeDetail.setText(details);
    }

    private void initIngredients() {
        this.ingredients = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipeIngredient").whereEqualTo("idRecipe",this.idRecipe)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ingredient ingredient = new Ingredient( document.getData().get("idIngredient").toString(),
                                                                    "",
                                                                        document.getData().get("quantity").toString());
                                ingredients.add(ingredient);
                            }
                            getIngredientName();

                        } else {
                            Log.d("TEST", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void getIngredientName() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(final Ingredient ingredient : ingredients ){
            db.collection("ingredient").whereEqualTo("id",ingredient.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ingredient.setName(document.getData().get("name").toString());
                                }
                                updateIngredientDisplay();
                            } else {
                                Log.d("TEST", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void updateIngredientDisplay() {
        String details = "";
        for(Ingredient ingredient: ingredients){
                details += " - " + ingredient.toString() + "\n";
        }
            // details += " -" + step.getDescription() + "\n";
        recipeDetail = findViewById(R.id.recipeDetails);
        recipeDetail.setText("");
        recipeDetail.setText(details);
    }
}
