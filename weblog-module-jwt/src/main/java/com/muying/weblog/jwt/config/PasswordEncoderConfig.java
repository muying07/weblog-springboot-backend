package com.muying.weblog.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 是一种安全且适合密码存储的哈希算法，它在进行哈希时会自动加入“盐”，增加密码的安全性。
        // 它的特点是每次生成的哈希值都不同，即使密码相同，也不会产生相同的哈希值。
        // 这种特性使得密码存储更加安全，即使数据库被泄露，也无法通过哈希值反推出原始密码。
        // 因此，BCrypt 是一种常用的密码哈希算法，广泛应用于密码存储和验证场景。
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("test123."));
    }
}
