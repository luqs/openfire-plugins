package com.skyseas.openfireplugins.userintegration;

/**
 * 注册用户信息。
 */
public final class UserInfo {
    private final String username;
    private final String password;
    private final String email;
    private final String name;

    public UserInfo(String username, String password, String email, String name) {
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
