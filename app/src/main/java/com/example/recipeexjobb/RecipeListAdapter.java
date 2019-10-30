package com.example.recipeexjobb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> {
    private List<Recipe> recipeList;
    private Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

//        public CardView cardView;
        ImageView recipeImage;
        TextView recipeTitle, recipeDescription;
        public MyViewHolder(View view){
            super(view);

            recipeImage = view.findViewById(R.id.recipeImage);
            recipeTitle = view.findViewById(R.id.recipeTitle);
            recipeDescription = view.findViewById(R.id.recipeDescription);

        }
    }

    public RecipeListAdapter(Context context, List<Recipe> recipes){

        this.recipeList = recipes;
        this.context = context;
    }



    @NonNull
    @Override
    public RecipeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_card_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getRecipeTitle());
        holder.recipeDescription.setText(recipe.getRecipeDescription());

        holder.recipeImage.setImageDrawable(context.getResources().getDrawable(recipe.getImage()));

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }






}
