package com.example.tareaprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ImageView imageView;
    private EditText descriptionEditText;
    private PhotoDatabaseHelper dbHelper;
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.imageView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dbHelper = new PhotoDatabaseHelper(this);

        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> savePhoto());

        // Actualizar el ListView al iniciar la actividad
        updatePhotoListView();
    }

    private void dispatchTakePictureIntent() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Ya tienes el permiso, puedes iniciar la actividad de la cámara
            startCameraActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, inicia la actividad de la cámara
                startCameraActivity();
            } else {
                // Permiso denegado, puedes mostrar un mensaje al usuario
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);

                // Actualizar el ListView después de guardar la foto
                updatePhotoListView();
            }
        }
    }

    private void savePhoto() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        String description = descriptionEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put(PhotoDatabaseHelper.COLUMN_IMAGE, image);
        values.put(PhotoDatabaseHelper.COLUMN_DESCRIPTION, description);
        dbHelper.getWritableDatabase().insert(PhotoDatabaseHelper.TABLE_PHOTOS, null, values);

        Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();

        // Actualizar el ListView después de guardar la foto
        updatePhotoListView();
    }

    private void updatePhotoListView() {
        List<Photograph> photos = dbHelper.getAllPhotos();
        adapter = new PhotoAdapter(this, photos);
        ListView photoListView = findViewById(R.id.photoListView);
        photoListView.setAdapter(adapter);
    }
}






