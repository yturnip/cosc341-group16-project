package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements RecentSearchAdapter.OnDeleteClickListener{

    private RecyclerView recentRecycler;
    private RecentSearchAdapter recentAdapter;
    private List<String> recentList = new ArrayList<>();

    private EditText searchInput;
    private ImageView filterIcon, searchBackButton;
    private TextView clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        filterIcon = findViewById(R.id.filterIcon);
        clearAll = findViewById(R.id.clearAll);
        recentRecycler = findViewById(R.id.recentRecycler);
        searchBackButton = findViewById(R.id.searchBackButton);

        searchBackButton.setOnClickListener(v -> finish());

        loadRecentSearches();

        recentAdapter = new RecentSearchAdapter(recentList, this);

        recentRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentRecycler.setAdapter(recentAdapter);

        searchInput.setOnEditorActionListener((v, actionId, event) ->{
            String text = searchInput.getText().toString().trim();

            if (!text.isEmpty()){
                addRecent(text);
                searchInput.setText("");
            }

            return true;
        });

        clearAll.setOnClickListener(v -> {
            recentList.clear();
            saveRecentSearches();
            recentAdapter.notifyDataSetChanged();
        });

        filterIcon.setOnClickListener(v -> {
            startActivity(new android.content.Intent(SearchActivity.this, FilterActivity.class));
        });
    }

    private void addRecent(String text){
        if (text.isEmpty()) return;

//        recentList.remove(text);
//
//        recentList.add(0, text);

        if (recentList.contains(text))
            recentList.remove(text);

        recentList.add(0, text);

        if (recentList.size() > 10){
            recentList = recentList.subList(0, 10);
        }

        saveRecentSearches();
        recentAdapter.notifyDataSetChanged();
    }

    public void onDeleteClick(String text){
        recentList.remove(text);
        saveRecentSearches();
        recentAdapter.notifyDataSetChanged();
    }

    private void saveRecentSearches(){
        SharedPreferences prefs = getSharedPreferences("search_history", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(recentList);
        editor.putString("recent", json);
        editor.apply();
    }

    private void loadRecentSearches(){
        SharedPreferences prefs = getSharedPreferences("search_history", MODE_PRIVATE);
        String json = prefs.getString("recent", "[]");

        Type type = new TypeToken<List<String>>(){}. getType();
        List<String> savedList = new Gson().fromJson(json, type);

        if (savedList != null){
            recentList.clear();
            recentList.addAll(savedList);
        }
    }
}