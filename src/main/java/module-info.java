module lk.ijse.wood_management {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlin.stdlib;
    requires java.sql;
    requires java.management;
    requires java.desktop;
    requires mysql.connector.j;
    requires jasperreports;

    opens lk.ijse.wood_management to javafx.fxml;
    opens lk.ijse.wood_management.controller to javafx.fxml;
    opens lk.ijse.wood_management.dto to javafx.base, javafx.fxml;
    opens lk.ijse.wood_management.entity to javafx.base, javafx.fxml;
    opens lk.ijse.wood_management.db to javafx.fxml;
    opens lk.ijse.wood_management.util to javafx.fxml;

    exports lk.ijse.wood_management;
    exports lk.ijse.wood_management.controller;
    exports lk.ijse.wood_management.bo;
    exports lk.ijse.wood_management.bo.custom;
    exports lk.ijse.wood_management.bo.custom.impl;
    exports lk.ijse.wood_management.dao;
    exports lk.ijse.wood_management.dao.custom;
    exports lk.ijse.wood_management.dao.custom.impl;
    exports lk.ijse.wood_management.dto;
    exports lk.ijse.wood_management.entity;
    exports lk.ijse.wood_management.db;
    exports lk.ijse.wood_management.util;
}
