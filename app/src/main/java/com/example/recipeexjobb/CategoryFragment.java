package com.example.recipeexjobb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

       // recipeList = new ArrayList<>();

        recipeList = ((MainActivity) getActivity()).getRecipeList();

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recipeRecycleView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

//        recipeList.add(new Recipe(R.drawable.knight_sprite, "Recipe one", "The Description of the recipe.", "ingr", "instr"));
//        recipeList.add(new Recipe(R.drawable.knight_sprite, "Recipe two", "The Description of the recipe.", "ingr", "instr"));
//        recipeList.add(new Recipe(R.drawable.knight_sprite, "Recipe three", "The Description of the recipe.", "ingr", "instr"));
//        recipeList.add(new Recipe(R.drawable.knight_sprite, "Recipe four", "The Description of the recipe.", "ingr", "instr"));

        recipeListAdapter = new RecipeListAdapter(view.getContext(), recipeList);
        recyclerView.setAdapter(recipeListAdapter);

       // TextView categoryLabel = view.findViewById(R.id.categoryLabel);
        //categoryLabel.setText(category);
        return view;
    }
}
