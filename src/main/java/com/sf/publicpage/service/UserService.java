package com.sf.publicpage.service;

import com.sf.publicpage.model.User;
import com.sf.publicpage.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // เมธอดสำหรับดึงข้อมูลผู้ใช้ทั้งหมด
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // เมธอดสำหรับดึงข้อมูลผู้ใช้ตามชื่อผู้ใช้
    // public User getByUsername(String username) {
    // return userRepository.findByUsername(username);
    // }

    // เมธอดสำหรับดึงข้อมูลผู้ใช้ตามชื่อผู้ใช้
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username)); // หรือใช้ข้อผิดพลาดที่คุณต้องการ
    }

    // เมธอดสำหรับบันทึกผู้ใช้ใหม่
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // เมธอดที่ใช้ในการค้นหาผู้ใช้ในการยืนยันตัวตน (ใช้ใน UserDetailsService)
    // @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // User user = userRepository.findByUsername(username);

    // if (user == null) {
    // throw new UsernameNotFoundException("User not found with username: " +
    // username);
    // }

    // // หากพบผู้ใช้ ให้สร้าง UserDetails จากผู้ใช้แล้วส่งกลับไป
    // return org.springframework.security.core.userdetails.User
    // .withUsername(user.getUsername())
    // .password(user.getPassword())
    // .roles(user.getRole()) // สามารถเปลี่ยนเป็น role ที่คุณกำหนด
    // .build();
    // }

    // เมธอดที่ใช้ในการค้นหาผู้ใช้ในการยืนยันตัวตน (ใช้ใน UserDetailsService)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ใช้ Optional และ orElseThrow
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // หากพบผู้ใช้ ให้สร้าง UserDetails จากผู้ใช้แล้วส่งกลับไป
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // สามารถเปลี่ยนเป็น role ที่คุณกำหนด
                .build();
    }

    // ค้นหาข้อมูล user สำหรับแก้ไข
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // ค้นหาข้อมูล user สำหรับลบ
    public void deleteByUsername(String username) {
        // userRepository.deleteById(username); // หรือจะใส่เช็คก่อนก็ได้
        if (userRepository.existsById(username)) {
            userRepository.deleteById(username);
        }
    }
}
