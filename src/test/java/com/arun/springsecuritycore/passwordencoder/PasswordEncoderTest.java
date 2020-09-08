package com.arun.springsecuritycore.passwordencoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author arun on 9/4/20
 */
public class PasswordEncoderTest {

    private static final String PASSWORD = "password";
    final String SALTED = PASSWORD + "add an extra layer";

    @Test
    public void hashingExample() {
        String encoderPassword = DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
        System.out.println(encoderPassword);

        String encoderPassword1 = DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
        assertThat(encoderPassword).isEqualTo(encoderPassword1);

        String saltedEncoderPassword = DigestUtils.md5DigestAsHex(SALTED.getBytes());
        String saltedEncoderPassword1 = DigestUtils.md5DigestAsHex(SALTED.getBytes());
        System.out.println("Salted encoded Password : " + saltedEncoderPassword);
        System.out.println("Salted encoded Password : " + saltedEncoderPassword1);
        assertThat(saltedEncoderPassword).isEqualTo(saltedEncoderPassword1);
    }

    @Test
    void ldapExample() {
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        String encode = ldap.encode(PASSWORD);
        String encode1 = ldap.encode(PASSWORD);
        System.out.println(encode);
        System.out.println(encode1);
        //values will be different, but ldap will decrypt to the same value
        assertTrue(ldap.matches(PASSWORD, encode1));
        assertTrue(ldap.matches(PASSWORD, encode));
    }
}