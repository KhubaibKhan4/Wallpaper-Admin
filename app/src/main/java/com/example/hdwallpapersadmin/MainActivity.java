package com.example.hdwallpapersadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ImageView imageWall;
    EditText edtCaption;
    Button btnShowGallery;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference myStorageRef;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        storage = FirebaseStorage.getInstance();
        myStorageRef = storage.getReference().child(String.valueOf(UUID.randomUUID()));

        imageWall = (ImageView) findViewById(R.id.image_cover);
        edtCaption = (EditText) findViewById(R.id.edtCaption);
        btnShowGallery = (Button) findViewById(R.id.btnShowWallpaper);

        btnShowGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,WallpapersActivity.class));
            }
        });

        imageWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {

            Uri uri = data.getData();
            imageWall.setImageURI(uri);

            UploadTask uploadTask = myStorageRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    Task<Uri> downloadUriTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUriTask.addOnSuccessListener(downloadUrl -> {
                        String Captions = edtCaption.getText().toString();
                        Users users = new Users(downloadUrl.toString(), Captions);
                        myRef.child(String.valueOf(UUID.randomUUID())).setValue(users);
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount();
                    Toast.makeText(MainActivity.this, ""+progress, Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}