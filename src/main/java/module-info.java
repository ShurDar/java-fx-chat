module ru.gb.shurupova.javafxchat {
    requires javafx.controls;
    requires javafx.fxml;

    exports ru.gb.shurupova.javafxchat.client;
    opens ru.gb.shurupova.javafxchat.client to javafx.fxml;
}