package com.josezamora.srecscanner.activities;

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
import android.widget.Toast;

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
import com.josezamora.srecscanner.AppGlobals;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.RecyclerViewOnClickListener;
import com.josezamora.srecscanner.firebase.Classes.CloudNotebook;
import com.josezamora.srecscanner.firebase.Classes.CloudUser;
import com.josezamora.srecscanner.firebase.Controllers.FirebaseController;
import com.josezamora.srecscanner.firebase.GlideApp;
import com.josezamora.srecscanner.preferences.PreferencesController;
import com.josezamora.srecscanner.srecprotocol.SRecProtocolController;
import com.josezamora.srecscanner.viewholders.CloudNotebookViewHolder;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * The type Main activity.
 */
@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity
        implements RecyclerViewOnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * The constant LIST_ITEM.
     */
    private static final int LIST_ITEM = R.layout.list_notebook_item;
    /**
     * The constant GRID_ITEM.
     */
    private static final int GRID_ITEM = R.layout.grid_notebook_item;
    /**
     * The Recycler view.
     */
    private RecyclerView recyclerView;
    /**
     * The Swipe refresh layout.
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * The Btn switch view mode.
     */
    private ImageView btnSwitchViewMode;
    /**
     * The Drawer layout.
     */
    private DrawerLayout drawerLayout;
    /**
     * The current view mode.
     */
    private int viewMode = LIST_ITEM;

    /**
     * The User.
     */
    private CloudUser user;

    /**
     * The Firebase controller.
     */
    private FirebaseController firebaseController;
    /**
     * The Cloud notebook adapter.
     */
    private FirestoreRecyclerAdapter cloudNotebookAdapter;

    /**
     * The Item decor vertical.
     */
    private DividerItemDecoration itemDecorVertical;

    /**
     * The S rec protocol controller.
     */
    private SRecProtocolController sRecProtocolController;
    /**
     * The Text view s rec.
     */
    private TextView textViewSRec;

    /**
     * The Preferences controller.
     */
    private PreferencesController preferencesController;
    /**
     * The Simple callback.
     */
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            /* Nothing */
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
                        .setAction(R.string.undo, v -> {
                        })
                        .show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            /* The way to show icons when user tries to swipe a item */
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState
                    , isCurrentlyActive)
                    .addBackgroundColor(getResources().getColor(R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_delete_sweep_30dp)
                    .addSwipeLeftLabel(getString(R.string.delete))
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

        // Ask for permissions.
        ActivityCompat.requestPermissions(MainActivity.this,
                AppGlobals.REQUIRED_PERMISSIONS, AppGlobals.REQUEST_CODE_PERMISSIONS);

        // Set-up toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set-up drawer.
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set-up user info.
        ImageView userImage = findViewById(R.id.user_pic);
        TextView userName = findViewById(R.id.user_name);
        TextView userEmail = findViewById(R.id.user_mail);

        // Set-up label SRec
        textViewSRec = findViewById(R.id.conectar_srec);

        // Set-up version
        ((TextView) findViewById(R.id.version)).setText(AppGlobals.VERSION);

        // Set-up navigation items.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        // Set-up controllers.
        firebaseController = new FirebaseController();
        preferencesController = new PreferencesController(this);
        sRecProtocolController = new SRecProtocolController();

        // Clear connection preferences.
        preferencesController.clearSRecConnection();

        // Load the user.
        user = CloudUser.userFromFirebase(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));
        // Create the user if not created yed.
        firebaseController.createUser(user);

        // Set-up activity visual elements.
        btnSwitchViewMode = findViewById(R.id.imageViewMode);
        recyclerView = findViewById(R.id.rv_notebooks);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        updateViewMode();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        GlideApp.with(this).load(user.getPhotoUrl()).into(userImage);

        DividerItemDecoration itemDecorHorizontal = new DividerItemDecoration(this,
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
        // Starts the cloud adapter listener
        cloudNotebookAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stops the cloud adapter listener
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

    /*
     *  OnClick Methods of buttons.
     */

    /**
     * Send inform.
     * Open the report activity.
     *
     * @param v the button that has the onClick set up.
     */
    public void sendInform(View v) {
        Intent toReportActivity = new Intent(this, ReportActivity.class);
        toReportActivity.putExtra(AppGlobals.USER_KEY, user);
        startActivity(toReportActivity);
    }

    /**
     * This is call when a button is clicked.
     * Swap view mode.
     * From list item views to a grid item view.
     *
     * @param v the button that has the onClick set up.
     */
    public void swapViewMode(View v) {
        if (viewMode == LIST_ITEM)
            viewMode = GRID_ITEM;
        else if (viewMode == GRID_ITEM)
            viewMode = LIST_ITEM;
        else
            throw new IllegalStateException("BAD View Mode " + viewMode);

        updateViewMode();
    }

    /**
     * Log out.
     * SignOut from Firebase Auth.
     * @param v the button that has the onClick set up.
     */
    public void logOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(toLoginActivity);
        finish();
    }

    /**
     * Qr reader open.
     * Open the QR reader activity.
     * @param v the button that has the onClick set up.
     */
    public void qrReaderOpen(View v) {
        String[] ip_port = preferencesController.getConnectionDetailsSRec();
        if (ip_port[0] == null || ip_port[0].equals(SRecProtocolController.NONE)) {
            Intent qrActivity = new Intent(this, QRActivity.class);
            startActivityForResult(qrActivity, AppGlobals.REQUEST_CODE_QR);
        } else {
            sRecProtocolController.stopConnection(ip_port[0], ip_port[1]);
            preferencesController.clearSRecConnection();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Add new notebook.
     * Create a dialog to enter the new notebook information
     * @param v the button that has the onClick set up.
     */
    public void addNewNotebook(View v) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = li.inflate(R.layout.dialog_name, null);

        Typeface font = ResourcesCompat.getFont(this, R.font.nunito_bold);

        builderConfig.setTitle(R.string.new_notebook);
        builderConfig.setView(view);
        builderConfig.setCancelable(false);

        final EditText editTextName = view.findViewById(R.id.editTextName);

        builderConfig.setPositiveButton(R.string.aceptar, (dialogInterface, i) -> {
            if(!editTextName.getText().toString().equals("")) {
                String notebookId = String.valueOf(System.currentTimeMillis());
                CloudNotebook cloudNotebook = new CloudNotebook(
                        notebookId, editTextName.getText().toString(), user.getuId());
                firebaseController.addNotebook(cloudNotebook);
            }
        });

        builderConfig.setNegativeButton(R.string.cancelar,
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

    /**
     * Open help info.
     * Create a dialog with the info.
     * @param v the button that has the onClick set up.
     */
    public void openHelpInfo(View v) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = li.inflate(R.layout.dialog_help, null);

        builderConfig.setTitle(R.string.info);
        builderConfig.setView(view);
        builderConfig.setCancelable(true);

        AlertDialog alertDialog = builderConfig.create();
        alertDialog.show();
    }

    /*
     * Auxiliary methods.
     */

    private FirestoreRecyclerAdapter getCloudRecyclerAdapter() {

        final RecyclerViewOnClickListener rvOnClick = this;

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

                if (model.getLanguage().equals(".java"))
                    holder.getImageLanguage().setImageResource(R.drawable.ic_java);
                else if (model.getLanguage().equals(".py"))
                    holder.getImageLanguage().setImageResource(R.drawable.ic_python);
                else
                    holder.getImageLanguage().setImageResource(R.drawable.ic_code_24dp);

                holder.getTxtNameNotebook().setText(name);
                holder.getTxtNumImages().setText(numImagesText);
            }
        };
    }

    private void updateViewMode() {
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

    private void handleQRResult(String result) {

        // Get ip-port info from result.
        String[] ip_port = result.split(":");

        // Launch new thread which start the connection with SRec Receiver.
        new Thread(() -> {
            if (sRecProtocolController.serverInSameNetwork(result)) {
                sRecProtocolController.startConnection(ip_port[1], ip_port[2]);
                preferencesController.connectedToSRec(sRecProtocolController.getIp(), sRecProtocolController.getPort());
                updateTextConnectionToggle();
            } else {
                Toast.makeText(getApplicationContext(), "No estás conectado a la misma red que SRecReceiver",
                        Toast.LENGTH_SHORT).show();
                preferencesController.clearSRecConnection();
            }

        }).start();
    }

    private void updateTextConnectionToggle() {
        String[] ip_port = preferencesController.getConnectionDetailsSRec();

        if (ip_port[0] == null || ip_port[0].equals(SRecProtocolController.NONE))
            textViewSRec.setText(getResources().getString(R.string.conectar_con_srec));
        else
            textViewSRec.setText(getResources().getString(R.string.desconectar_de_srec));
    }

}
