package com.example.photogallery;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.stream.Collectors;

public class PhotoViewerActivity extends AppCompatActivity {
    private ImageView photoView;
    private TextView tagTextView;
    private FloatingActionButton prevButton;
    private FloatingActionButton nextButton;
    private FloatingActionButton addTagButton;
    private FloatingActionButton movePhotoButton;
    private List<Photo> photos;
    private int currentPosition;
    private String albumName;
    private AlbumManager albumManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        // Initialize views
        photoView = findViewById(R.id.photoView);
        tagTextView = findViewById(R.id.tagTextView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        addTagButton = findViewById(R.id.addTagButton);
        movePhotoButton = findViewById(R.id.movePhotoButton);

        // Get album name and photo path from intent
        albumName = getIntent().getStringExtra("album_name");
        String photoPath = getIntent().getStringExtra("photo_path");

        albumManager = AlbumManager.getInstance(this);
        photos = albumManager.loadAlbums().stream()
                .filter(album -> album.getName().equals(albumName))
                .findFirst()
                .map(Album::getPhotos)
                .orElse(null);

        if (photos != null) {
            // Find current photo position
            for (int i = 0; i < photos.size(); i++) {
                if (photos.get(i).getPath().equals(photoPath)) {
                    currentPosition = i;
                    break;
                }
            }

            // Set up navigation buttons
            prevButton.setOnClickListener(v -> showPreviousPhoto());
            nextButton.setOnClickListener(v -> showNextPhoto());
            addTagButton.setOnClickListener(v -> showAddTagDialog());
            movePhotoButton.setOnClickListener(v -> showMovePhotoDialog());

            // Show initial photo
            showCurrentPhoto();
        }
    }

    private void showCurrentPhoto() {
        if (photos == null || photos.isEmpty()) return;

        Photo currentPhoto = photos.get(currentPosition);
        photoView.setImageBitmap(albumManager.loadPhoto(currentPhoto));
        
        // Update tag display
        StringBuilder tagsText = new StringBuilder();
        for (Tag tag : currentPhoto.getTags()) {
            tagsText.append(tag.getType()).append(": ").append(tag.getValue()).append("\n");
        }
        tagTextView.setText(tagsText.toString());

        // Update button visibility
        prevButton.setVisibility(currentPosition > 0 ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility(currentPosition < photos.size() - 1 ? View.VISIBLE : View.INVISIBLE);
    }

    private void showPreviousPhoto() {
        if (currentPosition > 0) {
            currentPosition--;
            showCurrentPhoto();
        }
    }

    private void showNextPhoto() {
        if (currentPosition < photos.size() - 1) {
            currentPosition++;
            showCurrentPhoto();
        }
    }

    private void showAddTagDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tag, null);
        Spinner tagTypeSpinner = dialogView.findViewById(R.id.tagTypeSpinner);
        EditText tagValueInput = dialogView.findViewById(R.id.tagValueInput);

        // Set up tag type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tag_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_tag)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String tagValue = tagValueInput.getText().toString().trim();
                    if (!tagValue.isEmpty()) {
                        Tag.Type tagType = Tag.Type.values()[tagTypeSpinner.getSelectedItemPosition()];
                        addTag(new Tag(tagType, tagValue));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addTag(Tag tag) {
        Photo currentPhoto = photos.get(currentPosition);
        if (currentPhoto.hasTag(tag)) {
            Toast.makeText(this, R.string.tag_already_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        currentPhoto.addTag(tag);
        saveAlbums();
        showCurrentPhoto();
    }

    private void saveAlbums() {
        List<Album> albums = albumManager.loadAlbums();
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                albumManager.saveAlbums(albums);
                break;
            }
        }
    }

    private void showMovePhotoDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_move_photo, null);
        Spinner albumSpinner = dialogView.findViewById(R.id.albumSpinner);

        // Get all albums except current one
        List<Album> albums = albumManager.loadAlbums();
        List<String> albumNames = albums.stream()
                .map(Album::getName)
                .filter(name -> !name.equals(albumName))
                .collect(Collectors.toList());

        if (albumNames.isEmpty()) {
            Toast.makeText(this, "No other albums available", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, albumNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        albumSpinner.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle(R.string.move_photo)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String destinationAlbum = albumSpinner.getSelectedItem().toString();
                    movePhotoToAlbum(destinationAlbum);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void movePhotoToAlbum(String destinationAlbum) {
        Photo currentPhoto = photos.get(currentPosition);
        
        // Remove from current album
        photos.remove(currentPosition);
        
        // Add to destination album
        List<Album> albums = albumManager.loadAlbums();
        for (Album album : albums) {
            if (album.getName().equals(destinationAlbum)) {
                album.getPhotos().add(currentPhoto);
                break;
            }
        }
        
        // Save changes
        albumManager.saveAlbums(albums);
        
        // Update UI
        if (photos.isEmpty()) {
            finish();
        } else {
            if (currentPosition >= photos.size()) {
                currentPosition = photos.size() - 1;
            }
            showCurrentPhoto();
        }
        
        Toast.makeText(this, R.string.photo_moved_successfully, Toast.LENGTH_SHORT).show();
    }
} 