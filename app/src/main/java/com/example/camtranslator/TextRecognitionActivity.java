package com.example.camtranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;

public class TextRecognitionActivity extends AppCompatActivity {

    //Constants
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 101;

    //components vars
    private ImageView iv;
    private Bitmap thumbnail;
    public TextView tvResult;

    //Text recognizer
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        //init components
        iv = (ImageView) findViewById(R.id.imageView_Gallery);
        tvResult = (TextView) findViewById(R.id.textView_Result);

        //Request permission for camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TextRecognitionActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_CAPTURE_REQUEST_CODE);
        }

        //init text recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Permission for camera usage
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                camera(null);
            } else {
                // Permission is denied, show a message or handle the denial
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * on click when using camera
     * @param view
     */
    public void camera(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_REQUEST_CODE);
        }
    }

    /**
     * This function is for the result after taking a photo

     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            byte bytesOfImage[] = bytes.toByteArray();
            iv.setImageBitmap(thumbnail);

            uploadPhotoToFirebase(bytesOfImage);
        }
    }

    /**
     * This function uploads the image to firebase storage
     * @param bytesOfImage
     */
    private void uploadPhotoToFirebase(byte[] bytesOfImage)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + "image_" +System.currentTimeMillis() + ".jpg");
        storageReference.putBytes(bytesOfImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(TextRecognitionActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TextRecognitionActivity.this, "Upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This function regonizes the text from the image and dispaly it using google ML kit
     * @param view
     */
    public void recognizeText(View view)
    {
        //check if user picked an image
        if(iv.getDrawable() == null)
        {
            Toast.makeText(this, "Take a picture first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            try {

                //converting bitmap to input image
                InputImage inputImage = InputImage.fromBitmap(thumbnail, 0);

                Task<Text> textTaskResult = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {

                                //get the recognized text into string
                                String recognizedText = text.getText();
                                tvResult.setText(recognizedText);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextRecognitionActivity.this, "Failed recognizing because " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


            }catch (Exception e)
            {
                Toast.makeText(this, "Failed recognizing because " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}