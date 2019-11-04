package com.example.recipeexjobb;

public class Recipe {

    private int image;
    private String recipeTitle;
    private String recipeDescription;
    private String recipeIngredients;
    private String recipeInstructions;
    private int recipeCategory;
    private boolean onWeeksMenu;
    private String[] recipeTagArray;

    public Recipe(int image, String recipeTitle, String recipeDescription, String recipeIngredients, String recipeInstructions, int category) {
        this.image = image;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeIngredients = recipeIngredients;
        this.recipeInstructions = recipeInstructions;
        this.recipeCategory = category;
    }

    public int getImage() {
        return image;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public int getRecipeCategory() {
        return recipeCategory;
    }

    public boolean isOnWeeksMenu() {
        return onWeeksMenu;
    }

    public void setOnWeeksMenu(boolean onWeeksMenu) {
        this.onWeeksMenu = onWeeksMenu;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public void setRecipeCategory(int recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String[] getRecipeTagArray() {
        return recipeTagArray;
    }

    public void setRecipeTagArray(String[] recipeTagArray) {
        this.recipeTagArray = recipeTagArray;
    }
}
