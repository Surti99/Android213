package com.example.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlbumManager {
    private static final String PREF_NAME = "PhotoGalleryPrefs";
    private static final String ALBUMS_KEY = "albums";
    private static final String TAG = "AlbumManager";
    private static AlbumManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    private AlbumManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized AlbumManager getInstance(Context context) {
        if (instance == null) {
            instance = new AlbumManager(context);
        }
        return instance;
    }

    public void saveAlbums(List<Album> albums) {
        String json = gson.toJson(albums);
        sharedPreferences.edit().putString(ALBUMS_KEY, json).apply();
    }

    public List<Album> loadAlbums() {
        String json = sharedPreferences.getString(ALBUMS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Album>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void savePhoto(Photo photo, Bitmap bitmap) {
        try {
            File file = new File(context.getFilesDir(), photo.getPath());
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving photo", e);
        }
    }

    public Bitmap loadPhoto(Photo photo) {
        try {
            File file = new File(context.getFilesDir(), photo.getPath());
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            Log.e(TAG, "Error loading photo", e);
            return null;
        }
    }
} 