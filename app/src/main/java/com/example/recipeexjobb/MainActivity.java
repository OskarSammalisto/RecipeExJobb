package com.example.recipeexjobb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class MainActivity extends AppCompatActivity {

    //sending notification
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" +BuildConfig.cmServerKey;
    final private String contentType = "application/JSON";
    final String TAG = "NOTIFICATION TAG";


    //All fireBase instances
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseFirestore fireStoreDb;

    //lists of friend and requests
    private List<Map> friendRequestsList = new ArrayList<>();
    private List<Map> friendsList = new ArrayList<>();
    private List<Recipe> sharedRecipesList = new ArrayList<>();
    boolean unsavedSharedRecipes;

    //share recipes and friend req ref and listeners
    CollectionReference sharedRecipeRef;
    private ListenerRegistration shareRecipeListener;
    private ListenerRegistration friendReqListener;


    //Adapter for fragment pager adapter
    FragmentPagerAdapter adapterViewPager;

    List<CategoryFragment> fragmentList = new ArrayList<>();
   // int viewPagerPosition;


    //Main Recipe List
    private List<Recipe> recipeList;
   // public List<RecipeListAdapter> recipeListAdapters = new ArrayList<>();
    public List<CategoryFragment> fragments = new ArrayList<>();

    //recipe that should be displayed in recipe fragment
    Recipe selectedRecipe;

    //viewpager to change page in code
    ViewPager viewPager;


    //event listener to refresh recipe list
    EventListener eventListener;

    //add recipe menu button for enabling and disabling
    MenuItem newRecipeButton;

    public SearchView searchView;

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


        //register to cloud messaging topic
        FirebaseMessaging.getInstance().subscribeToTopic(formatEmailForSub(mAuth.getCurrentUser().getEmail()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribed";
                        if(!task.isSuccessful()){
                            msg = "unsuccessful";
                        }
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

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
                                                redrawList();


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
                        Log.d("load from firestore", "Error getting documents: ", task.getException());
                    }
                }
            });



        }
        else {
            checkRecipeListForNewRecipes();
        }

        ///check for friend requests
        updateFriendRequestList();

        updateFriendsList();

        checkForSharedRecipes();

        //sets listeners for firestore database updates
       setFirestoreListeners();

        //set up the app toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        if(user.getDisplayName() != null){
            setTitle(user.getDisplayName());
        }




        //Sets pager adapter to main activity view pager
        viewPager = findViewById(R.id.viewPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapterViewPager);
        //refreshRecipeListAdapter();


    }

    private void setFirestoreListeners(){

        //listen for changes in shared recipes
        sharedRecipeRef = fireStoreDb.collection("users")
                .document(mAuth.getUid()).collection("sharedRecipes");
        shareRecipeListener = sharedRecipeRef.addSnapshotListener(MainActivity.this, new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() > sharedRecipesList.size()
                        || sharedRecipesList.size() == 0 && queryDocumentSnapshots != null   &&  queryDocumentSnapshots.size() > 0){

                    checkForSharedRecipes();
                    Toast.makeText(MainActivity.this, "New recipes shared", Toast.LENGTH_LONG).show();
                }

            }
        });


        //listen for friend requests
        CollectionReference friendRequestRef = fireStoreDb.collection("friends")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("friendRequests");
        friendReqListener = friendRequestRef.addSnapshotListener(MainActivity.this, new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() > friendRequestsList.size()
                        || friendRequestsList.size() == 0 && queryDocumentSnapshots != null   &&  queryDocumentSnapshots.size() > 0){

                    updateFriendRequestList();
                    Toast.makeText(MainActivity.this, "New friend request", Toast.LENGTH_LONG).show();

                }


            }
        });


    }


    public void redrawList(){
        viewPager.setAdapter(adapterViewPager);
        for(CategoryFragment fragment : fragments){
            fragment.refreshList();
        }

    }

    private String formatEmailForSub(String mail){

        return  mail.replace("@", "");

    }

    public void updateFriendRequestList(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("friends")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("friendRequests")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document : task.getResult()){

                        Map request = document.getData();
                        friendRequestsList.add(request);

                    }
                }
            }
        });



    }

    public void updateFriendsList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getUid()).collection("friends");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){

                        Map request = document.getData();
                        String email = request.get("email").toString();
                        List<String> friendsEmails = new ArrayList<>();

                        for(Map friend : friendsList){
                            friendsEmails.add(friend.get("email").toString());
                        }

                        if(!friendsEmails.contains(email)){
                            friendsList.add(request);
                        }


                    }
                }
            }
        });

    }


    public void addNewFriendToList(Map<String, Object> friendRequestMap, Boolean add){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(add){

            Map<String, Object> myInfo = new HashMap<>();
            myInfo.put("username", mAuth.getCurrentUser().getDisplayName());
            myInfo.put("userID", mAuth.getUid());
            myInfo.put("email", mAuth.getCurrentUser().getEmail());

            friendsList.add(friendRequestMap);

            CollectionReference myCollRef = db.collection("users").document(mAuth.getUid()).collection("friends");
            myCollRef.document(friendRequestMap.get("userID").toString()).set(friendRequestMap);
            //db.collection("users").document(mAuth.getUid()).collection("friends").add(friendRequestMap);


            CollectionReference friendCollRef = db.collection("users").document(friendRequestMap.get("userID").toString()).collection("friends");
            friendCollRef.document(mAuth.getUid()).set(myInfo);
            //db.collection("users").document(friendRequestMap.get("userID").toString()).collection("friends").add(myInfo);


        }


        db.collection("friends")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("friendRequests")
                .document(friendRequestMap
                        .get("email").toString() +" + " +mAuth.getCurrentUser().getEmail()).delete();


    }

    public List<Map> getFriendRequestsList(){
        return friendRequestsList;
    }

    public List<Map> getFriendsList(){
        return friendsList;
    }



    public void addRemoveFromFav(Recipe recipe){
        for(CategoryFragment fragment : fragments){
          fragment.addRemoveFav(recipe);
        }
    }

    public void removeRecipe(Recipe recipe){

        for(CategoryFragment fragment : fragments){
            fragment.removeRecipe(recipe);
        }


    }

    public void displayNewRecipe(Recipe recipe){
        for(CategoryFragment fragment : fragments){
           fragment.displayNewRecipe(recipe);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        newRecipeButton = menu.findItem(R.id.newRecipe);
        newRecipeButton.setVisible(true);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for(CategoryFragment fragment: fragments ){

                    fragment.recipeListAdapter.getFilter().filter(query);
                }



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for(CategoryFragment fragment: fragments ){

                    fragment.recipeListAdapter.getFilter().filter(newText);
                }

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newRecipe:
                openCreateRecipeFragment();
                return true;
            case R.id.signOut:


                //unregister from firestore listeners
                friendReqListener.remove();
                shareRecipeListener.remove();


                FirebaseMessaging.getInstance().unsubscribeFromTopic(formatEmailForSub(mAuth.getCurrentUser().getEmail()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "unsubbed";
                                if (!task.isSuccessful()) {
                                    msg = "unsub failed";
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                FirebaseAuth.getInstance().signOut();
                intentLoginScreen();
                return true;


            case R.id.action_search:
                return true;

            case R.id.friendsList:
                openFriendsList();
                return true;

            case R.id.addFriend:
                addFriend();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openCreateRecipeFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutForRecipes, new AddRecipeFragment());
        ft.commit();
        newRecipeButton.setVisible(false);

    }

    private void openFriendsList(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutForRecipes, new FriendsListFragment());
        ft.commit();
    }

    private void addFriend(){

        AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(MainActivity.this);

        addFriendDialog.setTitle("Please enter friends email address.");

        final EditText emailEditText = new EditText(MainActivity.this);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        addFriendDialog.setView(emailEditText);


        addFriendDialog.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String email = emailEditText.getText().toString();

                //check if email exists in database
                boolean validEmail = isValidEmail(email);

                if(validEmail){


                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        //check if user typed in a valid email address
                        boolean emailExists = !task.getResult().getSignInMethods().isEmpty();

                        if(email.equals(mAuth.getCurrentUser().getEmail())){
                            Toast.makeText(MainActivity.this
                                    , "I assume you're already friend with yourself."
                                    , Toast.LENGTH_SHORT).show();
                        }

                        else if(emailExists){

                            //TODO: check if users are already friends.
                            //send friend request in firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference collRef = db.collection("friends")
                                    .document(email).collection("friendRequests");

                            Map<String, Object> friendRequest = new HashMap<>();
                            friendRequest.put("username", mAuth.getCurrentUser().getDisplayName());
                            friendRequest.put("userID", mAuth.getUid());
                            friendRequest.put("email", mAuth.getCurrentUser().getEmail());

                            collRef.document(mAuth.getCurrentUser().getEmail() + " + " +email).set(friendRequest);

                            sendNotification(email);


                            Toast.makeText(MainActivity.this
                                    , "Friend Request Sent."
                                    , Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(MainActivity.this
                                    , "Return to sender, address unknown"
                                    , Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this
                                , "Something went wrong, please try again."
                                , Toast.LENGTH_SHORT).show();
                    }
                });

                }

                else {
                    Toast.makeText(MainActivity.this
                            , "invalid email entered."
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });

        addFriendDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addFriendDialog.show();

    }

    public void sendNotification(String email){
        Log.d("Sending", "1");
       String TOPIC = "/topics/" +formatEmailForSub(email);
       String NOTIFICATION_TITLE = "title";
       String NOTIFICATION_MESSAGE = "message";

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try{
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        }catch (JSONException e) {
            Log.e(TAG, "OnCreate: " +e.getMessage());
        }

        sendNote(notification);
    }

    private void sendNote(JSONObject notification){
        Log.d("Sending", "2");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Request Error", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onErrorResponse: Didn't work.");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
                                    if(!ids.contains(recipe.getRecipestorageID())){  //changed storage id here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

    private void checkForSharedRecipes(){



        CollectionReference reference = fireStoreDb.collection("users")
                .document(mAuth.getUid()).collection("sharedRecipes");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    Log.d("Add shared", "read");
                    List<String> ids = new ArrayList<>();
                    for(Recipe recipe1 : sharedRecipesList){
                        ids.add(recipe1.getRecipestorageID());
                    }

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        final Recipe recipe = documentSnapshot.toObject(Recipe.class);


                        if(!ids.contains(recipe.getRecipestorageID())){  //changed storage id here !!!!!!!!!!!!!!!!!!

                        sharedRecipesList.add(recipe);
                        Log.d("Add shared", "added");

                        File image = new File(Uri.parse(recipe.getImageUri()).getPath());

                        if(!image.exists()){

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("check shared rec", "failed");
            }
        });

        toggleUnsavedSharedRecipes();

    }

    public void toggleUnsavedSharedRecipes(){
        unsavedSharedRecipes = sharedRecipesList.size() > 0;

        List<String> friendNames = new ArrayList<>();

        for(Recipe recipe : sharedRecipesList){
            friendNames.add(recipe.getSharedBy());
        }

        for(Map friend : friendsList){
            if(friendNames.contains(friend.get("username").toString())){
                friend.put("sharing", "yes");
            }
            else {
                friend.put("sharing", "no");
            }

        }

    }

    public void saveSharedRecipes(String friendsName){



        for(Recipe recipe : sharedRecipesList){
            if(friendsName.equals(recipe.getSharedBy())){

                recipeList.add(recipe);
                uploadRecipe(recipe, recipe.getRecipestorageID());

            }
            sharedRecipesList.remove(recipe);  //todo get this to work
        }

        deleteSharedRecipes(friendsName);

    }

    public void deleteSharedRecipes(final String friendsName){

        CollectionReference reference = fireStoreDb.collection("users")
                .document(mAuth.getUid())
                .collection("sharedRecipes");

        Query sharedByUser = reference.whereEqualTo("sharedBy", friendsName);

                sharedByUser.whereEqualTo("sharedBy", friendsName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){

                        String imageId = document.get("recipestorageID").toString();
                        deleteCloudImage(imageId);

                        document.getReference().delete();
                    }
//                    for(Recipe recipe : sharedRecipesList){
//                        if(friendsName.equals(recipe.getSharedBy())){
//
//                            sharedRecipesList.remove(recipe);
//
//                        }
//                    }


                }
                for(Recipe recipe : sharedRecipesList){
                        if(friendsName.equals(recipe.getSharedBy())){

                            sharedRecipesList.remove(recipe);

                        }
                    }
            }
        });

        //reference.document(recipe.getRecipestorageID()).delete(); //



//        CollectionReference reference = db.collection("users")
//                .document(friend.get("userID").toString())
//                .collection("sharedRecipes");
//
//        reference.document(recipe.getRecipestorageID()).set(recipe);


    }



    public boolean unsavedRecipesStatus(){
        return unsavedSharedRecipes;
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


        File image = new File(Uri.parse(recipe.getImageUri()).getPath());
        boolean delete = image.delete();
        Log.d("delete file", "file deletion: " +delete);


        removeRecipe(recipe);

        recipeList.remove(recipe);

        //redrawList();


    }


    //Create a new recipe and add to Recipe List
    public void createRecipe(String imageLocalUri, String title, String description, String instructions,
                             int category, List<IngredientItem> ingredientItemList, String  ovenHeat, String prepTime, String cookTime){



        Recipe recipe = new Recipe(imageLocalUri, title, description, instructions, category, ingredientItemList, ovenHeat, prepTime, cookTime);
        recipeList.add(recipe);
       // redrawList();

        displayNewRecipe(recipe);


        String storageID = recipe.generateStorageId();   //changed storage id here !!!!!!!!!!!!!!!!!!
        recipe.setRecipestorageID(storageID);

        uploadRecipe(recipe, storageID);


        //eventListener.refreshList();


        Toast.makeText(MainActivity.this, "Recipe added to collection.", Toast.LENGTH_SHORT).show();
    }

    public void uploadRecipe(Recipe recipe, String storageID){



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getUid()).collection("Recipes");
        collectionReference.document(storageID).set(recipe).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("upload fail", "exeption: " + e);
            }
        });
        //   myRef.child("users").child(mAuth.getUid()).child("recipes").setValue(recipeList);

        uploadImage(Uri.parse(recipe.getImageUri()), recipe);



    }

    public List<Recipe> getRecipeList(){
        return recipeList;
    }

    public List<Recipe> getSharedRecipeList(){
        return sharedRecipesList;
    }

    private void intentLoginScreen(){


        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutForRecipes);
            if(myFragment != null){

                getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
                newRecipeButton.setVisible(true);

            }
    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        //unregister from firestore listeners
        friendReqListener.remove();
        shareRecipeListener.remove();

    }

    @Override
    public void onPause() {

        super.onPause();

        //unregister from firestore listeners
        friendReqListener.remove();
        shareRecipeListener.remove();

    }

    @Override
    public void onResume() {


        super.onResume();
    }


    //pager adapter class
    public static class PagerAdapter extends FragmentPagerAdapter {
            private static int NUM_ITEMS = 3;
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
                    return CategoryFragment.newInstance(0, "Recipes");
                case 1:
                    return CategoryFragment.newInstance(1, "Weeks Menu");
                case 2:
                    return CategoryFragment.newInstance(2, "Favorites");
//                case 3:
//                    return CategoryFragment.newInstance(3, "Dessert");
//                case 4:
//                    return CategoryFragment.newInstance(4, "Drink");
//                case 5:
//                    return CategoryFragment.newInstance(5, "Other");
//                case 6:
//                    return CategoryFragment.newInstance(6, "Weeks menu");
//                case 7:
//                    return CategoryFragment.newInstance(7, "All Recipes");


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

    private void deleteCloudImage(String id){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stRef = storage.getReference().child("images/" +mAuth.getUid() +"/" +id + ".jpg");

        stRef.delete();
    }


}
