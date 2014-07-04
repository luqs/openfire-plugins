package com.skyseas.openfireplugins.userintegration;

/**
 * 用户注册订阅器，当openfire用户成功注册之后会将注册的账户信息发布到订阅器上。
 */
public interface RegisterSubscriber {

    /**
     * 发布注册用户信息到订阅器上。
     * @param user 注册成功的账户信息。
     */
    void publish(RegisterUser user);
}
