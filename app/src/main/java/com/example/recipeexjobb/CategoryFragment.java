package com.example.recipeexjobb;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class CategoryFragment extends Fragment implements RecipeListAdapter.EventListener, MainActivity.EventListener {

    //name and number of tab
    private int page;
    private String category;
    public RecipeListAdapter recipeListAdapter;


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
        List<Recipe> recipeList = ((MainActivity) getActivity()).getRecipeList();


        ((MainActivity) getActivity()).setEvListener(this);


        if(!((MainActivity) getActivity()).fragments.contains(this)){
            ((MainActivity) getActivity()).fragments.add(this);
        }





        final View view = inflater.inflate(R.layout.fragment_category, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recipeRecycleView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        recipeListAdapter = new RecipeListAdapter(view.getContext(), recipeList, page, this);
        recyclerView.setAdapter(recipeListAdapter);





        return view;
    }

    @Override
    public void openRecipe(Recipe recipe) {
        ((MainActivity) getActivity()).openRecipe(recipe);

    }


    public void refreshList(){

        recipeListAdapter.notifyDataSetChanged();
        Log.d("refresh", "refreshed: " +page);
    }

    public void removeRecipe(Recipe recipe){
        recipeListAdapter.removeRecipe(recipe);
    }

    public void addRemoveFav(Recipe recipe){
        recipeListAdapter.addRemoveFav(recipe);
    }

    public void displayNewRecipe(Recipe recipe){
        recipeListAdapter.displayNewRecipe(recipe);
    }



}
