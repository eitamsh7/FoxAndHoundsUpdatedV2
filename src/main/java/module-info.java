module com.foxandhounds.foxandhounds_v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;

    opens com.foxandhounds.foxandhounds_v1 to javafx.fxml;
    opens com.foxandhounds.foxandhounds_v1.controller to javafx.fxml;

    exports com.foxandhounds.foxandhounds_v1;
    exports com.foxandhounds.foxandhounds_v1.controller;
    exports com.foxandhounds.foxandhounds_v1.model;
}