package com.josezamora.tcscanner.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.josezamora.tcscanner.AppGlobals;
import com.josezamora.tcscanner.Firebase.Classes.CloudNotebook;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseController;
import com.josezamora.tcscanner.Firebase.GlideApp;
import com.josezamora.tcscanner.Preferences.PreferencesController;
import com.josezamora.tcscanner.R;
import com.josezamora.tcscanner.SRecProtocol.SRecController;
import com.josezamora.tcscanner.ViewHolders.CloudNotebookViewHolder;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface, SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView btnSwitchViewMode;
    ItemTouchHelper itemTouchHelper;
    DrawerLayout drawerLayout;

    ImageView userImage;
    TextView userName;
    TextView userEmail;

    public static final int LIST_ITEM = R.layout.list_notebook_item;
    public static final int GRID_ITEM = R.layout.grid_notebook_item;
    private int viewMode = LIST_ITEM;

    CloudUser user;

    FirebaseController firebaseController;
    FirestoreRecyclerAdapter cloudNotebookAdapter;

    DividerItemDecoration itemDecorHorizontal;
    DividerItemDecoration itemDecorVertical;

    SRecController sRecController;
    TextView textViewSRec;

    PreferencesController preferencesController;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        boolean undo;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                final int position = viewHolder.getAdapterPosition();
                final CloudNotebook notebook = (CloudNotebook) cloudNotebookAdapter
                        .getItem(position);

                firebaseController.deleteNotebook(notebook, false);

                Snackbar.make(recyclerView, notebook.getName() + " ha sido eliminado"
                        , Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3000)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                undo = event == BaseTransientBottomBar
                                        .BaseCallback.DISMISS_EVENT_ACTION;

                                if (undo) firebaseController.addNotebook(notebook);
                                else firebaseController
                                        .deleteNotebook(notebook, true);
                            }
                        })
                        .setAction("Deshacer", v -> {
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
                    .setSwipeLeftLabelTextSize(COMPLEX_UNIT_SP, 16)
                    .setSwipeLeftLabelTypeface(ResourcesCompat
                            .getFont(getApplicationContext(), R.font.nunito))
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.colorPrimary))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                AppGlobals.REQUIRED_PERMISSIONS, AppGlobals.REQUEST_CODE_PERMISSIONS);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        userImage = findViewById(R.id.user_pic);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_mail);

        textViewSRec = findViewById(R.id.conectar_srec);

        ((TextView) findViewById(R.id.version)).setText(AppGlobals.VERSION);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        firebaseController = new FirebaseController();
        preferencesController = new PreferencesController(this);
        sRecController = new SRecController();

        preferencesController.clearSRecConnection();

        user = CloudUser.userFromFirebase(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));

        firebaseController.createUser(user);

        btnSwitchViewMode = findViewById(R.id.imageViewMode);
        recyclerView = findViewById(R.id.rv_notebooks);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);

        updateViewMode();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        GlideApp.with(this).load(user.getPhotoUrl()).into(userImage);

        itemDecorHorizontal = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecorHorizontal.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider_horizontal)));

        recyclerView.addItemDecoration(itemDecorHorizontal);

        itemDecorVertical = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        itemDecorVertical.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider_vertical)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawerLayout.closeDrawer(GravityCompat.START, false);

        updateTextConnectionToggle();

        cloudNotebookAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudNotebookAdapter.stopListening();
    }

    @Override
    public void onLongItemClick(int position) { /* Nothing */ }

    @Override
    public void onItemClick(int position) {
        final CloudNotebook notebook = (CloudNotebook) cloudNotebookAdapter
                .getItem(position);

        Intent toNotebookActivity = new Intent(this, NotebookActivity.class);
        toNotebookActivity.putExtra(AppGlobals.NOTEBOOK_KEY, notebook);
        startActivity(toNotebookActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        cloudNotebookAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AppGlobals.REQUEST_CODE_QR)
            if (data != null)
                handleQRResult(Objects.requireNonNull(data.getStringExtra("result")));
    }

    private void handleQRResult(String result) {
        String[] ip_port = result.split(":");

        sRecController.startConnection(ip_port[1], ip_port[2]);
        preferencesController.connectedToSRec(sRecController.getIp(), sRecController.getPort());

        updateTextConnectionToggle();
    }

    private void updateTextConnectionToggle() {
        String[] ip_port = preferencesController.getConnectionDetailsSRec();

        if (ip_port[0] == null || ip_port[0].equals(SRecController.NONE))
            textViewSRec.setText("Conectar con SRecReceiver");
        else
            textViewSRec.setText("Desconectar de SRecReceiver");
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
                if (newText.equals("")) cloudNotebookAdapter
                        .updateOptions(firebaseController.getRecyclerOptions(user));
                else cloudNotebookAdapter
                        .updateOptions(firebaseController.getRecyclerOptions(user, newText));
                cloudNotebookAdapter.startListening();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void sendInform(View v) {
        Intent toReportActivity = new Intent(this, ReportActivity.class);
        toReportActivity.putExtra(AppGlobals.USER_KEY, user);
        startActivity(toReportActivity);
    }

    private FirestoreRecyclerAdapter getCloudRecyclerAdapter() {

        final RecyclerViewOnClickInterface rvOnClick = this;
        return new FirestoreRecyclerAdapter<CloudNotebook, CloudNotebookViewHolder>(
                firebaseController.getRecyclerOptions(user)) {

            @NonNull
            @Override
            public CloudNotebookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(viewMode, parent, false);
                return new CloudNotebookViewHolder(view, rvOnClick);
            }

            @Override
            protected void onBindViewHolder(@NonNull CloudNotebookViewHolder holder, int position,
                                            @NonNull CloudNotebook model) {
                String name = model.getName();
                if (viewMode != LIST_ITEM) {
                    if (name.length() >= 10) {
                        name = new StringBuffer(name).substring(0, 9);
                        name += "...";
                    }
                }

                String numImagesText = getString(R.string.imagenes) + " " + model.getNumImages()
                        + "/" + AppGlobals.MAX_PHOTOS_PER_NOTEBOOK;

                holder.getTxtNameNotebook().setText(name);
                holder.getTxtNumImages().setText(numImagesText);
            }
        };
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

    public void logOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(toLoginActivity);
        finish();
    }

    public void updateViewMode() {
        cloudNotebookAdapter = getCloudRecyclerAdapter();

        if (viewMode == LIST_ITEM) {
            recyclerView.removeItemDecoration(itemDecorVertical);
            btnSwitchViewMode.setImageResource(R.drawable.ic_grid_24dp);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.addItemDecoration(itemDecorVertical);
            btnSwitchViewMode.setImageResource(R.drawable.ic_list_24dp);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        recyclerView.setAdapter(cloudNotebookAdapter);
        cloudNotebookAdapter.startListening();
    }

    public void qrReaderOpen(View v) {
        if (!sRecController.isConnected()) {
            Intent qrActivity = new Intent(this, QRActivity.class);
            startActivityForResult(qrActivity, AppGlobals.REQUEST_CODE_QR);
        } else {
            sRecController.stopConnection();
            preferencesController.clearSRecConnection();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void addNewNotebook(View v) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = li.inflate(R.layout.dialog_name, null);

        Typeface font = ResourcesCompat.getFont(this, R.font.nunito_bold);

        builderConfig.setTitle("Nuevo cuaderno");
        builderConfig.setView(view);
        builderConfig.setCancelable(false);

        final EditText editTextName = view.findViewById(R.id.editTextName);

        builderConfig.setPositiveButton("Aceptar", (dialogInterface, i) -> {
            if(!editTextName.getText().toString().equals("")) {
                String notebookId = String.valueOf(System.currentTimeMillis());
                CloudNotebook cloudNotebook = new CloudNotebook(
                        notebookId, editTextName.getText().toString(), user.getuId());
                firebaseController.addNotebook(cloudNotebook);
            }
        });

        builderConfig.setNegativeButton("Cancelar",
                (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = builderConfig.create();
        alertDialog.show();

        Button btn1 = alertDialog.findViewById(android.R.id.button1);
        assert btn1 != null;
        btn1.setTypeface(font);

        Button btn2 = alertDialog.findViewById(android.R.id.button2);
        assert btn2 != null;
        btn2.setTypeface(font);
    }
}
