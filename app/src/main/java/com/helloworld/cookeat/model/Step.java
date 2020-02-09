package com.helloworld.cookeat.model;

public class Step {
    private String id;
    private String description;
    private int stepNumber;
    private int timer;

    public Step(String id, String description, int stepNumber, int timer) {
        this.id = id;
        this.description = description;
        this.stepNumber = stepNumber;
        this.timer = timer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return "Step{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", stepNumber=" + stepNumber +
                ", timer=" + timer +
                '}';
    }
}
