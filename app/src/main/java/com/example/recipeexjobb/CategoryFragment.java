package com.example.recipeexjobb;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    //name and number of tab
    private int page;
    private String category;
    private RecyclerView recyclerView;
    private RecipeListAdapter recipeListAdapter;

    List<Recipe> recipeList;
    List<Recipe> categorizedList;

    public CategoryFragment(){
        //empty public constructor
    }


    //Constructor for creating a fragment
    public static CategoryFragment newInstance(int page, String category) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someString", category);
        categoryFragment.setArguments(args);
        return  categoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        category = getArguments().getString("someString");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //gets the recipe list from main activity
        recipeList = ((MainActivity) getActivity()).getRecipeList();




        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recipeRecycleView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        categorizedList = new ArrayList<>();


        //makes a list for the weeks recipes
        if(page == 6){
            for(Recipe recipe : recipeList){
                if(recipe.isOnWeeksMenu()){
                    categorizedList.add(recipe);
                }
            }
        }

        else if(page == 7){
            categorizedList = recipeList;
        }

        //makes a list of recipes fitting the category
        for(Recipe recipe : recipeList){
            if(recipe.getRecipeCategory() == page){
                categorizedList.add(recipe);
            }
        }


        recipeListAdapter = new RecipeListAdapter(view.getContext(), categorizedList);
        recyclerView.setAdapter(recipeListAdapter);

        return view;
    }

}
