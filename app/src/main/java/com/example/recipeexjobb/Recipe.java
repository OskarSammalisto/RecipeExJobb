package com.example.recipeexjobb;

public class Recipe {

    private int image;
    private String recipeTitle;
    private String recipeDescription;
    private String recipeIngredients;
    private String recipeInstructions;

    public Recipe(int image, String recipeTitle, String recipeDescription, String recipeIngredients, String recipeInstructions) {
        this.image = image;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeIngredients = recipeIngredients;
        this.recipeInstructions = recipeInstructions;
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
}
