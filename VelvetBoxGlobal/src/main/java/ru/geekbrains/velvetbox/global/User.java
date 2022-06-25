package ru.geekbrains.velvetbox.global;

import java.util.Base64;

public class User implements GlobalMessagingService {
    private String name;
    private String login;
    private String password;

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = EncryptPassword(password);
    }

    public User(String login, String password) {
        this.login = login;
        this.password = EncryptPassword(password);
    }

    private String EncryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private String DecryptPassword(String password) {
        byte[] decodedBytes = Base64.getDecoder().decode(password);
        return new String(decodedBytes);
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDecryptedPassword() {
        return DecryptPassword(password);
    }
}
