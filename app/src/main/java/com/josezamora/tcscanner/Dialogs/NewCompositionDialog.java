package com.josezamora.tcscanner.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.R;

import java.io.File;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NewCompositionDialog extends AppCompatDialogFragment {

    private EditText editTextName;
    private List<Composition> compositionList;
    private File pathToNew;

    public NewCompositionDialog (List<Composition> compositionList, File path ) {
        this.compositionList = compositionList;
        this.pathToNew = path;
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
                        addNewComposition();
                    }
                });

        return builder.create();
    }

    private void addNewComposition() {
        Composition composition = new Composition(editTextName.getText().toString());
        File path = new File(pathToNew, String.valueOf(composition.getId()));
        composition.setAbsolutePath(path.getAbsolutePath());
        compositionList.add(composition);
    }
}
