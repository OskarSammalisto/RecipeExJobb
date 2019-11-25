package com.example.recipeexjobb;

import android.widget.Switch;

public class IngredientItem {
    private double amount;
    private double altAmount;
    private int unit;
    private int altUnit;
    private String ingredient;

    public IngredientItem(){

    }

    public IngredientItem(double amount, int unit, String ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.ingredient = ingredient;

        switch(unit){

            case 1:
                //kg to Pound
                this.altAmount = amount * 2.205;
                this.altUnit = 10;
                break;

            case 2:
                //hg to ounce
                this.altAmount = amount * 3.53;
                this.altUnit = 11;
                break;

            case 3:
                //gram to ounce
                this.altAmount = amount / 28.35;
                this.altUnit = 11;
                break;

            case 4:
                //litre to pint
                this.altAmount = amount * 2.113;
                this.altUnit = 12;
                break;

            case 5:
                //dl to cups
                this.altAmount = amount / 2.366;
                this.altUnit = 14;
                break;

            case 6:
                //cl to fl.oz
                this.altAmount = amount / 2.957;
                this.altUnit = 13;
                break;

            case 7:
                 //ml to fl.oz
                this.altAmount = amount / 29.574;
                this.altUnit = 13;
                break;

            case 10:
                //Pound to kg
                this.altAmount = amount / 2.205;
                 this.altUnit = 1;
                 break;

            case 11:
                //Ounce to hg
                this.altAmount = amount * 0.28;
                this.altUnit = 2;
                break;

            case 12:
                //Pint to litre
                this.altAmount = amount / 2.113;
                this.altUnit = 4;
                break;

            case 13:
                //fl.oz to cl
                this.altAmount = amount * 2.957;
                this.altUnit = 6;
                break;

            case 14:
                //cups to dl
                this.altAmount = amount * 2.366;
                this.altUnit = 5;
                break;

            default:
                this.altAmount = amount;
                this.altUnit = unit;

        }



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

    public double getAltAmount() {
        return altAmount;
    }

    public void setAltAmount(double altAmount) {
        this.altAmount = altAmount;
    }

    public int getAltUnit() {
        return altUnit;
    }

    public void setAltUnit(int altUnit) {
        this.altUnit = altUnit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
