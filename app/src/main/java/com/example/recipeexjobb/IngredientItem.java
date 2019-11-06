package com.example.recipeexjobb;

public class IngredientItem {
    private int amount, unit;
    private String ingredient;

    public IngredientItem(){
//            this.amount = 0;
//            this.unit = 0;
//            this.ingredient = "Ingredient Name Empty";
    }

    public IngredientItem(int amount, int unit, String ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.ingredient = ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
