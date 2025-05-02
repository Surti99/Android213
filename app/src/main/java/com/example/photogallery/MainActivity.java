package com.example.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private static final int CREATE_ALBUM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize albums list (in a real app, this would load from storage)
        albums = new ArrayList<>();
        
        // Set up RecyclerView
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
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

        // Set up FAB
        FloatingActionButton addAlbumButton = findViewById(R.id.addAlbumButton);
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAlbumDialog();
            }
        });
    }

    private void openAlbum(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("album_name", album.getName());
        startActivity(intent);
    }

    private void showAlbumOptions(Album album) {
        // TODO: Implement album options dialog (rename, delete)
    }

    private void showCreateAlbumDialog() {
        // TODO: Implement create album dialog
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ALBUM_REQUEST && resultCode == RESULT_OK) {
            String albumName = data.getStringExtra("album_name");
            if (albumName != null && !albumName.isEmpty()) {
                albums.add(new Album(albumName));
                albumAdapter.notifyDataSetChanged();
            }
        }
    }
} 