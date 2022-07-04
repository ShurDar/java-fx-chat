package ru.gb.shurupova.javafxchat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    private static class UserDate {
        private String nick;
        private String login;
        private String password;

        public UserDate(String nick, String login, String password) {
            this.nick = nick;
            this.login = login;
            this.password = password;
        }

        public String getNick() {
            return nick;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

    private List<UserDate> users;

    public InMemoryAuthService() { // добавили сервис аутентификации, кот содержит список пользоватетелй users
        // этот метод умеет бегать по спику пользователей и ис кать по логину и паролю ник нужного пользователя
        users = new ArrayList<>();
        // мы список users сформировали сами циклом
        for (int i = 0; i < 5; i++) {
            users.add(new UserDate("nick" + i, "login" + i, "password" + i));
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (UserDate user : users) {
            if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
                return user.getNick();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Сервис аутентификации остановлен");
    }
}
