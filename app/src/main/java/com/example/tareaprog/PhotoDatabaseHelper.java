package com.example.tareaprog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "photoDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";

    public PhotoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_IMAGE + " BLOB,"
                + COLUMN_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    public void addPhoto(Photograph photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Bitmap image = photo.getImage();
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(COLUMN_IMAGE, byteArray);
        } else {
            // Si la imagen es nula, puedes manejarlo de alguna manera (omitir o establecer un valor predeterminado).
            // Aquí simplemente no agregamos nada a la columna de imagen.
        }

        values.put(COLUMN_DESCRIPTION, photo.getDescription());

        db.insert(TABLE_PHOTOS, null, values);
        db.close();
    }

    public List<Photograph> getAllPhotos() {
        List<Photograph> photos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTOS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int imageIndex = cursor.getColumnIndex(COLUMN_IMAGE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                // Esto se uso para Verificar si las columnas existen antes de intentar obtener los datos
                if (imageIndex != -1 && descriptionIndex != -1) {
                    byte[] imageBytes = cursor.getBlob(imageIndex);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    String description = cursor.getString(descriptionIndex);

                    Photograph photo = new Photograph();
                    photo.setImage(imageBitmap);
                    photo.setDescription(description);

                    photos.add(photo);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return photos;
    }
}