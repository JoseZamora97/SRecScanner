package com.josezamora.tcscanner.SRecProtocol.Messages;


/**
 * @author Jose Miguel Zamora Batista.
 * SRecMessage implementation,
 * This class is used for send messages
 * from server to clients.
 */
public class SRecMessageResponse extends SRecMessage{

    /**
     * Constructor of a response.
     * @param code the code of the new response.
     */
    public SRecMessageResponse(byte code) {
        super(code);
    }
}
