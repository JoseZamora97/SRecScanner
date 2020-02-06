package com.josezamora.srecscanner.srecprotocol;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import SRecProtocol.Client.Client;
import SRecProtocol.Client.SRecClient;
import SRecProtocol.Messages.SRecMessageRequest;


/**
 * The type SRec controller.
 */
@SuppressLint("StaticFieldLeak")
public class SRecController {

    /**
     * The constant NONE.
     */
    public static final String NONE = "none";

    private boolean connected;

    private SRecClient client;

    private volatile String ip, port;

    /**
     * Instantiates a new SRec Controller.
     */
    public SRecController() {
        this.connected = false;
        this.client = null;
        this.ip = this.port = NONE;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Start connection.
     *
     * @param ip   the ip
     * @param port the port
     */
    public void startConnection(String ip, String port) {
        if(!connected) {
            this.ip = ip;
            this.port = port;

            new StartConnectionTask().execute();
        }
    }

    /**
     * Stop connection.
     */
    public void stopConnection() {
        new StopConnectionTask().execute();
        connected = false;
    }

    /**
     * Send file.
     *
     * @param file the file
     */
    public void sendFile(File file) {
        new SendFileTask(file).execute();
    }

    /**
     * Connect and send file.
     *
     * @param ip   the ip
     * @param port the port
     * @param file the file
     */
    public void connectAndSendFile(String ip, String port, File file) {
        if(!connected) {
            this.ip = ip;
            this.port = port;

            new SendFileTask(file).execute();
        }
    }

    /**
     * The type Send file task.
     */
    class SendFileTask extends AsyncTask<Void, Void, Void> {

        /**
         * The File to be sent.
         */
        File file;

        /**
         * Instantiates a new Send file task.
         *
         * @param file the file to be sent.
         */
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

            if (request != null) client.send(request);

            return null;
        }
    }

    private byte[] fileToByteArray(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        byte[] fileContent = new byte[(int) f.length()];
        f.readFully(fileContent);

        return fileContent;
    }

    /**
     * The type Stop connection task.
     */
    class StopConnectionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            client = new SRecClient(ip, port);
            client.send(new SRecMessageRequest(Client.BYE, null, null));
            ip = port = NONE;
            return null;
        }
    }

    /**
     * The type Start connection task.
     */
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
