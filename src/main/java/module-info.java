module com.example.controlequipolap {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.example.controlequipolap to javafx.fxml;
    opens com.example.controlequipolap.controller to javafx.fxml;
    opens com.example.controlequipolap.model to javafx.base, org.hibernate.orm.core;
    exports com.example.controlequipolap;
}
