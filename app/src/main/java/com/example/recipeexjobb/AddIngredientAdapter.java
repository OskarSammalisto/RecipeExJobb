package com.example.recipeexjobb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.MyViewHolder>  {

    private Context context;
    private int numberOfIngredients;
    private List<IngredientItem> ingredientsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        EditText setAmount, setIngredient;
        Spinner setUnit;

        public MyViewHolder(View view){
            super(view);

            setAmount = view.findViewById(R.id.setAmountTextView);
            setIngredient = view.findViewById(R.id.setIngredientTextView);
            setUnit = view.findViewById(R.id.setUnitSpinner);

        }
    }

    public AddIngredientAdapter(Context context, List<IngredientItem> ingredientsList){

        this.context = context;
        this.ingredientsList = ingredientsList;


    }


    @NonNull
    @Override
    public AddIngredientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_recipe_item_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientAdapter.MyViewHolder holder, int position) {
        if(ingredientsList != null){

            holder.setAmount.setText(String.valueOf(ingredientsList.get(position).getAmount()));
            holder.setIngredient.setText(ingredientsList.get(position).getIngredient());

        }



    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }
}
