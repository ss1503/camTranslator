package com.example.camtranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public String imageName = "";

    //consts
    private final int GALLERY_REQ_CODE = 1000;

    //components vars
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView_Gallery);

    }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void gallery(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE)
        {
            imageName = data.getData().getFragment();
            uploadImage(data.getData(), imageName);
        }
    }

    private void uploadImage(Uri imageUri, String name) {
        // Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

        // Create a unique name for the image
        String imageName = name + ".jpg";


        // Create a reference to the specific image
        StorageReference imageReference = storageReference.child(imageName);

        // Upload the image to Firebase Storage
        imageReference.putFile(imageUri);
    }

    public void download(View view)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageName + ".jpg");

        final long MAX_BYTES = 1024 * 1024;
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

    public void next(View view)
    {
        Intent sourceIntent = new Intent(MainActivity.this, TextRecognitionActivity.class);
        startActivity(sourceIntent);
    }
}