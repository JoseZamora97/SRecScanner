package com.josezamora.srecscanner.qr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Qr detector processor.
 */
public class QRDetectorProcessor implements Detector.Processor<Barcode> {

    /**
     * The Qr pattern format ip and port.
     */
    private static final String QR_PATTERN_FORMAT_IP_AND_PORT =
            "^(SRecReceiver:)" +
                    "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])" +
                    ":[0-9]+$";
    /**
     * The Ip port pattern.
     */
    private Pattern ipPortPattern;
    /**
     * The Context.
     */
    private Activity context;

    /**
     * Instantiates a new Qr detector processor.
     *
     * @param context the context
     */
    public QRDetectorProcessor(Context context) {
        this.ipPortPattern = Pattern.compile(QR_PATTERN_FORMAT_IP_AND_PORT);
        this.context = (Activity) context;
    }

    @Override
    public void release() {
        /* Nothing */
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> qrCodes = detections.getDetectedItems();
        Matcher matcher;

        if (qrCodes.size() > 0) {
            matcher = ipPortPattern.matcher(qrCodes.valueAt(0).displayValue);
            if(matcher.matches())
                activityResult(matcher.group());
        }
    }

    private void activityResult(String token) {
        Intent returnActivity = new Intent();

        returnActivity.putExtra("result", token);

        this.context.setResult(Activity.RESULT_OK, returnActivity);
        this.context.finish();
    }
}
