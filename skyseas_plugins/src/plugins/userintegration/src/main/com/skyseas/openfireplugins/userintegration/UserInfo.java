package com.skyseas.openfireplugins.userintegration;

/**
 * �û���Ϣ��
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

    public String getUsername() {
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
