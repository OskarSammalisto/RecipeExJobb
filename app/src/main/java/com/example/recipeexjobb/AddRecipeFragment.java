package com.example.recipeexjobb;

import android.Manifest;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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

    //Button variables
    private ImageButton saveRecipeButton;
    private ImageButton cancelButton;
    private ImageButton ingredientsFromImage;
    private ImageButton instructionsFromImage;

    //Edit text variables
    private EditText recipeTitle;
    private EditText recipeDescription;
    private EditText recipeIngredients;
    private EditText recipeInstructions;

    //Current text Box that shall receive text from image to text api
    EditText imageToTextEditText;

    //image view for recipe image
    ImageView imView;

    //File to save photo taken with camera
    File photoFile;

    //Constant used for the camera intent
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA = 1;


    //String to create photo path for new photos
    private String currentPhotoPath;

    Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_add_recipie, container, false);

        //instantiate recipe image variable
        imView = view.findViewById(R.id.addRecipeImage);

        //instantiate button variables and edit text
        saveRecipeButton = view.findViewById(R.id.saveRecipe);
        cancelButton = view.findViewById(R.id.exitAddRecipe);
        ingredientsFromImage = view.findViewById(R.id.cameraAddIngredients);
        instructionsFromImage = view.findViewById(R.id.cameraAddInstructions);

        recipeTitle = view.findViewById(R.id.addRecipeTitle);
        recipeDescription = view.findViewById(R.id.addRecipeDescription);
        recipeIngredients = view.findViewById(R.id.addRecipeIngredients);
        recipeInstructions = view.findViewById(R.id.addRecipeInstructions);


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



                ((MainActivity) getActivity()).createRecipe(title, description, ingredients, instructions);

                closeFragment();
            }
        });

        //Button to read ingredients from an image taken with the camera
        ingredientsFromImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //open camera and do stuff with image
                imageToTextEditText = recipeIngredients;
                dispatchTakePictureIntent();
            }
        });

        //Button to read instructions from an image taken with the camera
        instructionsFromImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
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

            //TODO: send picture to cloud instead of setting it as thumbnail.



            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                readTextInImage(bitmap);
                imView.setImageBitmap(bitmap);

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
                        imageToTextEditText.setText(resultText);


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
