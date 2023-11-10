package com.example.camtranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 123;
    private final int GALLERY_REQ_CODE = 1000;

    public String imageName = "";

    //consts

    //components vars
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init components
        iv = (ImageView) findViewById(R.id.imageView_Gallery);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);

        }
    }

    /**
     * This function isfor loging out form the authentication
     * @param view
     */
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        finish();
    }


    /**
     * on click for choosing image from gallery
     * @param view
     */
    public void gallery(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
    }

    /**
     * sending the image for upload to firebase storage
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

        if(resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE)
        {
            imageName = "image_" + System.currentTimeMillis() + ".jpg";
            Uri imageUri = data.getData();
            uploadImage(imageUri, imageName);
        }
    }

    /**
     * function for uploading image to firebase storage
     * @param imageUri
     * @param name
     */
    private void uploadImage(Uri imageUri, String name) {
        // Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

        // Create a reference to the specific image
        StorageReference imageReference = storageReference.child(name);

        // Upload the image to Firebase Storage
        imageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Photo uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to upload photo due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * on click for downloading image from firebase storage and displaying it
     * @param view
     */
    public void download(View view)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageName);

        final long MAX_BYTES = 4096 * 4096;
        storageReference.getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes)
            {
                //convert byte to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * on click for going to text recognition activity
     * @param view
     */
    public void next(View view)
    {
        Intent sourceIntent = new Intent(MainActivity.this, TextRecognitionActivity.class);
        startActivity(sourceIntent);
    }
}