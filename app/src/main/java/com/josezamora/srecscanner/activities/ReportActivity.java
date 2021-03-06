package com.josezamora.srecscanner.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.josezamora.srecscanner.AppGlobals;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.firebase.Classes.CloudReport;
import com.josezamora.srecscanner.firebase.Classes.CloudUser;
import com.josezamora.srecscanner.firebase.Controllers.FirebaseController;

import java.util.Objects;

/**
 * The type Report activity.
 */
public class ReportActivity extends AppCompatActivity {

    /**
     * The Edit text details.
     */
    private EditText editTextDetails;
    /**
     * The Firebase controller.
     */
    private FirebaseController firebaseController;
    /**
     * The User.
     */
    private CloudUser user;
    /**
     * The Imm.
     */
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        firebaseController = new FirebaseController();

        user = (CloudUser) getIntent().getSerializableExtra(AppGlobals.USER_KEY);

        // Set-up toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.report);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editTextDetails = findViewById(R.id.editText_details);
        editTextDetails.requestFocus();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Send report.
     *
     * @param v the v
     */
    public void send_report(View v){

        String text = editTextDetails.getText().toString();

        if(text.equals(""))
            return;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int densityDpi = (int) (dm.density * 160f);

        CloudReport cloudReport = new CloudReport(
                user.getuId() + "@" + System.currentTimeMillis(),
                text,
                Build.MANUFACTURER,
                Build.BRAND,
                Build.MODEL,
                Build.BOARD,
                Build.HARDWARE,
                densityDpi + " dpi",
                Build.BOOTLOADER,
                Build.USER,
                Build.HOST,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT + "",
                Build.ID,
                Build.TIME + "",
                Build.FINGERPRINT
        );

        firebaseController.sendReport(cloudReport);
        editTextDetails.setText("");

        Toast.makeText(this, this.getString(R.string.informe_enviado)
                , Toast.LENGTH_SHORT).show();

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
