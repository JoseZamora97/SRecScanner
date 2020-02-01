package com.josezamora.srecscanner.firebase.Classes;

import java.io.Serializable;

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


    public Report() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getBoard() {
        return board;
    }

    public String getHardware() {
        return hardware;
    }

    public String getDensity() {
        return density;
    }

    public String getBootloader() {
        return bootloader;
    }

    public String getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public String getBuildId() {
        return buildId;
    }

    public String getBuild_time() {
        return build_time;
    }

    public String getFingerprint() {
        return fingerprint;
    }
}
