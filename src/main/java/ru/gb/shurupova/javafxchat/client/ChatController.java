package ru.gb.shurupova.javafxchat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class ChatController {
    @FXML
    private HBox authBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
    @FXML
    private VBox messageBox;
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;

    private final ChatClient client;

    public ChatController() {
        this.client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            } catch (IOException e) {
                showNotification();
            }
        }
    }

    private void showNotification() {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "Не могу подключиться к серверу.\n" +
                        "Проверьте, что сервер запущен и доступен",
                new ButtonType("Попоробовать снова", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE)
        );
        alert.setTitle("Ошибка подключения1");
        Optional<ButtonType> answer = alert.showAndWait();
        Boolean isExit = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if(isExit){
            System.exit(0);
        }

    }

    public void clickSendButton() {
        String message = messageField.getText(); // getText() возвращает тот текст, который будет введен
        if (message.isBlank()) { // isBlank() когда ответ пустой и когда вместо ответа пробелы
            return; // return тут нужен, чтобы досрочно выйти из метода
        }

        client.sendMessage(message);
        messageField.clear(); // очищаем поле userAnswer
        messageField.requestFocus(); // устанавливаем на него фокус, чтобы был курсор
    }

    public void addMessage(String message) {
        messageArea.appendText(message + "\n"); // метод .appendText() передает текст
    }

    public void setAuth(boolean success){
        authBox.setVisible(!success);
        messageBox.setVisible(success);
    }

    public void signinBtnClick() {
        client.sendMessage("/auth " + loginField.getText() + " " + passField.getText());
    }
}