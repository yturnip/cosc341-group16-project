package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SellActivity extends AppCompatActivity {
    ImageButton backToHome;
    MaterialCardView createVideoCard, addPhotosCard;
    RecyclerView previewsRecyclerView;
    MaterialButton publishButton;

    private final List<Uri> selectedImageUris = new ArrayList<>();
    private ImagePreviewAdapter imagePreviewAdapter;

    // Launcher for picking multiple images
    private final ActivityResultLauncher<Intent> pickMultipleImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // This handles multiple image selection
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                            selectedImageUris.add(imageUri);
                        }
                    }
                    // This handles single image selection
                    else if (result.getData().getData() != null) {
                        Uri imageUri = result.getData().getData();
                        selectedImageUris.add(imageUri);
                    }
                    // Update the adapter and make the RecyclerView visible
                    imagePreviewAdapter.notifyDataSetChanged();
                    previewsRecyclerView.setVisibility(View.VISIBLE);
                }
            });

    // Launcher for recording a video
    private final ActivityResultLauncher<Intent> recordVideoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    // For simplicity, we can add the video URI to the same list.
                    // In a real app, you might handle it differently.
                    selectedImageUris.add(videoUri);
                    imagePreviewAdapter.notifyDataSetChanged();
                    previewsRecyclerView.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sell);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backToHome = findViewById(R.id.backButton);
        createVideoCard = findViewById(R.id.createVideoCard);
        addPhotosCard = findViewById(R.id.addPhotosCard);
        previewsRecyclerView = findViewById(R.id.previewsRecyclerView);
        publishButton = findViewById(R.id.publishButton);

        // --- Setup RecyclerView Adapter ---
        imagePreviewAdapter = new ImagePreviewAdapter(this, selectedImageUris);
        previewsRecyclerView.setAdapter(imagePreviewAdapter);

        backToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(SellActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        addPhotosCard.setOnClickListener(v -> {
            // Create an intent to open the image gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selections
            intent.setType("image/* video/*"); // Allow both images and videos

            // You can specify mimetypes to be more specific
            // String[] mimeTypes = {"image/jpeg", "image/png", "video/mp4"};
            // intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            pickMultipleImagesLauncher.launch(intent);
        });

        createVideoCard.setOnClickListener(v -> {
            // Create an intent to open the camera for video recording
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // Limit video duration to 60 seconds (1 minute)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            recordVideoLauncher.launch(intent);
        });

        publishButton.setOnClickListener(v -> publishProduct());
    }

private void publishProduct() {
    // --- Find views for all input fields ---
    TextInputEditText titleEditText = findViewById(R.id.titleEditText);
    TextInputEditText priceEditText = findViewById(R.id.priceEditText);
    TextInputEditText descriptionEditText = findViewById(R.id.descriptionEditText);
    TextInputEditText locationEditText = findViewById(R.id.locationEditText);
    ChipGroup conditionChipGroup = findViewById(R.id.conditionChipGroup);
    ChipGroup categoryChipGroup = findViewById(R.id.categoryChipGroup);

    // 1. Get data from EditText fields
    String title = titleEditText.getText().toString().trim();
    String priceStr = priceEditText.getText().toString().trim();
    String description = descriptionEditText.getText().toString().trim();
    String location = locationEditText.getText().toString().trim();
    User currentUser = ProductRepository.getInstance().getCurrentUser();

    // 2. Validate text input and image selection
    if (title.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
        Toast.makeText(this, "Please fill all text fields.", Toast.LENGTH_SHORT).show();
        return;
    }
    if (selectedImageUris.isEmpty()) {
        Toast.makeText(this, "Please add at least one photo or video.", Toast.LENGTH_SHORT).show();
        return;
    }
    if (currentUser == null) {
        Toast.makeText(this, "Cannot publish without a logged-in user.", Toast.LENGTH_SHORT).show();
        return;
    }

    // 3. Get selected chip text for Condition and Category
    int selectedConditionId = conditionChipGroup.getCheckedChipId();
    int selectedCategoryId = categoryChipGroup.getCheckedChipId();

    if (selectedConditionId == View.NO_ID || selectedCategoryId == View.NO_ID) {
        Toast.makeText(this, "Please select a condition and category.", Toast.LENGTH_SHORT).show();
        return;
    }

    Chip conditionChip = findViewById(selectedConditionId);
    Chip categoryChip = findViewById(selectedCategoryId);
    String condition = conditionChip.getText().toString();
    String category = categoryChip.getText().toString();

    // --- Create the Product object ---
    try {
        double price = Double.parseDouble(priceStr);
        // Create a unique ID for the new product (e.g., using timestamp)
        String productId = "prod" + System.currentTimeMillis();
        // Use the first image as the main imageUrl, or a placeholder
        //String imageUrl = selectedImageUris.get(0).toString();
        Uri temporaryImageUri = selectedImageUris.get(0);
        Uri permanentImageUri = copyImageToInternalStorage(temporaryImageUri, productId);


        Product newProduct = new Product(
                productId,
                title,
                price,
                permanentImageUri.toString(),
                "Available", // Default status
                currentUser.getUserId(), // Get seller ID from the current user
                condition,
                category,
                description,
                location
        );

        // 4. Add the product to the repository
        ProductRepository.getInstance().addProduct(newProduct);

        // 5. Notify user and finish activity
        Toast.makeText(this, "Product published successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Go back to the previous screen

    } catch (NumberFormatException e) {
        Toast.makeText(this, "Please enter a valid price.", Toast.LENGTH_SHORT).show();
    }
}

    // --- ADD THIS HELPER METHOD inside your SellActivity class ---
    private Uri copyImageToInternalStorage(Uri uri, String newFileName) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Create a file in your app's private directory (e.g., /data/data/com.example.myapplication/files/prod12345.jpg)
            File file = new File(getFilesDir(), newFileName + ".jpg");
            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
            }
            inputStream.close();

            // Return the URI of the newly created file, which is permanent
            return Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}