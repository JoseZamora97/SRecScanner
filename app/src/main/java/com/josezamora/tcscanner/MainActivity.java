package com.josezamora.tcscanner;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.josezamora.tcscanner.Adapters.CompositionsRecyclerAdapter;
import com.josezamora.tcscanner.Dialogs.NewCloudCompositionDialog;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseDatabaseController;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseStorageController;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.ViewHolders.CloudCompositionViewHolder;


import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
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
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView btnSwitchViewMode;
    ItemTouchHelper itemTouchHelper;

    public static final int LIST_ITEM = R.layout.list_composition_item;
    public static final int GRID_ITEM = R.layout.grid_composition_item;
    private int viewMode = LIST_ITEM;

    CloudUser user;

    FirebaseDatabaseController databaseController;
    FirebaseStorageController storageController;

    FirestoreRecyclerAdapter cloudCompositionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                AppGlobals.REQUIRED_PERMISSIONS, AppGlobals.REQUEST_CODE_PERMISSIONS);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseController = new FirebaseDatabaseController();

        user = CloudUser.userFromFirebase(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));

        databaseController.createUser(user);

        btnSwitchViewMode = findViewById(R.id.imageViewMode);
        recyclerView = findViewById(R.id.rv_compositions);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);

        updateViewMode();
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cloudCompositionsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudCompositionsAdapter.stopListening();
    }

    @Override
    public void onItemClick(int position) {
        final CloudComposition composition = (CloudComposition) cloudCompositionsAdapter
                .getItem(position);

        Toast.makeText(this, composition.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {}

    @Override
    public void onRefresh() {
        cloudCompositionsAdapter.notifyDataSetChanged();
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                    cloudCompositionsAdapter.updateOptions(databaseController
                            .createRecyclerOptions(user));

                else
                    cloudCompositionsAdapter.updateOptions(databaseController
                            .createFilterOptions(user, newText));

                cloudCompositionsAdapter.startListening();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private FirestoreRecyclerAdapter getCloudRecyclerAdapter() {

        final RecyclerViewOnClickInterface rvOnClick = this;

        return new FirestoreRecyclerAdapter<CloudComposition, CloudCompositionViewHolder>(
                databaseController.createRecyclerOptions(user)) {

            @NonNull
            @Override
            public CloudCompositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(viewMode, parent, false);
                return new CloudCompositionViewHolder(view, rvOnClick);
            }

            @Override
            protected void onBindViewHolder(@NonNull CloudCompositionViewHolder holder, int position,
                                            @NonNull CloudComposition model) {
                String name = model.getName();
                if (viewMode != LIST_ITEM) {
                    if (name.length() >= 10) {
                        name = new StringBuffer(name).substring(0, 9);
                        name += "...";
                    }
                }
                holder.getTxtName().setText(name);
            }
        };
    }

    public void updateViewMode() {

        cloudCompositionsAdapter = getCloudRecyclerAdapter();

        if (viewMode == LIST_ITEM) {
            btnSwitchViewMode.setImageResource(R.drawable.ic_grid_24dp);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            btnSwitchViewMode.setImageResource(R.drawable.ic_list_24dp);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        recyclerView.setAdapter(cloudCompositionsAdapter);
        cloudCompositionsAdapter.startListening();

    }

    public void swapViewMode(View v) {
        if (viewMode == LIST_ITEM)
            viewMode = GRID_ITEM;
        else if (viewMode == GRID_ITEM)
            viewMode = LIST_ITEM;
        else
            throw new IllegalStateException("BAD Viewmode " + viewMode);

        updateViewMode();
    }

    public void addNewComposition(View view) {
        new NewCloudCompositionDialog(user, databaseController)
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

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Swipe Right to Left to delete.
            if(direction == ItemTouchHelper.LEFT) {

                final int position = viewHolder.getAdapterPosition();
                final CloudComposition composition = (CloudComposition) cloudCompositionsAdapter
                        .getItem(position);

                databaseController.deleteComposition(composition);

                Snackbar.make(recyclerView, composition.getName() + " "
                                + "ha sido eliminado"
                        , Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3000)
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseController.addComposition(composition);
                            }
                        })
                        .show();
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
