package com.example.recipeexjobb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DisplayIngredientsListInRecipeAdapter extends RecyclerView.Adapter<DisplayIngredientsListInRecipeAdapter.MyViewHolder> {

    private Context context;
    private List<IngredientItem> ingredients;
    private String[] units;

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


        if(ingredients != null){

            holder.amount.setText(String.valueOf(ingredients.get(position).getAmount()));
            holder.unit.setText(units[ingredients.get(position).getUnit()]);
            holder.ingredient.setText(ingredients.get(position).getIngredient());

        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }


}
