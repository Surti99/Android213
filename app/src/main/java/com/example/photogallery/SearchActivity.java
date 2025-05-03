package com.example.photogallery;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {
    private Spinner tagTypeSpinner;
    private EditText tagValueInput;
    private Button addTagButton;
    private Button searchButton;
    private TextView searchCriteriaText;
    private RecyclerView searchResultsRecyclerView;
    private List<Tag> searchCriteria;
    private PhotoAdapter photoAdapter;
    private AlbumManager albumManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        tagTypeSpinner = findViewById(R.id.tagTypeSpinner);
        tagValueInput = findViewById(R.id.tagValueInput);
        addTagButton = findViewById(R.id.addTagButton);
        searchButton = findViewById(R.id.searchButton);
        searchCriteriaText = findViewById(R.id.searchCriteriaText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Initialize data
        searchCriteria = new ArrayList<>();
        albumManager = AlbumManager.getInstance(this);

        // Set up tag type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tag_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner.setAdapter(adapter);

        // Set up RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(new ArrayList<>(), this::onPhotoClick);
        searchResultsRecyclerView.setAdapter(photoAdapter);

        // Set up click listeners
        addTagButton.setOnClickListener(v -> addSearchCriteria());
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void addSearchCriteria() {
        String tagValue = tagValueInput.getText().toString().trim();
        if (tagValue.isEmpty()) {
            Toast.makeText(this, "Please enter a tag value", Toast.LENGTH_SHORT).show();
            return;
        }

        Tag.Type tagType = Tag.Type.values()[tagTypeSpinner.getSelectedItemPosition()];
        Tag tag = new Tag(tagType, tagValue);
        searchCriteria.add(tag);

        // Update search criteria text
        updateSearchCriteriaText();

        // Clear input
        tagValueInput.setText("");
    }

    private void updateSearchCriteriaText() {
        StringBuilder text = new StringBuilder(getString(R.string.search_criteria) + "\n");
        for (Tag tag : searchCriteria) {
            text.append(tag.getType()).append(": ").append(tag.getValue()).append("\n");
        }
        searchCriteriaText.setText(text.toString());
    }

    private void performSearch() {
        if (searchCriteria.isEmpty()) {
            Toast.makeText(this, "Please add at least one search criterion", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Photo> allPhotos = albumManager.loadAlbums().stream()
                .flatMap(album -> album.getPhotos().stream())
                .collect(Collectors.toList());

        List<Photo> searchResults = allPhotos.stream()
                .filter(photo -> searchCriteria.stream()
                        .allMatch(criteria -> photo.getTags().stream()
                                .anyMatch(tag -> tag.getType() == criteria.getType() &&
                                        tag.getValue().equalsIgnoreCase(criteria.getValue()))))
                .collect(Collectors.toList());

        if (searchResults.isEmpty()) {
            Toast.makeText(this, R.string.no_photos_found, Toast.LENGTH_SHORT).show();
        }

        photoAdapter.updatePhotos(searchResults);
    }

    private void onPhotoClick(Photo photo) {
        // Find the album containing this photo
        String albumName = albumManager.loadAlbums().stream()
                .filter(album -> album.getPhotos().contains(photo))
                .findFirst()
                .map(Album::getName)
                .orElse("");

        // Start PhotoViewerActivity
        startActivity(PhotoViewerActivity.newIntent(this, albumName, photo.getPath()));
    }
} 