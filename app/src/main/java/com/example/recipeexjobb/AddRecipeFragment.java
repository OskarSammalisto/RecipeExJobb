package com.example.recipeexjobb;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddRecipeFragment extends Fragment {


    //This fragment is where the user adds new recipes. the recipes can be added by simply writing in the text manually or
    //by taking a photo of the text and filling the appropriate text box automatically.



    //Button variables
    private ImageButton saveRecipeButton;
    private ImageButton cancelButton;
    private ImageButton ingredientsFromImage;
    private ImageButton instructionsFromImage;

    //text View variables
    private TextView recipeTitle;
    private TextView recipeDescription;
    private TextView recipeIngredients;
    private TextView recipeInstructions;
    //private TextView recipeAddTags;



    //Current text Box that shall receive text from image to text api or from edit text
    private TextView imageToTextEditText;

    //image view for recipe image
    private ImageView imView;

    //File to save photo taken with camera
    private File photoFile;

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



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_add_recipie, container, false);


        //init spinner and set categories
        categorySpinner = view.findViewById(R.id.categorySpinner);
        fillCategorySpinner();





        //instantiate recipe image variable
        imView = view.findViewById(R.id.addRecipeImage);

        //instantiate button variables, text views and edit text
        saveRecipeButton = view.findViewById(R.id.saveRecipe);
        cancelButton = view.findViewById(R.id.exitAddRecipe);
        ingredientsFromImage = view.findViewById(R.id.cameraAddIngredients);
        instructionsFromImage = view.findViewById(R.id.cameraAddInstructions);

        recipeTitle = view.findViewById(R.id.addRecipeTitle);
        recipeDescription = view.findViewById(R.id.addRecipeDescription);
        recipeIngredients = view.findViewById(R.id.addRecipeIngredients);
        recipeInstructions = view.findViewById(R.id.addRecipeInstructions);
        //recipeAddTags = view.findViewById(R.id.addRecipeTags);



        //Button to cancel adding a new recipe
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Closes the fragment without saving
                closeFragment();
            }
        });

        //Button to save the recipe and return to main screen.
        saveRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save recipes in main activity recipe list

                String title = recipeTitle.getText().toString();
                String description = recipeDescription.getText().toString();
                String ingredients = recipeIngredients.getText().toString();
                String instructions = recipeInstructions.getText().toString();

                int category = categorySpinner.getSelectedItemPosition() -1;



                ((MainActivity) getActivity()).createRecipe(title, description, ingredients, instructions, category);


                closeFragment();
            }
        });


        //Click listeners to edit text in text views, opens up an edit text alert dialog.

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
        recipeIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!ingredientsChanged){
                    recipeIngredients.setText("");
                    ingredientsChanged = true;
                }
                setViewText(recipeIngredients);
            }
        });

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

//        //Add Tags View
//        recipeAddTags.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });




        //Button to read ingredients from an image taken with the camera
        ingredientsFromImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!ingredientsChanged){
                    recipeIngredients.setText("");
                    ingredientsChanged = true;
                }

                //open camera and do stuff with image
                imageToTextEditText = recipeIngredients;
                dispatchTakePictureIntent();
            }
        });

        //Button to read instructions from an image taken with the camera
        instructionsFromImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //removes hardcoded text from text view
                if(!instructionsChanged){
                    recipeInstructions.setText("");
                    instructionsChanged = true;
                }

                imageToTextEditText = recipeInstructions;
                dispatchTakePictureIntent();
                //open camera and do stuff with image
            }
        });


        return view;
    }


    private void closeFragment(){
        getFragmentManager().beginTransaction().remove(AddRecipeFragment.this).commit();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imView.setImageBitmap(imageBitmap);

            Uri uri = Uri.fromFile(photoFile);
            Bitmap bitmap;


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                readTextInImage(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }




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

                        //set returned text as text in text box
                        String resultText = result.getText();
                        imageToTextEditText.append(resultText);
                        photoFile.delete();  //deletes the image used for text.


                        // Task completed successfully
                        // ...
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







}