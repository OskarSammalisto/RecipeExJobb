package com.example.recipeexjobb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //All fireBase instances
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseFirestore fireStoreDb;


    //Adapter for fragment pager adapter
    FragmentPagerAdapter adapterViewPager;

    //button variables
    private ImageButton addRecipeButton;
    private ImageButton menuButton;

    //Main Recipe List
    private List<Recipe> recipeList;

    //recipe that should be displayed in recipe fragment
    Recipe selectedRecipe;

    ViewPager viewPager;


    //event listener to refresh recipe list
    EventListener eventListener;

    public interface EventListener{
        void  refreshList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //instantiates fireBase auth
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        fireStoreDb = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            //go to login screen
            intentLoginScreen();
        }

        //instantiate recipe list if null
        if(recipeList == null){
            recipeList = new ArrayList<>();
        }

        //get data from fire store if recipe list is empty
        if(recipeList.isEmpty()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("users")
                    .document(mAuth.getUid()).collection("Recipes");

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            final Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipeList.add(recipe);


                                File image = new File(Uri.parse(recipe.getImageUri()).getPath());

                                Log.d("!!!1", "1");
                                if(!image.exists()){
                                    Log.d("!!!1", "2");

                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference stRef = storage.getReference().child("images/" +mAuth.getUid() +"/" +recipe.getRecipestorageID() + ".jpg");

                                    try {
                                        final File localFile = File.createTempFile("images", "jpg");

                                        stRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("image download", "success");
                                                Uri localFileUri = Uri.fromFile(localFile);
                                                String uriString = localFileUri.toString();
                                                recipe.setImageUri(uriString);



                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("image download", "unsuccessful");
                                            }
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }




                                }


                        }

                    }
                    else {
                        Log.d("load from fires tore", "Error getting documents: ", task.getException());
                    }
                }
            });



        }
        else {
            checkRecipeListForNewRecipes();
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
                FirebaseAuth.getInstance().signOut();
                intentLoginScreen();
               // Toast.makeText(MainActivity.this, "this would open the side menu", Toast.LENGTH_SHORT).show();
            }
        });






        //Sets pager adapter to main activity view pager
        viewPager = findViewById(R.id.viewPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapterViewPager);


    }

    void setEvListener(EventListener listener){
        this.eventListener = listener;
    }

    private void checkRecipeListForNewRecipes(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users")
                .document(mAuth.getUid()).collection("Recipes");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && task.getResult().size() > 0){
                        if(task.getResult().size() > recipeList.size()){
                            //for each check id and add missing
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                final Recipe recipe = documentSnapshot.toObject(Recipe.class);
                                    List<String> ids = new ArrayList<>();
                                    for(Recipe recipe1 : recipeList){
                                        ids.add(recipe1.getRecipestorageID());
                                    }
                                    if(!ids.contains(recipe.setRecipestorageID())){
                                        recipeList.add(recipe);

                                        File image = new File(Uri.parse(recipe.getImageUri()).getPath());

                                        Log.d("!!!1", "1");
                                        if(!image.exists()){
                                            Log.d("!!!1", "2");

                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference stRef = storage.getReference().child("images/" +mAuth.getUid() +"/" +recipe.getRecipestorageID() + ".jpg");

                                            try {
                                                final File localFile = File.createTempFile("images", "jpg");

                                                stRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("image download", "success");
                                                        Uri localFileUri = Uri.fromFile(localFile);
                                                        String uriString = localFileUri.toString();
                                                        recipe.setImageUri(uriString);



                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("image download", "unsuccessful");
                                                    }
                                                });

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }




                                        }


                                    }

                            }

                        }
                    }
            }
        });

    }

    public void openRecipe(Recipe recipe){ //int index

        setCurrentRecipe(recipe);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutForRecipes, new DisplayRecipeFragment());
        ft.commit();

    }

    private void setCurrentRecipe(Recipe recipe){
        selectedRecipe = recipe;
    }

    public Recipe getCurrentRecipe(){
        return selectedRecipe;
    }

    public void deleteRecipe(Recipe recipe){



        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stRef = storage.getReference().child("images/" +mAuth.getUid() +"/" +recipe.getRecipestorageID() + ".jpg");

        stRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("delete", "image deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("delete", "image not deleted");
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getUid()).collection("Recipes");
        collectionReference.document(recipe.getRecipestorageID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("delete", "recipe deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("delete", "recipe not deleted");
            }
        });

        //File(recipe.getImageUri()).delete;
//        File image = new File(recipe.getImageUri());
//        image.delete();

//        ContentResolver contentResolver = getContentResolver();
//        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ recipe.getImageUri() });

//        String dir = getFilesDir().getAbsolutePath();
//        File image = new File(dir, recipe.getImageUri());
//        boolean delete = image.delete();
//        Log.d("delete file", "file deletion: " +delete);



        //TODO: its not deleting the image from phone it seems

        recipeList.remove(recipe);
        eventListener.refreshList();
        viewPager.setCurrentItem(7); //TODO: fix and remove

    }


    //Create a new recipe and add to Recipe List
    public void createRecipe(String imageLocalUri, String title, String description, String instructions,
                             int category, List<IngredientItem> ingredientItemList, String  ovenHeat, String prepTime, String cookTime){



        Recipe recipe = new Recipe(imageLocalUri, title, description, instructions, category, ingredientItemList, ovenHeat, prepTime, cookTime);
        recipeList.add(recipe);

        eventListener.refreshList();
        viewPager.setCurrentItem(7); //TODO: fix and remove

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getUid()).collection("Recipes");
        collectionReference.document(recipe.setRecipestorageID()).set(recipe);
     //   myRef.child("users").child(mAuth.getUid()).child("recipes").setValue(recipeList);

        uploadImage(Uri.parse(recipe.getImageUri()), recipe);

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

    }



    //pager adapter class
    public static class PagerAdapter extends FragmentPagerAdapter {
            private static int NUM_ITEMS = 8;
            private Context context;
            private static String[] categoryArray;



        public PagerAdapter(FragmentManager fragmentManager, Context nContext){
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            context = nContext;
            categoryArray = context.getResources().getStringArray(R.array.menuTitleCategoryList);

        }


        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CategoryFragment.newInstance(0, "Meat");
                case 1:
                    return CategoryFragment.newInstance(1, "Fish");
                case 2:
                    return CategoryFragment.newInstance(2, "Vegetarian");
                case 3:
                    return CategoryFragment.newInstance(3, "Dessert");
                case 4:
                    return CategoryFragment.newInstance(4, "Drink");
                case 5:
                    return CategoryFragment.newInstance(5, "Other");
                case 6:
                    return CategoryFragment.newInstance(6, "Weeks menu");
                case 7:
                    return CategoryFragment.newInstance(7, "All Recipes");


                default:
                        return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoryArray[position];
        }
    }

    private void uploadImage(Uri uri, Recipe recipe){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stRef = storage.getReference().child("images/" +mAuth.getUid() +"/" +recipe.getRecipestorageID() + ".jpg");

        stRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("upload", "image Upload Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("upload", "image Upload Unsuccessful");
            }
        });


    }

}
