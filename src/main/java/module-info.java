module lk.ijse.wood_managment {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.management;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires mysql.connector.j;
    requires jasperreports;

    opens lk.ijse.wood_managment to javafx.fxml;
    opens lk.ijse.wood_managment.Dto to javafx.base;

    opens lk.ijse.wood_managment.Model to javafx.base, javafx.fxml;

    exports lk.ijse.wood_managment;
    exports lk.ijse.wood_managment.Controller;
    opens lk.ijse.wood_managment.Controller to javafx.fxml;
    exports lk.ijse.wood_managment.db;
    opens lk.ijse.wood_managment.db to javafx.fxml;
}
