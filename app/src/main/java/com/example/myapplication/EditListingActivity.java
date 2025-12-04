package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class EditListingActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextInputEditText titleEditText, priceEditText, descriptionEditText, locationEditText;
    private ChipGroup conditionChipGroup, categoryChipGroup;
    private MaterialButton saveChangesButton;

    private ProductRepository repository;
    private Product productToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_listing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();

        // --- Get Product to Edit ---
        repository = ProductRepository.getInstance();
        String productId = getIntent().getStringExtra("productId");

        if (productId == null) {
            Toast.makeText(this, "Error: Product not found.", Toast.LENGTH_LONG).show();
            finish(); // Can't edit without a product ID
            return;
        }
        productToEdit = repository.getProductById(productId);

        if (productToEdit == null) {
            Toast.makeText(this, "Error: Product data could not be loaded.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        populateFields();

        setupListeners();
    }

    private void initializeViews() {
        // --- Text fields ---
        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);

        // --- Chip groups ---
        conditionChipGroup = findViewById(R.id.conditionChipGroup);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);

        // --- Buttons and Titles ---
        saveChangesButton = findViewById(R.id.publishButton); // Reusing the publish button
        backButton = findViewById(R.id.arrowBackButton);

        // Hide media upload cards as we are not supporting media editing for now
        findViewById(R.id.createVideoCard).setVisibility(View.GONE);
        findViewById(R.id.addPhotosCard).setVisibility(View.GONE);
        findViewById(R.id.previewsRecyclerView).setVisibility(View.GONE);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    private void populateFields() {
        String title = productToEdit.getName();
        double price = productToEdit.getPrice();
        String description = productToEdit.getDescription();
        String location = productToEdit.getLocation();
        String condition = productToEdit.getCondition();
        String category = productToEdit.getCategory();

        titleEditText.setText(title);
        priceEditText.setText(String.format("%.2f", price));
        descriptionEditText.setText(description);
        locationEditText.setText(location);
        for (int i = 0; i < conditionChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) conditionChipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(condition)) {
                chip.setChecked(true);
                break;
            }
        }
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryChipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(category)) {
                chip.setChecked(true);
                break;
            }
        }
    }

    private void saveChanges() {
        // Get the updated values from the fields
        String title = Objects.requireNonNull(titleEditText.getText().toString());
        String priceStr = Objects.requireNonNull(priceEditText.getText().toString());
        String description = Objects.requireNonNull(descriptionEditText.getText().toString());
        String location = Objects.requireNonNull(locationEditText.getText().toString());

        // Validate input
        if (title.isEmpty() || priceStr.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedConditionId = conditionChipGroup.getCheckedChipId();
        int selectedCategoryId = categoryChipGroup.getCheckedChipId();

        if (selectedConditionId == -1 || selectedCategoryId == -1) {
            Toast.makeText(this, "Please select a condition and category", Toast.LENGTH_SHORT).show();
            return;
        }

        Chip selectedConditionChip = findViewById(selectedConditionId);
        Chip selectedCategoryChip = findViewById(selectedCategoryId);

        String condition = selectedConditionChip.getText().toString();
        String category = selectedCategoryChip.getText().toString();

        try {
            double price = Double.parseDouble(priceStr);

            repository.updateProductDetails(productToEdit.getId(), title, price, description, location, condition, category);

            // Notify user
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
        }
    }
}