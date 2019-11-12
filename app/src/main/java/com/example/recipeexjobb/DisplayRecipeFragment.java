package com.example.recipeexjobb;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayRecipeFragment extends Fragment {

    Recipe recipe;
    ImageButton starButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.fragment_display_recipe, container, false);


        recipe = ((MainActivity) getActivity()).getCurrentRecipe();

        //exit button
        ImageButton exitButton = view.findViewById(R.id.exitRecipe);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        starButton = view.findViewById(R.id.addToWeekMenu);
        toggleStar();

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setOnWeeksMenu(!recipe.isOnWeeksMenu());
                toggleStar();
                ((MainActivity) getActivity()).redrawList();
            }
        });





        //delete recipe
        ImageButton deleteButton = view.findViewById(R.id.deleteRecipe);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).deleteRecipe(recipe);
                closeFragment();
                Toast.makeText(getContext(), "Recipe Deleted", Toast.LENGTH_SHORT).show();

            }
        });


        //set recipe image
        ImageView image = view.findViewById(R.id.recipeImageView);
        try{
            image.setImageURI(Uri.parse(recipe.getImageUri()));
        }
        catch (Exception e){
            //TODO: set some other placeholder image instead.
        }


        //fill in heat and time info
        TextView ovenHeat = view.findViewById(R.id.displayOvenHeatTV);
        ovenHeat.setText(recipe.getOvenHeat());

        TextView prepTime = view.findViewById(R.id.displayPrepTimeTV);
        prepTime.setText(recipe.getPrepTime());

        TextView cookTime = view.findViewById(R.id.displayCookTimeTV);
        cookTime.setText(recipe.getCookTime());


        //fill title text view
        TextView title = view.findViewById(R.id.recipeTitle);
        title.setText(recipe.getRecipeTitle());

        //fill description text view
        TextView description = view.findViewById(R.id.recipeDescription);
        description.setText(recipe.getRecipeDescription());

        //fill recycle view with ingredients
        RecyclerView ingredientsList = view.findViewById(R.id.ingredientsListRecycleView);
        ingredientsList.setHasFixedSize(true);
        ingredientsList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DisplayIngredientsListInRecipeAdapter recipeAdapter = new DisplayIngredientsListInRecipeAdapter(view.getContext(), recipe);
        ingredientsList.setAdapter(recipeAdapter);


        //fill instructions text view
        TextView instructions = view.findViewById(R.id.recipeInstructions);
        instructions.setText(recipe.getRecipeInstructions());



        return view;
    }

    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(DisplayRecipeFragment.this).commit();
    }

    private void toggleStar(){
        if(recipe.isOnWeeksMenu()){
            starButton.setImageResource(R.drawable.round_star_black_18dp);
        }
        else {
            starButton.setImageResource(R.drawable.round_star_border_black_18dp);
        }

    }








}
