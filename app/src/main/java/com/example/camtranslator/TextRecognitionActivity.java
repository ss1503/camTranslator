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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class TextRecognitionActivity extends AppCompatActivity {


    //components vars
    private ImageView iv;
    private Bitmap bitmap;
    public TextView tvResult;

    //Text recognizer
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        iv = (ImageView) findViewById(R.id.imageView_Camera);
        tvResult = (TextView) findViewById(R.id.textView_Result);

        //Request permission for camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TextRecognitionActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        //init texr recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }


    public void camera(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bitmap);
        }
    }

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

                InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

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