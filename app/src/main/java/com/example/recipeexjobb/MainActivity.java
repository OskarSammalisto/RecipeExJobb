package com.example.recipeexjobb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //All fireBase instances
    private FirebaseAuth mAuth;

    //Adapter for fragment pager adapter
    FragmentPagerAdapter adapterViewPager;

    //button variables
    private ImageButton addRecipeButton;
    private ImageButton menuButton;

    //Main Recipe List
    private List<Recipe> recipeList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiates fireBase auth
        mAuth = FirebaseAuth.getInstance();

        //instantiate recipe list if null
        if(recipeList == null){
            recipeList = new ArrayList<>();
        }

        //instantiate button variables
        addRecipeButton = findViewById(R.id.addRecipeButton);
        menuButton = findViewById(R.id.menuButton);


        //On click listener for add recipe button
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayoutForRecipes, new AddRecipeFragment());
                ft.commit();

            }
        });

        //on click listener for menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "this would open the side menu", Toast.LENGTH_SHORT).show();
            }
        });



        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            //go to login screen
            intentLoginScreen();
        }


        //Sets pager adapter to main activity view pager
        ViewPager viewPager = findViewById(R.id.viewPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);


    }


    //Create a new recipe and add to Recipe List
    public void createRecipe(String title, String description, String ingredients, String instructions){
        Recipe recipe = new Recipe(R.drawable.knight_sprite, title, description, ingredients, instructions);
        recipeList.add(recipe);


        Toast.makeText(MainActivity.this, "Recipe added to collection.", Toast.LENGTH_SHORT).show();
    }

    public List<Recipe> getRecipeList(){
        return recipeList;
    }

    private void intentLoginScreen(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        FirebaseAuth.getInstance().signOut();
        intentLoginScreen();
    }


    //pager adapter class
    public static class PagerAdapter extends FragmentPagerAdapter {
            private static int NUM_ITEMS = 3;


        public PagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        }


        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryFragment.newInstance(0, "page # 1");
                case 1:
                    return CategoryFragment.newInstance(1, "page # 2");
                case 2:
                    return CategoryFragment.newInstance(2, "page # 3");
                    default:
                        return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "page " + position;
        }
    }

}
