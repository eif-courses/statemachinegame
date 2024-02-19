module eif.viko.lt.simple.statemachinegame{
        requires javafx.controls;
        requires javafx.fxml;


        opens eif.viko.lt.simple.statemachine to javafx.fxml;
        exports eif.viko.lt.simple.statemachine;
        }