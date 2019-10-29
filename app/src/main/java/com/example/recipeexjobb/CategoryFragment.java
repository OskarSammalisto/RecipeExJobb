package com.example.recipeexjobb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CategoryFragment extends Fragment {

    //name and number of tab
    private int page;
    private String category;

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
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        TextView categoryLabel = view.findViewById(R.id.categoryLabel);
        categoryLabel.setText(category);
        return view;
    }
}
