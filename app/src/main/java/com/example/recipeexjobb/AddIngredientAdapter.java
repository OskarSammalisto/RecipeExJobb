package com.example.recipeexjobb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.MyViewHolder>  {

    private Context context;
    private int numberOfIngredients;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        EditText setAmount, setingredient;
        Spinner setUnit;

        public MyViewHolder(View view){
            super(view);

            setAmount = view.findViewById(R.id.setAmountTextView);
            setingredient = view.findViewById(R.id.setIngredientTextView);
            setUnit = view.findViewById(R.id.setUnitSpinner);

        }
    }

    public AddIngredientAdapter(Context context, int index){
        this.context = context;
        this.numberOfIngredients = index;

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

    }

    @Override
    public int getItemCount() {
        return numberOfIngredients;
    }
}
