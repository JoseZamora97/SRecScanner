package com.josezamora.srecscanner.firebase.Vision;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.josezamora.srecscanner.editor.CodeEditor;

import java.util.List;

public class VisualAnalyzer implements Runnable {

    private StringBuilder scanResult;
    private Bitmap bitmap;
    private CodeEditor codeView;

    public VisualAnalyzer(Bitmap bitmap, CodeEditor codeView) {
        this.bitmap = bitmap;
        this.codeView = codeView;
        scanResult = new StringBuilder();
    }

    @Override
    public void run() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {

                    scanResult = new StringBuilder();
                    List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();

                    if (blocks.size() != 0)
                        for (int i = 0; i < blocks.size(); i++) {
                            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                            for (int j = 0; j < lines.size(); j++)
                                scanResult.append(lines.get(j).getText()).append("\n");
                            scanResult.append("\n");
                        }

                    codeView.setText(scanResult.substring(0));
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }
}
