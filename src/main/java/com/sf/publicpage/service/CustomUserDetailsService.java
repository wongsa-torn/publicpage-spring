// encoder

// package dev.wongweb.tailwind.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import javax.sql.DataSource;
// import java.sql.*;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {

//     @Autowired
//     private DataSource dataSource;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         try (
//             Connection conn = dataSource.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(
//                 "SELECT USERNAME, PASSWORD FROM PUBLICPAGE_STAFF WHERE USERNAME = ?"
//             )
//         ) {
//             stmt.setString(1, username);
//             ResultSet rs = stmt.executeQuery();

//             if (rs.next()) {
//                 String dbUsername = rs.getString("USERNAME");
//                 String dbPassword = rs.getString("PASSWORD"); // ควรเป็น encoded (BCrypt)

//                 return User.withUsername(dbUsername)
//                            .password(dbPassword)
//                            .roles("USER") // หรือกำหนดจาก DB เพิ่มเติม
//                            .build();
//             } else {
//                 throw new UsernameNotFoundException("User not found: " + username);
//             }
//         } catch (SQLException e) {
//             throw new RuntimeException(e);
//         }
//     }
// }

// plain text
package com.sf.publicpage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    // @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // try (
    // Connection conn = dataSource.getConnection();
    // PreparedStatement stmt = conn.prepareStatement(
    // "SELECT USERNAME, PASSWORD FROM PUBLICPAGE_STAFF WHERE USERNAME = ?")) {
    // stmt.setString(1, username);
    // ResultSet rs = stmt.executeQuery();

    // System.out.println("LOAD USER: " + username);
    // System.out.println("RESULT = " + rs.getString("USERNAME") + " / " +
    // rs.getString("PASSWORD"));

    // if (rs.next()) {
    // String dbUsername = rs.getString("USERNAME");
    // String dbPassword = rs.getString("PASSWORD");

    // return User.withUsername(dbUsername)
    // .password("{noop}" + dbPassword)
    // .roles("USER")
    // .build();
    // } else {
    // throw new UsernameNotFoundException("User not found: " + username);
    // }
    // } catch (SQLException e) {
    // throw new RuntimeException("Database error", e);
    // }

    // }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PUBLICPAGE_STAFF WHERE USERNAME = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("PASSWORD");
                System.out.println("USER FOUND: " + username + " / " + password);
                return User.withUsername(username)
                        .password(password)
                        .roles("USER") // หรือจะ set authorities จาก DB ก็ได้
                        .build();
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 👈 อันนี้จะช่วยให้เห็น error JDBC
            throw new InternalAuthenticationServiceException("Database error", e);
        }
    }
}
