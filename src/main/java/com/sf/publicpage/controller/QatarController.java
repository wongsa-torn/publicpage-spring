package com.sf.publicpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;

import com.sf.publicpage.model.ItemQatar;
import com.sf.publicpage.service.QatarService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.HashMap;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// @Controller
// @RequestMapping("/qatar")
// public class QatarController {

//     @Autowired
//     private QatarService qatarService;

//     @GetMapping("")
//     public String showQatarPage(@RequestParam(defaultValue = "DMK") String airportCode, Model model) {
//         List<ItemQatar> list = qatarService.getDataQatar();
//         model.addAttribute("qatars", list);
//           stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
//     stats.put("count", qatars.size());
//         return "qatar/qatar";
//     }
// }

@Controller
@RequestMapping("/qatar")
public class QatarController {

    @Autowired
    private QatarService qatarService;

    // แสดงหน้าเว็บหลัก
    @GetMapping("")
    public String showQatarPage(@RequestParam(defaultValue = "DMK") String airportCode, Model model) {
        List<ItemQatar> list = qatarService.getDataQatar();
        model.addAttribute("qatars", list);
        return "qatar/qatar";
    }

    // ✅ Endpoint ใหม่ สำหรับ fetch timestamp + count
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getQatarStats() {
        List<ItemQatar> qatars = qatarService.getDataQatar();
        Map<String, Object> stats = new HashMap<>();
        stats.put("datestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        stats.put("count", qatars.size());
        return stats;
    }
}