package com.example.recipeexjobb;

public class Recipe {

    private int image;
    private String recipeTitle;
    private String recipeDescription;

    public Recipe(int image, String recipeTitle, String recipeDescription) {
        this.image = image;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
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
}
