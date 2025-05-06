package com.sf.publicpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;

import com.sf.publicpage.model.ItemArrival;
import com.sf.publicpage.model.ItemDeparture;
import com.sf.publicpage.service.DepartureService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/departure")
public class DepartureController {

    @Autowired
    private DepartureService departureService;

    // @GetMapping("")
    // public String showDeparturePage(Model model) {
    // List<ItemDeparture> list = departureService.getDataDepartureDMK();

    // // Log เพื่อตรวจสอบว่าได้ข้อมูลมาหรือไม่
    // // System.out.println("Data: " + list);

    // model.addAttribute("departures", list);
    // return "departure/departure"; // ชื่อ template เช่น departure.html
    // }

    // @GetMapping("/departure")
    // public String getDepartureFlights(Model model) {
    // // ดึงข้อมูลรายการเที่ยวบินจาก DepartureService
    // List<ItemDeparture> departures = departureService.getDataDepartureDMK();

    // // ตรวจสอบค่า jfnoList ของแต่ละรายการใน departures
    // for (ItemDeparture departure : departures) {
    // System.out.println("jfnoList: " + departure.getJfnoList());
    // }

    // // ส่งข้อมูลไปยัง HTML ผ่าน model
    // model.addAttribute("departures", departures);
    // return "departure/departure"; // ชื่อไฟล์ HTML ที่จะใช้แสดงข้อมูล
    // }

    // @GetMapping("")
    // public String showDeparturePage(@RequestParam(defaultValue = "DMK") String
    // airportCode,
    // @RequestParam(defaultValue = "flight") String type,
    // Model model) {
    // List<ItemDeparture> list = departureService.getDataDeparture(airportCode);
    // model.addAttribute("departures", list);
    // model.addAttribute("selectedAirport", airportCode);
    // model.addAttribute("type", type);
    // return "departure/departure";
    // }

    @GetMapping("")
    public String showDeparturePage(@RequestParam(defaultValue = "DMK") String airportCode,
            @RequestParam(defaultValue = "flight") String type,
            @RequestParam(defaultValue = "ALL") String domintCode,
            Model model) {
        List<ItemDeparture> list = departureService.getDataDeparture(airportCode);

        // ✅ กรองตาม domintCode ถ้าไม่ใช่ ALL
        if (!"ALL".equalsIgnoreCase(domintCode)) {
            list = list.stream()
                    .filter(a -> domintCode.equalsIgnoreCase(a.getDomint()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("departures", list);
        model.addAttribute("selectedAirport", airportCode);
        model.addAttribute("selectedDomint", domintCode);
        return "departure/departure";
    }
}
