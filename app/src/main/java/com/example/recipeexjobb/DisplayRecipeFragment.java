package com.example.recipeexjobb;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayRecipeFragment extends Fragment {

    Recipe recipe;

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

        //set recipe image
        ImageView image = view.findViewById(R.id.recipeImageView);
        try{
            image.setImageURI(Uri.parse(recipe.getImageUri()));
        }
        catch (Exception e){
            //TODO: set some other placeholder image instead.
        }




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








}
