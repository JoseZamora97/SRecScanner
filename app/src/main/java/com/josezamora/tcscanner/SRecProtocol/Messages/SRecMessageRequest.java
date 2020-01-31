package com.josezamora.tcscanner.SRecProtocol.Messages;

/**
 * @author Jose Miguel Zamora Batista.
 * SRecMessage implementation,
 * This class is used for send messages
 * from client to server.
 */
public class SRecMessageRequest extends SRecMessage {

    /* File to send */
    private final byte[] fileContent;

    private String name;

    /**
     * Constructor of a request.
     * @param code the code of the new request
     * @param fileContent the file to send. Can be Null.
     */
    public SRecMessageRequest(byte code, String name, byte[] fileContent) {
        super(code);
        this.fileContent = fileContent;
        this.name = name;
    }

    /**
     * Return the file.
     * @return the file.
     */
    public byte[] getFileContent() {
        return fileContent;
    }

    /**
     * Return the filename.
     * @return the filename.
     */
    public String getName() {
        return name;
    }

}
