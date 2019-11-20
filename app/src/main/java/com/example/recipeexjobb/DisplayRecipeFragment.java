package com.example.recipeexjobb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DisplayRecipeFragment extends Fragment {

    private Recipe recipe;
    private ImageButton starButton;
    private ImageButton weekButton;
    private ImageView toggleWeekCheck;
    private DisplayIngredientsListInRecipeAdapter recipeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.fragment_display_recipe, container, false);


        recipe = ((MainActivity) getActivity()).getCurrentRecipe();

        ScrollView backgroundView = view.findViewById(R.id.displayRecipeScrollView);
        int[] backgroundColors = getActivity().getResources().getIntArray(R.array.categoryColors);

        backgroundView.setBackgroundColor(backgroundColors[recipe.getRecipeCategory()]);

        //exit button
        ImageButton exitButton = view.findViewById(R.id.exitRecipe);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        starButton = view.findViewById(R.id.addToFavorites);
        toggleStar();

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setFavorite(!recipe.isFavorite());
                toggleStar();
                //((MainActivity) getActivity()).redrawList();
                ((MainActivity) getActivity()).addRemoveFromFav(recipe);


            }
        });

        weekButton = view.findViewById(R.id.addToWeeksMenu);
        toggleWeekCheck = view.findViewById(R.id.weekCheck);
        toggleWeek();

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setOnWeeksMenu(!recipe.isOnWeeksMenu());
                toggleWeek();
                //((MainActivity) getActivity()).redrawList();
                ((MainActivity) getActivity()).addRemoveFromFav(recipe);
            }
        });







        //delete recipe
        ImageButton deleteButton = view.findViewById(R.id.deleteRecipe);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonPressed();

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
//        TextView description = view.findViewById(R.id.recipeDescription);
//        description.setText(recipe.getRecipeDescription());

        //fill recycle view with ingredients
        RecyclerView ingredientsList = view.findViewById(R.id.ingredientsListRecycleView);
        ingredientsList.setHasFixedSize(true);
        ingredientsList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recipeAdapter = new DisplayIngredientsListInRecipeAdapter(view.getContext(), recipe);
        ingredientsList.setAdapter(recipeAdapter);

        CardView ingredientsCard = view.findViewById(R.id.ingredientsCardView);
        ingredientsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustIngredientsList();
            }
        });



        //fill instructions text view
        TextView instructions = view.findViewById(R.id.recipeInstructions);
        instructions.setText(recipe.getRecipeInstructions());



        return view;
    }

    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(DisplayRecipeFragment.this).commit();
    }

    private void toggleStar(){
        if(recipe.isFavorite()){
            starButton.setImageResource(R.drawable.round_star_black_18dp);

        }
        else {
            starButton.setImageResource(R.drawable.round_star_border_black_18dp);

        }

    }

    private void deleteButtonPressed(){
        AlertDialog.Builder deleteRecipeAlert = new AlertDialog.Builder(getContext());

        deleteRecipeAlert.setTitle("Are You Sure You Want To Delete This Recipe?");


        deleteRecipeAlert.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               deleteRecipe();

            }
        });

        deleteRecipeAlert.setNegativeButton("No!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        deleteRecipeAlert.show();
    }

    private void deleteRecipe(){
        ((MainActivity) getActivity()).deleteRecipe(recipe);
        closeFragment();
        Toast.makeText(getContext(), "Recipe Deleted", Toast.LENGTH_SHORT).show();

    }

    private void toggleWeek(){

       if(recipe.isOnWeeksMenu()){
           toggleWeekCheck.setVisibility(View.VISIBLE);

       }
       else {
           toggleWeekCheck.setVisibility(View.INVISIBLE);

       }


    }

    private void adjustIngredientsList(){

        AlertDialog.Builder adjustIngredientsAlert = new AlertDialog.Builder(getContext());

        adjustIngredientsAlert.setTitle("What would you like to do?");

        final EditText multiplier = new EditText(getContext());
        multiplier.setInputType(InputType.TYPE_CLASS_NUMBER);
        adjustIngredientsAlert.setView(multiplier);

        adjustIngredientsAlert.setPositiveButton("Multiply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mp = Integer.parseInt(multiplier.getText().toString());

                recipeAdapter.multiplyRecipe(mp);
                //multiplyRecipe(mp);
            }
        });

        adjustIngredientsAlert.setNeutralButton("Change Units", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeUnits();
            }
        });

        adjustIngredientsAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adjustIngredientsAlert.show();

    }

//    private void multiplyRecipe(double multiplier){
//
//
//        List<IngredientItem> tempIngredientList = recipe.getIngredientsList();
//
//        for (IngredientItem ingredientItem : tempIngredientList){
//            ingredientItem.setAmount(ingredientItem.getAmount() * multiplier);
//        }
//
//        recipeAdapter.updateIngredientsList(tempIngredientList);
//
//    }

    private void changeUnits(){



    }




}
