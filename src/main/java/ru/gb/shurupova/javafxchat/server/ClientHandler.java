package ru.gb.shurupova.javafxchat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {
        try {
            // в конструкторе инициализируем поля
            this.authService = authService;
            this.socket = socket; // сервер
            this.server = server; // сокет
            this.in = new DataInputStream(socket.getInputStream()); // поток для чтения сообщений
            this.out = new DataOutputStream(socket.getOutputStream()); // поток для записи сообщений
            new Thread(() -> { // создаем отдельный тред
                try {
                    authenticate(); // перед тем как читать сообщение от пользователя, его надо аутентифицировать по логину и паролю
                    readMessages(); // для чтения сообщений от клиента
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {
                String message = in.readUTF();
                if (message.startsWith("/auth")) {
                    String[] split = message.split("\\p{Blank}+");
                    String login = split[1];
                    String password = split[2];
                    String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (server.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        server.broadcast("Пользователь " + nick + " зашел в чат");
                        server.subscribe(this);
                        break;
                    } else {
                        sendMessage("Неверный логин и пароль");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        sendMessage("/end");
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            server.unsubscribe(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        while (true) { // в бесконечном цике крутимся пока клиент не пришлет "/end"
            try {
                String message = in.readUTF();
                if ("/end".equals(message)) {
                    break; // если присылает "/end" делаем break
                }
                // для всех остальных сообщений вызываем метод broadcast, где рассылаем сообщения всем клиентам
                server.broadcast(nick + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNick() {
        return nick;
    }
}
