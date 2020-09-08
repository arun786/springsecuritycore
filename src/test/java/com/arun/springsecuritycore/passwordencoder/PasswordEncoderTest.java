package com.arun.springsecuritycore.passwordencoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
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


        String ldapstudent = ldap.encode("ldapstudent");
        System.out.println(ldapstudent); //{SSHA}v5nAsGzRuA+PWyveT2jH7TqU8eNMNBZNYv7PFA==

        String ldapadmin = ldap.encode("ldapadmin");
        System.out.println(ldapadmin); //{SSHA}81/VhsnqiDlulryB5ag3K6vvEZZeT2iAxPG5Pg==
    }

    @Test
    void testSha256() {
        PasswordEncoder sha256 = new StandardPasswordEncoder();
        String encode = sha256.encode(PASSWORD);
        System.out.println(encode);

        String encode1 = sha256.encode(PASSWORD);
        System.out.println(encode1);

        assertTrue(sha256.matches(PASSWORD, encode1));
        assertTrue(sha256.matches(PASSWORD, encode));


        //Everytime it will generate a new value
        String encodeStudentApp = sha256.encode("student");
        System.out.println(encodeStudentApp); //67df0c8329a0bfc0650395cadfbd88437a98272e9d5fcdb6da4f37ad6ddbd2d21d1864bacea8f45e
        String encodeAdminApp = sha256.encode("admin");
        System.out.println(encodeAdminApp);//e956ed1382e539dbf4e6a5c0309eb8fc4bb1dcaa71c819af19e8bdae87b1d77af141a0538dd09881
    }

    @Test
    void passwordEncoder() {
        PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
        String student = passwordEncoder.encode("student");
        System.out.println(student);
    }

    @Test
    void bEncryptEncoder() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String student = passwordEncoder.encode("student");
        System.out.println(student); //$2a$10$DAZWSXKXiWJcAibCS8.CguwekNABqhTwT8exLy8Z//MZZAJVSakuW
        String admin = passwordEncoder.encode("admin");
        System.out.println(admin);//$2a$10$iofIqijAEgQcFpwjgvGdgO1iRgjvV6gTXHWqyWGz.UtFzwoYTNPj.
    }
}
