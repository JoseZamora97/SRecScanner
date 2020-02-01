package com.josezamora.tcscanner.SRecProtocol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.josezamora.tcscanner.R;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import SRecProtocol.Client.Client;
import SRecProtocol.Client.SRecClient;
import SRecProtocol.Messages.SRecMessageRequest;


@SuppressLint("StaticFieldLeak")
public class SRecController {

    public static final String NONE = "none";

    private boolean connected;
    private SRecClient client;

    private String ip, port;

    private Activity context;

    public SRecController(Activity context) {
        this.connected = false;
        this.client = null;
        this.context = context;
        this.ip = this.port = NONE;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public boolean isConnected() {
        return connected;
    }

    public void startConnection(String ip, String port) {
        if(!connected) {
            this.ip = ip;
            this.port = port;

            new StartConnectionTask().execute();
            ((TextView) this.context.findViewById(R.id.conectar_srec)).setText("Desconectar " +
                    "de SRecReceiver");
        }
    }

    public void stopConnection() {
        new StopConnectionTask().execute();
        connected = false;
        ((TextView) context.findViewById(R.id.conectar_srec)).setText("Conectar con SRecReceiver");
    }

    public void sendFile(File file) {
        new SendFileTask(file).execute();
    }

    public void connectAndSendFile(String ip, String port, File file) {
        if(!connected) {
            this.ip = ip;
            this.port = port;

            new SendFileTask(file).execute();
        }
    }

    class SendFileTask extends AsyncTask<Void, Void, Void> {

        File file;

        SendFileTask(File file) {
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            client = new SRecClient(ip, port);
            SRecMessageRequest request = null;

            try {
                request = new SRecMessageRequest(Client.PUT,
                        file.getName(), fileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            client.send(request);

            return null;
        }
    }

    private byte[] fileToByteArray(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        byte[] fileContent = new byte[(int) f.length()];
        f.readFully(fileContent);

        return fileContent;
    }

    class StopConnectionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            client = new SRecClient(ip, port);
            SRecMessageRequest request = new SRecMessageRequest(Client.BYE,
                    null,null);
            client.send(request);

            ip = port = NONE;

            return null;
        }
    }

    class StartConnectionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            client = new SRecClient(ip, port);
            SRecMessageRequest request = new SRecMessageRequest(Client.HII,
                    null, null);

            client.send(request);
            connected = true;

            return null;
        }
    }

}
