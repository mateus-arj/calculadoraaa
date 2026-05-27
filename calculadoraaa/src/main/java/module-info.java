module com.example.calculadoraaa {

    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.calculadoraaa to javafx.fxml;

    exports com.example.calculadoraaa;
}