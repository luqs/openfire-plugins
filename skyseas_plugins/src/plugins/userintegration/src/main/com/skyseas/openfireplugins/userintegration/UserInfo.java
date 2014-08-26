package com.skyseas.openfireplugins.userintegration;

/**
 * 用户信息。
 */
public final class UserInfo {
    private final String userName;
    private final String password;
    private final String email;
    private final String name;

    public UserInfo(String username, String password, String email, String name) {
        this.userName 	= username;
        this.password 	= password;  
        this.email 		= email;
        this.name 		= name;
    }

    public String getUserName() {
        return userName;
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
