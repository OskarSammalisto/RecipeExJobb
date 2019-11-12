package com.example.recipeexjobb;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int image;
    private String recipeTitle;
    private String recipeDescription;
    private String recipeIngredients;
    private String recipeInstructions;
    private int recipeCategory;
    private boolean onWeeksMenu;
    private List<IngredientItem> ingredientsList;
    private String recipestorageID;
    private String imageUri;
    private String ovenHeat, prepTime, cookTime;
    private int hitCount;

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public String getOvenHeat() {
        return ovenHeat;
    }

    public void setOvenHeat(String ovenHeat) {
        this.ovenHeat = ovenHeat;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getRecipestorageID() {
        return recipestorageID;
    }

    public String setRecipestorageID(){
        long tsLong = System.currentTimeMillis()/1000;
        this.recipestorageID = Long.toString(tsLong);

        return recipestorageID;

    }

    public Recipe(){

    }

    public Recipe(String image, String recipeTitle, String recipeDescription,
                  String recipeInstructions, int category, List<IngredientItem> ingredientsList, String ovenHeat, String prepTime, String cookTime) {
        this.imageUri = image;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeIngredients = recipeIngredients;
        this.recipeInstructions = recipeInstructions;
        this.recipeCategory = category;
        this.ingredientsList = ingredientsList;
        this.ovenHeat = ovenHeat;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
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

    public List<IngredientItem> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<IngredientItem> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
}
