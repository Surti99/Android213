package com.example.photogallery;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumAdapter albumAdapter;
    private List<Album> albums;
    private AlbumManager albumManager;
    private static final int CREATE_ALBUM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumManager = AlbumManager.getInstance(this);
        albums = albumManager.loadAlbums();
        
        // Initialize views
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        Button createAlbumButton = findViewById(R.id.createAlbumButton);
        Button searchButton = findViewById(R.id.searchButton);

        // Set up RecyclerView
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new AlbumAdapter(albums, new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(Album album) {
                openAlbum(album);
            }

            @Override
            public void onAlbumLongClick(Album album) {
                showAlbumOptions(album);
            }
        });
        albumsRecyclerView.setAdapter(albumAdapter);

        // Set up click listeners
        createAlbumButton.setOnClickListener(v -> showCreateAlbumDialog());
        searchButton.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    private void openAlbum(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("album_name", album.getName());
        startActivity(intent);
    }

    private void showAlbumOptions(Album album) {
        String[] options = {getString(R.string.rename_album), getString(R.string.delete_album)};
        new AlertDialog.Builder(this)
                .setTitle(album.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameAlbumDialog(album);
                    } else {
                        showDeleteAlbumDialog(album);
                    }
                })
                .show();
    }

    private void showCreateAlbumDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_album_name, null);
        EditText albumNameInput = dialogView.findViewById(R.id.albumNameInput);

        new AlertDialog.Builder(this)
                .setTitle(R.string.create_album)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String albumName = albumNameInput.getText().toString().trim();
                    if (!albumName.isEmpty()) {
                        createAlbum(albumName);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showRenameAlbumDialog(Album album) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_album_name, null);
        EditText albumNameInput = dialogView.findViewById(R.id.albumNameInput);
        albumNameInput.setText(album.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_album)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String newName = albumNameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        renameAlbum(album, newName);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDeleteAlbumDialog(Album album) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_album)
                .setMessage(getString(R.string.confirm_delete_album, album.getName()))
                .setPositiveButton(R.string.ok, (dialog, which) -> deleteAlbum(album))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void createAlbum(String name) {
        if (isAlbumNameExists(name)) {
            Toast.makeText(this, "Album name already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        albums.add(new Album(name));
        saveAlbums();
        albumAdapter.notifyDataSetChanged();
    }

    private void renameAlbum(Album album, String newName) {
        if (isAlbumNameExists(newName)) {
            Toast.makeText(this, "Album name already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        album.setName(newName);
        saveAlbums();
        albumAdapter.notifyDataSetChanged();
    }

    private void deleteAlbum(Album album) {
        albums.remove(album);
        saveAlbums();
        albumAdapter.notifyDataSetChanged();
    }

    private boolean isAlbumNameExists(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void saveAlbums() {
        albumManager.saveAlbums(albums);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAlbums();
    }
} 