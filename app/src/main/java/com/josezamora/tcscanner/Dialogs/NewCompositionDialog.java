package com.josezamora.tcscanner.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseDatabaseController;
import com.josezamora.tcscanner.R;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NewCompositionDialog extends AppCompatDialogFragment {

    private CloudUser user;
    private EditText editTextName;

    private FirebaseDatabaseController databaseController;

    public NewCompositionDialog (CloudUser user, FirebaseDatabaseController databaseController) {
        this.user = user;
        this.databaseController = databaseController;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.dialog_name_composition, null);

        editTextName = view.findViewById(R.id.editTextName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .setTitle("Nueva Composición")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editTextName.getText().toString().equals(""))
                            addComposition();
                    }
                });

        return builder.create();
    }

    private void addComposition() {
        String compositionId = String.valueOf(System.currentTimeMillis());
        CloudComposition composition = new CloudComposition(
                compositionId, editTextName.getText().toString(), user.getuId());

        databaseController.addComposition(composition);
    }
}
