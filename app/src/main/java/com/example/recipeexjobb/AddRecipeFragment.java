package com.example.recipeexjobb;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.Double.parseDouble;

public class AddRecipeFragment extends Fragment implements AddIngredientAdapter.EventListener {


    //This fragment is where the user adds new recipes. the recipes can be added by simply writing in the text manually or
    //by taking a photo of the text and filling the appropriate text box automatically.


    //text View variables
    private TextView recipeTitle;
    private TextView recipeDescription;
   // private TextView recipeIngredients;
    private TextView recipeInstructions;

    //oven and time settings
    private ImageButton ovenHeat;
    private TextView ovenHeatTV;

    private ImageButton prepTime;
    private TextView prepTimeTV;

    private ImageButton cookTime;
    private TextView cookTimeTV;

    //ArrayList for ingredients in new recipe
    List<IngredientItem> ingredientsList = new ArrayList<>();


    //recipe list adapter and recycle view to hold ingredients list
    private AddIngredientAdapter addIngredientAdapter;
    private RecyclerView recyclerView;
    private int numberOfIngredients = 1;


    //image view for recipe image
    private ImageView imView;


    //uri string for image storage location
    private String imageStorageUri;

    //Current text Box that shall receive text from image to text api or from edit text
    private TextView imageToTextEditText;

    //File to save photo taken with camera
    private File photoFile;
    private File imageFile;
    private boolean takingPicture;

    //Constant used for the camera intent
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA = 1;


    //String to create photo path for new photos
    private String currentPhotoPath;


    //booleans for identifying first change in text views, so that sample text is removed.
    private boolean titleChanged, descriptionChanged, ingredientsChanged, instructionsChanged  = false;

    //Spinner for selecting recipe category
    private Spinner categorySpinner;

    //boolean to determine whether text is analyzed or returned as plain text
    private boolean imageToList = false;
    private boolean imageToImageView = false;

    //Strings for camera popup menu
    private String[] popupMenuStrings = new String[]{"Add Description", "Add Ingredients", "Add Instructions"};

    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.fragment_add_recipie, container, false);



        //init spinner and set categories
        categorySpinner = view.findViewById(R.id.categorySpinner);
        fillCategorySpinner();



        context = getContext();



        //instantiate recipe image variable
        imView = view.findViewById(R.id.addRecipeImage);
        imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageToImageView = true;
                takingPicture = true;
                dispatchTakePictureIntent();


            }
        });

        //instantiate button variables, text views and edit text
        ImageButton saveRecipeButton = view.findViewById(R.id.saveRecipe);
        final ImageButton cancelButton = view.findViewById(R.id.exitAddRecipe);
        // ingredientsFromImage = view.findViewById(R.id.cameraAddIngredients);
        // private ImageButton ingredientsFromImage;
//        ImageButton instructionsFromImage = view.findViewById(R.id.cameraAddInstructions);
//        ImageButton ingredientsListFromImage = view.findViewById(R.id.cameraAddIngredientsAsList);
//        ImageButton descriptionFromImage = view.findViewById(R.id.cameraAddRecipeDescription);

        recipeTitle = view.findViewById(R.id.addRecipeTitle);
        recipeDescription = view.findViewById(R.id.addRecipeDescription);
       // recipeIngredients = view.findViewById(R.id.addRecipeIngredients);
        recipeInstructions = view.findViewById(R.id.addRecipeInstructions);


        //one camera button to rule all camera buttons, one button to bind them to a single menu.
        final ImageButton imageToTextButton = view.findViewById(R.id.cameraForAll);

        imageToTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getContext(), imageToTextButton);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                for (String string : popupMenuStrings){
                    popupMenu.getMenu().add(string);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getTitle().toString()){

                            case "Add Description":

                                if(!descriptionChanged){
                                    recipeDescription.setText("");
                                    descriptionChanged = true;
                                }

                                takingPicture = false;
                                imageToTextEditText = recipeDescription;
                                dispatchTakePictureIntent();

                                return true;

                            case "Add Ingredients":

                                imageToList = true;
                                takingPicture = false;
                                dispatchTakePictureIntent();

                                return true;

                            case "Add Instructions":

                                //removes hardcoded text from text view
                                if(!instructionsChanged){
                                    recipeInstructions.setText("");
                                    instructionsChanged = true;
                                }

                                imageToTextEditText = recipeInstructions;
                                takingPicture = false;
                                dispatchTakePictureIntent();

                                return true;

                            default:

                                return false;



                        }

                    }
                });
                menuInflater.inflate(R.menu.camera_menu, popupMenu.getMenu());
                popupMenu.show();


            }
        });



        //Button to cancel adding a new recipe
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(photoFile != null){
                    photoFile.delete();
                }

                //Closes the fragment without saving
                closeFragment();
            }
        });

        //Button to save the recipe and return to main screen.
        saveRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageStorageUri == null){
                    Toast.makeText(getContext(), "Don't Forget the Picture!", Toast.LENGTH_SHORT).show();
                }
                else {

                    //Save recipes in main activity recipe list

                    String title = recipeTitle.getText().toString();
                    String description = recipeDescription.getText().toString();
                    // String ingredients = recipeIngredients.getText().toString();
                    String instructions = recipeInstructions.getText().toString();
                    String ovenHeat = ovenHeatTV.getText().toString();
                    String prepTime = prepTimeTV.getText().toString();
                    String cookTime = cookTimeTV.getText().toString();

                    int category = categorySpinner.getSelectedItemPosition() -1;



                    ((MainActivity) getActivity()).createRecipe(imageStorageUri, title, description, instructions, category, ingredientsList, ovenHeat, prepTime, cookTime);


                    closeFragment();


                }



            }
        });


        //Click listeners to edit text in text views, opens up an edit text alert dialog.

        //oven heat setting
        ovenHeat = view.findViewById(R.id.setOvenHeat);
        ovenHeatTV = view.findViewById(R.id.setOvenHeatTV);

        ovenHeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewText(ovenHeatTV);
            }
        });

        //prep time settings
        prepTime = view.findViewById(R.id.setPrepTime);
        prepTimeTV = view.findViewById(R.id.prepTimeTV);

        prepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewText(prepTimeTV);
            }
        });

        //cook time settings
        cookTime = view.findViewById(R.id.setCookTime);
        cookTimeTV = view.findViewById(R.id.cookTimeTV);

        cookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewText(cookTimeTV);
            }
        });


        //Title View
        recipeTitle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!titleChanged){
                    recipeTitle.setText("");
                    titleChanged = true;
                }

                setViewText(recipeTitle);

            }
        });

        //Description View
        recipeDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!descriptionChanged){
                    recipeDescription.setText("");
                    descriptionChanged = true;
                }
                setViewText(recipeDescription);
            }
        });

        //Ingredients view
//        recipeIngredients.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //removes hardcoded text from text view
//                if(!ingredientsChanged){
//                    recipeIngredients.setText("");
//                    ingredientsChanged = true;
//                }
//                setViewText(recipeIngredients);
//            }
//        });

        //Instructions View
        recipeInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!instructionsChanged){
                    recipeInstructions.setText("");
                    instructionsChanged = true;
                }

                setViewText(recipeInstructions);
            }
        });



        //Button to read ingredients from an image taken with the camera
//        ingredientsFromImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                //removes hardcoded text from text view
//                if(!ingredientsChanged){
//                    recipeIngredients.setText("");
//                    ingredientsChanged = true;
//                }
//
//                //open camera and do stuff with image
//                imageToTextEditText = recipeIngredients;
//                dispatchTakePictureIntent();
//            }
//        });


        //Camera buttons have been replaced with one button and a popup menu!

//        descriptionFromImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!descriptionChanged){
//                    recipeDescription.setText("");
//                    descriptionChanged = true;
//                }
//
//                takingPicture = false;
//                imageToTextEditText = recipeDescription;
//                dispatchTakePictureIntent();
//            }
//        });
//
//        //button to read ingredients and analyze text to make a list of ingredients objects
//        ingredientsListFromImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageToList = true;
//                takingPicture = false;
//                dispatchTakePictureIntent();
//            }
//        });
//
//        //Button to read instructions from an image taken with the camera
//        instructionsFromImage.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//
//                //removes hardcoded text from text view
//                if(!instructionsChanged){
//                    recipeInstructions.setText("");
//                    instructionsChanged = true;
//                }
//
//                imageToTextEditText = recipeInstructions;
//                takingPicture = false;
//                dispatchTakePictureIntent();
//                //open camera and do stuff with image
//            }
//        });

        //set recycler view adapter for ingredients list
        //View ingredientsView = inflater.inflate(R.layout.)
        //ingredientsList.add(new IngredientItem());
        recyclerView = view.findViewById(R.id.addItemRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        addIngredientAdapter = new AddIngredientAdapter(view.getContext(), ingredientsList, this);
        recyclerView.setAdapter(addIngredientAdapter);

        Button button = view.findViewById(R.id.addIngredientButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        return view;
    }

    //increase number of ingredients
    private void addIngredient(){
            IngredientItem ingredientItem = new IngredientItem();
            ingredientsList.add(ingredientItem);
            updateAdapterList();

    }

    public void removeIngredient(int position){
        ingredientsList.remove(position);
        updateAdapterList();
    }

    private void updateAdapterList(){
        addIngredientAdapter.notifyDataSetChanged();
    }

    public void editIngredientAmount(int position,double amount){
        ingredientsList.get(position).setAmount(amount);
    }

    public void editIngredientMeasurement(int position, int unit){
        ingredientsList.get(position).setUnit(unit);
    }

    public void editIngredientName(int position, String name){
        ingredientsList.get(position).setIngredient(name);
    }



    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(AddRecipeFragment.this).commit();
        ((MainActivity) getActivity()).newRecipeButton.setVisible(true);
    }


    //initialize and populate category spinner
    private void fillCategorySpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (getContext(), R.array.categoryList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

    }


    //Open alert dialog to edit text in text views
    public void setViewText(final TextView textView){

        AlertDialog.Builder editTextPopup = new AlertDialog.Builder(getContext());

        editTextPopup.setTitle("Text Editor");

        final EditText titleInput = new EditText(getContext());
        titleInput.setText(textView.getText());
        editTextPopup.setView(titleInput);

        editTextPopup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(titleInput.getText());

            }
        });

        editTextPopup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(titleInput.getText().toString().equals("")){
                    textView.setText("Canceled...");
                }

            }
        });

        editTextPopup.show();


    }



    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            if(takingPicture){

                if(imageFile != null){
                    try {
                        photoFile.delete();
                    }catch (Exception e){
                        Log.d("11111", "delete exception: " +e);
                    }

                }


                imageFile = null;
                try {
                    imageFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (imageFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.example.recipeexjobb.provider",
                            imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }


            }

            else{
                if(photoFile != null){
                    photoFile.delete();
                }


                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.example.recipeexjobb.provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imView.setImageBitmap(imageBitmap);

            Uri uri = (takingPicture) ? Uri.fromFile(imageFile) : Uri.fromFile(photoFile);
            Bitmap bitmap;


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                int file_size = Integer.parseInt(String.valueOf(photoFile.length()/1024));
//                int bitmapSize = bitmap.getByteCount()/1024;
//
//                Log.d("sizes", "file: " + file_size + ". Bitmap: " +bitmapSize);

                    if(imageToImageView){
                        imageToImageView = false;

                        saveRecipeImage(uri);
                    }
                    else {
                        readTextInImage(bitmap);
                    }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveRecipeImage(Uri uri){

        imageFile = saveBitmapToFile(imageFile);

        imView.setImageURI(uri);
        imageStorageUri = uri.toString();

    }


    //reduces image size to avoid using up unnecessary storage space.
    private File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=35;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //reads the text in the image and
    private void readTextInImage(Bitmap bitmapImage){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapImage);


        //text recognizer for the on device api
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        //text recognizer for the cloud based api
//        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
//                .getCloudTextRecognizer();
//

        //
        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        Log.d("image processing", "success");

                        //analyze text and create ingredients objects
                        if(imageToList){
                            imageToList = false;
                            generateIngredientsList(result);
                        }

                        //set returned text as text in text box
                        else {
                            String resultText = result.getText();

                            imageToTextEditText.append(resultText);
                        }


                        photoFile.delete();  //deletes the image used for text.


                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("image processing", "failed: " + e );
                                // Task failed with an exception
                                // ...
                            }
                        });



    }


    //create ingredients as objects and add to list
    private void generateIngredientsList(FirebaseVisionText result){


        for(FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()){
                List<String> words = new ArrayList<>();
                for (FirebaseVisionText.Element element: line.getElements()){
                    String word = element.getText();
                   // Log.d("output", element.toString());
                    words.add(word);
                }
                double amount = 0;
                int unit = 0;
                String ingredient = "";

                try {

                    amount = parseDouble(words.get(0));


                    // it seems to have a hard time understanding fractions
//                    if(words.get(0).equals("¼")){
//                        amount = 0.25;
//                    }
//
//                    if(words.get(0).equals("½")){
//                        amount = 0.5;
//                    }
//
//                    if(words.get(0).equals("¾")){
//                        amount = 0.75;
//                    }

                }catch (Exception e){

                    Log.d("parse int ", "parse Unsuccessful on: " + words.get(0));
                }




                try{
                    unit = analyzeMeasurement(words.get(1));
                    ingredient = generateIngredientName(amount, unit, words);
                    IngredientItem item = new IngredientItem(amount, unit, ingredient);
                    ingredientsList.add(item);
                    addIngredientAdapter.notifyDataSetChanged();

                }
                catch (Exception e){
                    Log.d("creating ingredients", "creationg ingredient unsuccessful: " + e);
                }



            }


        }


    }

    private String generateIngredientName(double amount, int unit, List<String> words){

        StringBuilder ingredientName = new StringBuilder();

        if(amount > 0 && unit > 0){
            for(int i = 2; i < words.size() ; i++){
                ingredientName.append(words.get(i));
                ingredientName.append("\t");
            }
        }

        else if(amount > 0){
            for(int i = 1; i < words.size() ; i++){
                ingredientName.append(words.get(i));
                ingredientName.append("\t");
            }

        }

        else {
            for(int i = 0; i < words.size() ; i++){
                ingredientName.append(words.get(i));
                ingredientName.append("\t");
            }
        }


        Log.d("unit", "unit: " + unit);

        return String.valueOf(ingredientName);
    }

    private int analyzeMeasurement(String string){



//        N/A = 0
//        Kg = 1
//        hg = 2
//        g = 3
//        Litre = 4
//        dl = 5
//        cl = 6
//        ml = 7
//        tbsp = 8
//        tsp = 9
//        Pound = 10
//        Ounce = 11
//        Pint = 12
//        fl.oz = 13
//        cup = 14

        String searchWord = string.toLowerCase();
        if(searchWord.equals("litre")){
            searchWord = "liter";
        }
        if(searchWord.equals("msk")){
            searchWord = "tbsp";
        }
        if(searchWord.equals("tsk")){
            searchWord = "tsp";
        }


        if(searchWord.equals("kryddmått")){
            return 7;
        }
        String[] comparisonArray = new String[]{"void", "kg", "hg", "g", "liter", "dl", "cl", "ml", "tbsp", "tsp", "pound", "ounce", "pint", "fl.oz", "cup"};

       for(int i = 0; i < comparisonArray.length; i++){
           if(searchWord.equals(comparisonArray[i])){
               return i;
           }
       }


        return 0;
    }





}
