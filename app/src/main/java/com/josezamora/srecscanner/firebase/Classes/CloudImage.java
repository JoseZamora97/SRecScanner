package com.josezamora.srecscanner.firebase.Classes;

import java.io.Serializable;

/**
 * The type Cloud image.
 */
public class CloudImage implements Serializable {

    private String id;
    private String owner;
    private String notebook;
    private String firebaseStoragePath;
    private String downloadLink;
    private int order;

    /**
     * Instantiates a new Cloud image.
     */
    @SuppressWarnings("unused")
    public CloudImage() {}

    /**
     * Instantiates a new Cloud image.
     *
     * @param id                  the id
     * @param owner               the owner
     * @param notebook            the notebook
     * @param firebaseStoragePath the firebase storage path
     * @param downloadLink        the download link
     * @param order               the order
     */
    public CloudImage(String id, String owner, String notebook, String firebaseStoragePath,
                      String downloadLink, int order ){
        this.id = id;
        this.owner = owner;
        this.notebook = notebook;
        this.firebaseStoragePath = firebaseStoragePath;
        this.downloadLink = downloadLink;
        this.order = order;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets notebook.
     *
     * @return the notebook
     */
    public String getNotebook() {
        return notebook;
    }

    /**
     * Gets firebase storage path.
     *
     * @return the firebase storage path
     */
    public String getFirebaseStoragePath() {
        return firebaseStoragePath;
    }

    /**
     * Gets download link.
     *
     * @return the download link
     */
    public String getDownloadLink() {
        return downloadLink;
    }

    /**
     * Gets order.
     *
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets order.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }
}
