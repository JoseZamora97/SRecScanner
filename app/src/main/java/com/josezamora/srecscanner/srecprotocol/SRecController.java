package com.josezamora.srecscanner.srecprotocol;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

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

    public SRecController() {
        this.connected = false;
        this.client = null;
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
        }
    }

    public void stopConnection() {
        new StopConnectionTask().execute();
        connected = false;
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
