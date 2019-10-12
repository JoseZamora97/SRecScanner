package com.josezamora.tcscanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.josezamora.tcscanner.Adapters.CompositionsRecyclerAdapter;
import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Classes.IOCompositionsController;
import com.josezamora.tcscanner.Dialogs.NewCompositionDialog;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;

import java.io.File;
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
    IOCompositionsController compositionsController;

    private Stack<Composition> compositionsRemoved;

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

        btnSwitchViewMode = findViewById(R.id.imageViewMode);
        recyclerView = findViewById(R.id.rv_compositions);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        compositionsController = new IOCompositionsController(this);
        compositionsController.loadCompositions();

        recyclerAdapter = new CompositionsRecyclerAdapter(compositionsController,
                this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        updateViewMode();

        compositionsRemoved = new Stack<>();

    }


    @Override
    protected void onStop() {
        super.onStop();
        compositionsController.saveCompositions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositionsController.saveCompositions();
    }

    @Override
    public void onItemClick(int position) {

        Intent toCompositionActivityIntent = new Intent(this,
                CompositionActivity.class);

        toCompositionActivityIntent.putExtra(AppGlobals.COMPOSITION_KEY, position);

        startActivity(toCompositionActivityIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        compositionsController.loadCompositions();
        recyclerAdapter.notifyDataSetChanged();
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
        new NewCompositionDialog(compositionsController)
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

                compositionsRemoved.push(compositionsController.getCompositions().remove(position));
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
                                compositionsController.getCompositions().add(position, compositionsRemoved.pop());
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
