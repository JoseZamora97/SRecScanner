package com.josezamora.tcscanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.josezamora.tcscanner.Adapters.CompositionsRecyclerAdapter;
import com.josezamora.tcscanner.Classes.IOCompositionsController;
import com.josezamora.tcscanner.Dialogs.NewCompositionDialog;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseDatabaseController;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseStorageController;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private int viewMode = 0;

    public static final int VIEW_MODEL_LIST = 0;
    public static final int VIEW_MODEL_GRID = 1;

    CloudUser user;

    FirebaseDatabaseController databaseController;
    FirebaseStorageController storageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                AppGlobals.REQUIRED_PERMISSIONS, AppGlobals.REQUEST_CODE_PERMISSIONS);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseController = new FirebaseDatabaseController();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(AppGlobals.PROVIDERS)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.AppTheme)
                        .build(),
                AppGlobals.REQUEST_CODE_SIGN_IN);


//        btnSwitchViewMode = findViewById(R.id.imageViewMode);
//        recyclerView = findViewById(R.id.rv_compositions);
//
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(this);
//
//        compositionsController = new IOCompositionsController(this);
//        compositionsController.loadCompositions();
//
//        recyclerAdapter = new CompositionsRecyclerAdapter(compositionsController,
//                this);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(recyclerAdapter);
//
//        itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//        updateViewMode();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        TODO: fix
//        compositionsController.saveCompositions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: fix
//        compositionsController.saveCompositions();
    }

    @Override
    public void onItemClick(int position) {
//  TODO: fix.
//        Intent toCompositionActivityIntent = new Intent(this,
//                CompositionActivity.class);
//
//        toCompositionActivityIntent.putExtra(AppGlobals.COMPOSITION_KEY, position);
//
//        startActivity(toCompositionActivityIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: fix
//        compositionsController.loadCompositions();
//        recyclerAdapter.notifyDataSetChanged();
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
        /* TODO: Fix.
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
        }); */

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
        new NewCompositionDialog(user, databaseController)
                .show(getSupportFragmentManager(), "NEW");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppGlobals.REQUEST_CODE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser userFromOAuth = FirebaseAuth.getInstance().getCurrentUser();
                assert userFromOAuth != null;
                user = CloudUser.userFromFirebase(userFromOAuth);
                databaseController.createUser(user);
            }
            else {
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }
                if (Objects.requireNonNull(response.getError()).getErrorCode()
                        == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }
                showSnackbar(R.string.unknown_error);
            }
        }
    }

    private void showSnackbar(int stringRes) {
        //TODO: snack con error.
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
                // TODO:
                throw new RuntimeException("not implemented");
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
