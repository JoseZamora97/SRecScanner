package com.josezamora.tcscanner.Firebase.Vision;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

import androidx.annotation.NonNull;

public class VisualAnalyzer implements Runnable {

    private StringBuilder scanResult;
    private Bitmap bitmap;

    public VisualAnalyzer(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {

        scanResult = new StringBuilder();

        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();

        if (blocks.size() != 0) {
            for (int i = 0; i < blocks.size(); i++) {
                List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++)
                    scanResult.append(lines.get(j).getText());
                scanResult.append("\n");
            }
        }
    }

    @Override
    public void run() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processTextRecognitionResult(firebaseVisionText);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });

    }

    public StringBuilder getScanResult() {
        return scanResult;
    }
}
