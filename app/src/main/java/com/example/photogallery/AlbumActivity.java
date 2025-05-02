package com.example.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private static final int PICK_PHOTO_REQUEST = 1;
    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photos;
    private String albumName;
    private AlbumManager albumManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumName = getIntent().getStringExtra("album_name");
        if (albumName == null) {
            finish();
            return;
        }

        setTitle(albumName);
        albumManager = AlbumManager.getInstance(this);

        // Load photos for this album
        photos = new ArrayList<>();
        List<Album> albums = albumManager.loadAlbums();
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                photos.addAll(album.getPhotos());
                break;
            }
        }

        // Set up RecyclerView
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        photosRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        photoAdapter = new PhotoAdapter(photos, new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo) {
                viewPhoto(photo);
            }

            @Override
            public void onPhotoLongClick(Photo photo) {
                showPhotoOptions(photo);
            }
        });
        photosRecyclerView.setAdapter(photoAdapter);

        // Set up FAB
        FloatingActionButton addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(v -> pickPhoto());
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_REQUEST);
    }

    private void viewPhoto(Photo photo) {
        Intent intent = new Intent(this, PhotoViewerActivity.class);
        intent.putExtra("photo_path", photo.getPath());
        intent.putExtra("album_name", albumName);
        startActivity(intent);
    }

    private void showPhotoOptions(Photo photo) {
        // TODO: Implement photo options (remove, move, add tag)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                addPhoto(uri);
            }
        }
    }

    private void addPhoto(Uri uri) {
        // TODO: Implement photo addition
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // TODO: Implement search
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 