package com.josezamora.tcscanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.josezamora.tcscanner.Adapters.CompositionsRecyclerAdapter;
import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Classes.IOCompositions;
import com.josezamora.tcscanner.Dialogs.NewCompositionDialog;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;

import java.io.File;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface, SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    CompositionsRecyclerAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView btnSwitchViewMode;
    ItemTouchHelper itemTouchHelper;

    private List<Composition> compositions;
    private Stack<Composition> compositionsRemoved;
    private File fileCompositions;

    private int viewMode = 0;

    public static final int VIEW_MODEL_LIST = 0;
    public static final int VIEW_MODEL_GRID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                AppGlobals.REQUIRED_PERMISSIONS, AppGlobals.REQUEST_CODE_PERMISSIONS);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCompositions();

        btnSwitchViewMode = findViewById(R.id.imageViewMode);
        recyclerView = findViewById(R.id.rv_compositions);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerAdapter = new CompositionsRecyclerAdapter(compositions, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        updateViewMode();

    }

    @Override
    protected void onStop() {
        super.onStop();
        IOCompositions.saveCompositions(fileCompositions.getAbsolutePath() + "/",
                compositions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IOCompositions.saveCompositions(fileCompositions.getAbsolutePath() + "/",
                compositions);
    }

    @Override
    public void onItemClick(int position) {

        Composition composition = compositions.get(position);
        Intent toCompositionActivityIntent = new Intent(this,
                CompositionActivity.class);

        toCompositionActivityIntent.putExtra(AppGlobals.TO_COMPOSITION_KEY, composition);

        startActivity(toCompositionActivityIntent);
    }

    @Override
    public void onLongItemClick(int position) {}

    @Override
    public void onRefresh() {
        recyclerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem itemSearch = menu.findItem(R.id.mItemActionSearch);

        SearchView searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void updateViewMode() {

        if (viewMode == VIEW_MODEL_LIST) {
            btnSwitchViewMode.setImageResource(R.drawable.ic_grid_24dp);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerAdapter.switchViewMode(CompositionsRecyclerAdapter.LIST_ITEM);
        }
        else {
            btnSwitchViewMode.setImageResource(R.drawable.ic_list_24dp);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerAdapter.switchViewMode(CompositionsRecyclerAdapter.GRID_ITEM);
        }

        recyclerView.setAdapter(recyclerAdapter);

    }

    private void initCompositions() {

        compositionsRemoved = new Stack<>();
        fileCompositions = new File(getFilesDir(), AppGlobals.COMPOSITIONS_FILENAME);
        compositions = IOCompositions.recoverCompositions(fileCompositions.getAbsolutePath());

    }

    public void swapViewMode(View v) {
        if (viewMode == VIEW_MODEL_LIST)
            viewMode = VIEW_MODEL_GRID;
        else if (viewMode == VIEW_MODEL_GRID)
            viewMode = VIEW_MODEL_LIST;
        else
            throw new IllegalStateException("BAD Viewmode " + viewMode);

        updateViewMode();
    }

    public void addNewComposition(View view) {
        new NewCompositionDialog(compositions, fileCompositions)
                .show(getSupportFragmentManager(), "NEW");
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT ) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        boolean undo;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Swipe Right to Left to delete.
            if(direction == ItemTouchHelper.LEFT) {

                final int position  = viewHolder.getAdapterPosition();

                compositionsRemoved.push(compositions.remove(position));
                recyclerAdapter.notifyItemRemoved(position);

                undo = false;

                Snackbar.make(recyclerView, compositionsRemoved.peek().getName() + " "
                                + "ha sido eliminado"
                        , Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3000)
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undo = true;
                                compositions.add(position, compositionsRemoved.pop());
                                recyclerAdapter.notifyItemInserted(position);
                            }
                        })
                        .show();

                if(!undo)
                    // Todo: esto puede ir en un hilo.
                    new File(compositionsRemoved.pop().getAbsolutePath()).delete();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState
                    , isCurrentlyActive)
                    .addBackgroundColor(getResources().getColor(R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_delete_sweep_30dp)
                    .addSwipeLeftLabel("Eliminar")
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.colorPrimary))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
