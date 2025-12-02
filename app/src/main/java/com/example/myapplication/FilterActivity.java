package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

public class FilterActivity extends BottomSheetDialogFragment {

    private ChipGroup categoryGroup;
    private RangeSlider priceSlider;
    private TextView minPriceLabel, maxPriceLabel;
    private Button resetButton, applyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter, container, false);

        categoryGroup = view.findViewById(R.id.categoryGroup);
        priceSlider = view.findViewById(R.id.priceSlider);
        minPriceLabel = view.findViewById(R.id.minPriceLabel);
        maxPriceLabel = view.findViewById(R.id.maxPriceLabel);
        resetButton = view.findViewById(R.id.resetButton);
        applyButton = view.findViewById(R.id.applyButton);

        setupLogic();

        return view;
    }

    private void setupLogic(){
        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            float min = slider.getValues().get(0);
            float max = slider.getValues().get(1);

            minPriceLabel.setText("$" + (int) min);
            maxPriceLabel.setText("$" + (int) max);
        });

        resetButton.setOnClickListener(v -> {
            categoryGroup.clearCheck();

            priceSlider.setValues(0f, 3000f);

            minPriceLabel.setText("$0");
            maxPriceLabel.setText("$3000");
        });

        applyButton.setOnClickListener(v -> {
            int checkedId = categoryGroup.getCheckedChipId();
            String selectedCategory = null;

            if (checkedId != -1){
                Chip selectedChip = categoryGroup.findViewById(checkedId);
                if (selectedChip != null)
                    selectedCategory = selectedChip.getText().toString();
            }

            float minPrice = priceSlider.getValues().get(0);
            float maxPrice = priceSlider.getValues().get(1);

            Intent intent = new Intent(getContext(), ResultActivity.class);
            intent.putExtra("category", selectedCategory);
            intent.putExtra("minPrice", minPrice);
            intent.putExtra("maxPrice", maxPrice);

            startActivity(intent);
            dismiss();
        });
    }
}