package com.josezamora.tcscanner.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IOCompositionsController {

    private Context context;
    private List<Composition> compositions;
    private File root;

    private final String SHARED_PREFERENCES = "shared";
    private final String SHARED_COMPOSITIONS = "compositions";
    private final String PATH_TO_COMPOSITIONS = "Compositions";

    public IOCompositionsController(Context context) {
        this.context = context;
        this.compositions = new ArrayList<>();
        this.root = new File(context.getFilesDir(), PATH_TO_COMPOSITIONS);
    }

    public void saveCompositions() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(compositions);

        editor.putString(SHARED_COMPOSITIONS, json);
        editor.apply();
    }

    public void loadCompositions(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Composition>>(){}.getType();
        String json = sharedPreferences.getString(SHARED_COMPOSITIONS, null);

        if(json!=null)
            this.compositions = gson.fromJson(json, type);
    }

    public List<Composition> getCompositions() {
        return compositions;
    }

    public File getPathRoot() {
        return this.root;
    }
}
