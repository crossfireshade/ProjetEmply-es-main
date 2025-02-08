module com.example.workshopsgbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.workshopsgbd to javafx.fxml;
    exports com.example.workshopsgbd;
}