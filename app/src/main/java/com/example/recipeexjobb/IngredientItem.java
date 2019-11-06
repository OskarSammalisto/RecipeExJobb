package com.example.recipeexjobb;

public class IngredientItem {
    private double amount;
    private int unit;
    private String ingredient;

    public IngredientItem(){
//            this.amount = 0;
//            this.unit = 0;
//            this.ingredient = "Ingredient Name Empty";
    }

    public IngredientItem(double amount, int unit, String ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.ingredient = ingredient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
