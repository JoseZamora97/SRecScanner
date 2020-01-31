package com.josezamora.tcscanner.SRecProtocol.Client;

import com.josezamora.tcscanner.SRecProtocol.Messages.SRecMessageRequest;



/**
 * @author Jose Miguel Zamora Batista.
 * Interface Client,
 * Contains all codes that are sent from
 * clients to server.
 */
@SuppressWarnings("unused")
public interface Client {

    /**
     * CODE: HII,
     * when the server get this CODE saves in
     * aliveConnections list from ServerConnectionService class.
     * and say to client if is connected or not.
     */
    byte HII = 10;

    /**
     * CODE: PUT,
     * when the server get this CODE take
     * info from InputStream.
     * And create a file.
     */
    byte PUT = 13;

    /**
     * CODE: BYE,
     * when the server get this CODE removes the client
     * information from aliveConnections list from ServerConnectionService class.
     */
    byte BYE = 3;

    /**
     * Send to server a SRecMessageRequest message.
     * @see SRecMessageRequest
     * @param request the message sent to server
     *                with the request code and content.
     */
    void send(SRecMessageRequest request);

    /**
     * Allow to us if last request was good or not.
     * @return true if last request receive a Server.OKK
     *         false if last request receive a Server.BAD
     */
    boolean getLastResponseResult();
}
