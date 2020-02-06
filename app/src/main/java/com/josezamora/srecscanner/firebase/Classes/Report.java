package com.josezamora.srecscanner.firebase.Classes;

import java.io.Serializable;

/**
 * The type Report.
 */
@SuppressWarnings("unused")
public class Report implements Serializable {

    private String id;
    private String comment;

    private String manufacturer;
    private String brand;
    private String model;
    private String board;
    private String hardware;
    private String density;
    private String bootloader;
    private String user;
    private String host;
    private String version;
    private String apiLevel;
    private String buildId;
    private String build_time;
    private String fingerprint;


    /**
     * Instantiates a new Report.
     */
    public Report() {}

    /**
     * Instantiates a new Report.
     *
     * @param id           the id
     * @param comment      the comment
     * @param manufacturer the manufacturer
     * @param brand        the brand
     * @param model        the model
     * @param board        the board
     * @param hardware     the hardware
     * @param density      the density
     * @param bootloader   the bootloader
     * @param user         the user
     * @param host         the host
     * @param version      the version
     * @param apiLevel     the api level
     * @param buildId      the build id
     * @param build_time   the build time
     * @param fingerprint  the fingerprint
     */
    public Report(String id,
                  String comment,
                  String manufacturer,
                  String brand,
                  String model,
                  String board,
                  String hardware,
                  String density,
                  String bootloader,
                  String user,
                  String host,
                  String version,
                  String apiLevel,
                  String buildId,
                  String build_time,
                  String fingerprint) {

        this.id = id;
        this.comment = comment;
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.model = model;
        this.board = board;
        this.hardware = hardware;
        this.density = density;
        this.bootloader = bootloader;
        this.user = user;
        this.host = host;
        this.version = version;
        this.apiLevel = apiLevel;
        this.buildId = buildId;
        this.build_time = build_time;
        this.fingerprint = fingerprint;
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
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets manufacturer.
     *
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Gets brand.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Gets model.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public String getBoard() {
        return board;
    }

    /**
     * Gets hardware.
     *
     * @return the hardware
     */
    public String getHardware() {
        return hardware;
    }

    /**
     * Gets density.
     *
     * @return the density
     */
    public String getDensity() {
        return density;
    }

    /**
     * Gets bootloader.
     *
     * @return the bootloader
     */
    public String getBootloader() {
        return bootloader;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets api level.
     *
     * @return the api level
     */
    public String getApiLevel() {
        return apiLevel;
    }

    /**
     * Gets build id.
     *
     * @return the build id
     */
    public String getBuildId() {
        return buildId;
    }

    /**
     * Gets build time.
     *
     * @return the build time
     */
    public String getBuild_time() {
        return build_time;
    }

    /**
     * Gets fingerprint.
     *
     * @return the fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }
}
