package com.example.recipeexjobb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DisplayIngredientsListInRecipeAdapter extends RecyclerView.Adapter<DisplayIngredientsListInRecipeAdapter.MyViewHolder> {

    private Context context;
    private List<IngredientItem> ingredients;
    private String[] units;
    private List<TextView> textViewList = new ArrayList<>();
    private boolean unitChanged = false;
    private Double currentMultiplier = 1.0;


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView amount, unit, ingredient;




        public MyViewHolder(View view){
            super(view);

            amount = view.findViewById(R.id.ingredientAmount);
            unit = view.findViewById(R.id.measurementUnit);
            ingredient = view.findViewById(R.id.ingredientName);



        }
    }


    public DisplayIngredientsListInRecipeAdapter(Context context, Recipe recipe){
        this.context = context;

        ingredients = recipe.getIngredientsList();
        units = context.getResources().getStringArray(R.array.measurementUnits);



    }


    @NonNull
    @Override
    public DisplayIngredientsListInRecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_list_item_layout, null);




        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayIngredientsListInRecipeAdapter.MyViewHolder holder, int position) {

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        textViewList.clear();

        if(ingredients != null){

            String unitAmount = (unitChanged) ? String
                    .valueOf(format.format(ingredients.get(position).getAltAmount()* currentMultiplier ))
                    : String.valueOf(format.format(ingredients.get(position).getAmount()*currentMultiplier));

            String ingrUnit = (unitChanged) ? units[ingredients.get(position).getAltUnit()]  : units[ingredients.get(position).getUnit()];


            holder.amount.setText(unitAmount);


            textViewList.add(holder.amount);


            holder.unit.setText(ingrUnit);
            holder.ingredient.setText(ingredients.get(position).getIngredient());

        }

    }

//    public void updateIngredientsList(List<IngredientItem> ingredientItemList){
//        this.ingredients = ingredientItemList;
//        notifyDataSetChanged();
//    }

    public void multiplyRecipe(Double multiplier){

        this.currentMultiplier = multiplier;

//        DecimalFormat format = new DecimalFormat();
//        format.setDecimalSeparatorAlwaysShown(false);
//        int i = 0;
//
//        for(TextView tv : textViewList){
//            tv.setText(String.valueOf(format.format(ingredients.get(i).getAmount() * multiplier)));
//            i++;
//        }

        notifyDataSetChanged();
    }

    public void changeUnits(){

        unitChanged = !unitChanged;
        notifyDataSetChanged();




    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }


}
