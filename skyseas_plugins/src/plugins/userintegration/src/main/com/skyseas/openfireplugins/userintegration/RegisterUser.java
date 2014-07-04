package com.skyseas.openfireplugins.userintegration;

/**
 * ע���û���Ϣ��
 */
public final class RegisterUser {
    private final String username;
    private final String password;
    private final String email;
    private final String name;

    public RegisterUser(String username, String password, String email, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
