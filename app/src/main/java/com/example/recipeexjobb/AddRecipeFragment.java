package com.example.recipeexjobb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddRecipeFragment extends Fragment {

    //Button variables
    ImageButton saveRecipeButton;
    ImageButton cancelButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_add_recipie, container, false);

        //instantiate button variables
        saveRecipeButton = view.findViewById(R.id.saveRecipe);
        cancelButton = view.findViewById(R.id.exitAddRecipe);


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

                //logic to save recipes in main activity goes here

                closeFragment();
            }
        });

        return view;
    }

    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(AddRecipeFragment.this).commit();
    }



}
