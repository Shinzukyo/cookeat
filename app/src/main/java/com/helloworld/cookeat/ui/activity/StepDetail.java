package com.helloworld.cookeat.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.helloworld.cookeat.R;
import com.helloworld.cookeat.model.Step;
import com.helloworld.cookeat.service.Constant;
import com.helloworld.cookeat.ui.widget.StepWidget;

import java.util.ArrayList;
import java.util.List;

public class StepDetail extends AppCompatActivity {

    private String idRecipe;
    private String recipeName;
    private List<Step> steps;
    private int currentIndex;

    private int timerValue;

    TextView recipeNameView;
    TextView stepDetail;
    TextView timerTextView;

    CountDownTimer timer;


    Button timerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        timerTextView = findViewById(R.id.timerTextView);

        this.idRecipe = getIntent().getStringExtra(Constant.RECIPE_ID);
        this.recipeName = getIntent().getStringExtra(Constant.RECIPE_NAME);
        this.currentIndex = 1;
        this.initSteps();

        findViewById(R.id.previousButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == 1){
                    currentIndex = 1;
                }else{
                    currentIndex--;
                }
                updateStepDisplay();
            }
        });

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == steps.size()){
                    currentIndex = steps.size();
                }else{
                    currentIndex++;
                }
                updateStepDisplay();
            }
        });

        findViewById(R.id.timerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTimer();
            }
        });
    }

    private void launchTimer() {
        if(this.timer != null){
            this.timer.cancel();
            this.timer = null;
            timerTextView.setText("");
        }else{
            this.timer =  new CountDownTimer(this.timerValue, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    timerTextView.setText(String.format("%02d", minutes)+ ":"+String.format("%02d", seconds));
                }

                public void onFinish() {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                }

            }.start();
        }

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
        String title = this.recipeName+ " / Étape " + currentIndex;
        recipeNameView = findViewById(R.id.recipeName);
        recipeNameView.setText("");
        recipeNameView.setText(title);

        stepDetail = findViewById(R.id.stepDetail);
        stepDetail.setText("");

        for(Step step : this.steps){
            if(step.getStepNumber() == currentIndex){
                stepDetail.setText(step.getDescription());
                updateWidget(step.getDescription());
                updateTimerButton(step.getTimer());
            }
        }
    }

    private void updateTimerButton(int timer) {
        timerButton = findViewById(R.id.timerButton);
        if(timer == 0){
            timerButton.setVisibility(View.GONE);
        }else{
            this.timerValue = timer * 60000;
            timerButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateWidget(String description) {
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        StepWidget.updateWidget(this, awm, description);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emptyWidget();
    }

    private void emptyWidget() {
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        StepWidget.updateWidget(this, awm, "");
    }
}
