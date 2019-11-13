package com.example.recipeexjobb;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> implements Filterable {
    private List<Recipe> recipeList;
    private Context context;
    private EventListener listener;
    private int page;
    private List<Recipe> filteredList = new ArrayList<>();
    private List<Recipe> searchFilteredList;
    private TypedArray categoryIcons;
    private String[] categories;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                String[] searchArray = charString.split(" ");
                if(charString.isEmpty() || charString.length() < 2){
                    searchFilteredList = filteredList;
                } else{
                    final List<Recipe> tempSearchList = new ArrayList<>();

                    for(Recipe recipe : filteredList) {

                        int searchStringCount = searchArray.length;
                        int searchHits = 0;



                        for (String searchString : searchArray) {

                            //see if search matches recipe name
                            if(recipe.getRecipeTitle().toLowerCase().contains(searchString)){

                                    searchHits++;
                                    continue;


                            }

                           if(categories[recipe.getRecipeCategory()].toLowerCase().contains(searchString)){
                               searchHits++;
                               continue;
                           }

                            //see if search matches any recipe ingredient
                            for(IngredientItem ingredientItem : recipe.getIngredientsList()){


                                    if (ingredientItem.getIngredient().toLowerCase().contains(searchString.toLowerCase())) {
                                        searchHits ++;
//
                                    }

                            }

                        }

                        //see if recipe gets a hit on all search words
                        if(searchStringCount == searchHits){
                            if(!tempSearchList.contains(recipe)){
                                tempSearchList.add(recipe);
                            }
                        }

                    }


                    searchFilteredList = tempSearchList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchFilteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                searchFilteredList = (ArrayList<Recipe>) results.values;
                notifyDataSetChanged();


            }
        };
    }

    public interface EventListener{
        void openRecipe(Recipe recipe);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView recipeImage, categoryIcon;
        TextView recipeTitle, recipeDescription;
        public MyViewHolder(View view){
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.openRecipe(searchFilteredList.get(getAdapterPosition()));

                }
            });

            recipeImage = view.findViewById(R.id.recipeImage);
            categoryIcon = view.findViewById(R.id.categoryIcon);
            recipeTitle = view.findViewById(R.id.recipeTitle);
            recipeDescription = view.findViewById(R.id.recipeDescription);
        }
    }



    public RecipeListAdapter(Context context, List<Recipe> recipes,int page, EventListener listener){

        this.recipeList = recipes;
        this.context = context;
        this.listener = listener;
        this.page = page;

        categoryIcons = context.getResources().obtainTypedArray(R.array.categoryIcons);
        categories = context.getResources().getStringArray(R.array.menuCategoryList);

        if(page == 0){
            filteredList = recipes;
        }

        else {
            for(Recipe recipe : recipes){

                if(page == 1){

                    if(recipe.isOnWeeksMenu()){
                        if(!filteredList.contains(recipe)){
                            filteredList.add(recipe);
                        }
                    }
                    else{
                        filteredList.remove(recipe);
                    }

                }
                else if(page == 2){
                    if(recipe.isFavorite()){
                        if(!filteredList.contains(recipe)){
                            filteredList.add(recipe);
                        }
                    }
                    else{
                        filteredList.remove(recipe);
                    }

                }
            }
        }

        searchFilteredList = filteredList;



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



        Recipe recipe = searchFilteredList.get(position);


        holder.recipeTitle.setText(recipe.getRecipeTitle());
        holder.recipeDescription.setText(recipe.getRecipeDescription());
        holder.categoryIcon.setImageDrawable(categoryIcons.getDrawable(recipe.getRecipeCategory()));



        try{
            holder.recipeImage.setImageURI(Uri.parse(filteredList.get(position).getImageUri()));    //Drawable(context.getResources().getDrawable(recipe.getImage()));

        }
        catch (Exception e){
            //TODO: set some other placeholder image instead.
        }


    }

    @Override
    public int getItemCount() {
        return searchFilteredList.size();
    }


}
