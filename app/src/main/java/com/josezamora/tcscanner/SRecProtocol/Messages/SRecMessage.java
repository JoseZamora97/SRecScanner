package com.josezamora.tcscanner.SRecProtocol.Messages;

import java.io.Serializable;

/**
 * @author Jose Miguel Zamora Batista.
 */
@SuppressWarnings("WeakerAccess")
public abstract class SRecMessage implements Serializable {

    /* Code */
    private final byte code;

    /**
     * Constructor.
     * @param code new message code.
     */
    public SRecMessage(byte code) {
        this.code = code;
    }

    /**
     * Get the message code.
     * @return the code.
     */
    public byte getCode() {
        return this.code;
    }
}
