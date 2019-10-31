package com.example.recipeexjobb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddRecipeFragment extends Fragment {

    //Button variables
    ImageButton saveRecipeButton;
    ImageButton cancelButton;

    //Edit text variables
    EditText recipeTitle;
    EditText recipeDescription;
    EditText recipeIngredients;
    EditText recipeinstructions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_add_recipie, container, false);

        //instantiate button variables and edit text
        saveRecipeButton = view.findViewById(R.id.saveRecipe);
        cancelButton = view.findViewById(R.id.exitAddRecipe);

        recipeTitle = view.findViewById(R.id.addRecipeTitle);
        recipeDescription = view.findViewById(R.id.addRecipeDescription);
        recipeIngredients = view.findViewById(R.id.addRecipeIngredients);
        recipeinstructions = view.findViewById(R.id.addRecipeInstructions);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Closes the fragment without saving
                closeFragment();
            }
        });

        saveRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save recipes in main activity recipe list

                String title = recipeTitle.getText().toString();
                String description = recipeDescription.getText().toString();
                String ingredients = recipeIngredients.getText().toString();
                String instructions = recipeinstructions.getText().toString();



                ((MainActivity) getActivity()).createRecipe(title, description, ingredients, instructions);

                closeFragment();
            }
        });

        return view;
    }

    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(AddRecipeFragment.this).commit();
    }



}
