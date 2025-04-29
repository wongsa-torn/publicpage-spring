package com.sf.publicpage.controller;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sf.publicpage.model.User;
import com.sf.publicpage.service.UserService;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
@RequestMapping("/management")
public class ManagementController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(ManagementController.class);

    public ManagementController(UserService userService) {
        this.userService = userService;
    }

    // @Autowired
    // private HttpServletRequest request;

    // @GetMapping
    // public String getManagementPage(Model model) {
    // // ดึงข้อมูลผู้ใช้จาก service
    // List<User> users = userService.getAllUsers();
    // model.addAttribute("users", users); // ส่งข้อมูลไปที่หน้า
    // return "management/management"; // แสดงผลใน template ของ management
    // }

    @GetMapping // หรือ @GetMapping("")
    public String getManagementPage(Model model, HttpServletRequest request) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        // ✅ ดึง CSRF token จาก request
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "management/management";
    }

    //
    // 1 start-------------- Add New User
    // แสดงฟอร์มเพิ่มข้อมูลใน Modal
    @GetMapping("/add")
    public String showAddForm(Model model) {
        User newUser = new User();
        model.addAttribute("user", newUser);
        model.addAttribute("hopoList", new ArrayList<String>());
        return "management/user-form :: userForm"; // <<< ดึงเฉพาะ fragment
    }

    // เพิ่มผู้ใช้
    @PostMapping("/save")
    public String saveUser(@RequestParam("hopoValues") List<String> hopoValues, @ModelAttribute User user) {
        // รวมค่า checkbox ด้วย ":" เช่น BKK:DMK:HKT
        String hopoCombined = String.join(":", hopoValues);
        user.setHopo(hopoCombined); // สมมุติว่า user.hopo เป็น String
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // กำหนดค่าเริ่มต้น
        }
        userService.saveUser(user);
        return "redirect:/management";
    }
    // 1 end-----------------

    // 2 start--------------- Edit User
    // แสดงฟอร์มแก้ไขข้อมูลใน Modal
    @GetMapping("/edit/{username}")
    public String showEditForm(@PathVariable String username, Model model) {
        User existingUser = userService.getByUsername(username);
        String hopo = existingUser.getHopo();
        List<String> hopoList = hopo != null ? Arrays.asList(hopo.split(":")) : new ArrayList<>();
        model.addAttribute("user", existingUser);
        model.addAttribute("hopoList", hopoList); // ส่ง hopoList ที่แยกมา
        return "management/user-form :: userForm"; // 👈 fragment เท่านั้น
    }

    // อัปเดตผู้ใช้
    // @PostMapping("/update")
    // public String updateUser(@ModelAttribute User user) {
    // userService.saveUser(user); // คำสั่งในการอัปเดต
    // return "redirect:/management"; // ไปยังหน้าหลักของผู้ใช้
    // }
    @PostMapping("/update")
    public String updateUser(@RequestParam(value = "hopoValues", required = false) List<String> hopoValues,
            @ModelAttribute User user) {
        if (hopoValues != null && !hopoValues.isEmpty()) {
            user.setHopo(String.join(":", hopoValues)); // รวมค่า checkbox
        } else {
            user.setHopo(""); // หรือ null แล้วแต่ต้องการ
        }

        userService.saveUser(user);
        return "redirect:/management";
    }
    // 2 end--------------------

    // 3 start------------------ Delete User
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        log.info("Deleting user with username: " + username);
        userService.deleteByUsername(username); // 🔄 เปลี่ยนมาใช้ service
        return ResponseEntity.ok().build();
    }
    // 3 end-------------------------

}