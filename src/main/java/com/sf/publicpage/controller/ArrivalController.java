package com.sf.publicpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;

import com.sf.publicpage.model.ItemArrival;
import com.sf.publicpage.service.ArrivalService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/arrival")
public class ArrivalController {

    @Autowired
    private ArrivalService arrivalService;

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
    // public String showArrivalPage(@RequestParam(defaultValue = "DMK") String
    // airportCode, String domintCode,Model model) {
    // List<ItemArrival> list = arrivalService.getDataArrival(airportCode);
    // model.addAttribute("arrivals", list);
    // model.addAttribute("selectedAirport", airportCode);
    // model.addAttribute("selectedDomint", domintCode);
    // return "arrival/arrival";
    // }

    @GetMapping("/arrival_N")
    public String showArrival_DevPage(@RequestParam(defaultValue = "DMK") String airportCode, Model model) {
        List<ItemArrival> list = arrivalService.getDataArrival(airportCode);
        model.addAttribute("arrivals", list);
        model.addAttribute("selectedAirport", airportCode);
        return "arrival/arrival_N";
    }

    @GetMapping("")
    public String showArrivalPage(@RequestParam(defaultValue = "DMK") String airportCode,
            @RequestParam(defaultValue = "ALL") String domintCode,
            Model model) {
        List<ItemArrival> list = arrivalService.getDataArrival(airportCode);

        // ✅ กรองตาม domintCode ถ้าไม่ใช่ ALL
        if (!"ALL".equalsIgnoreCase(domintCode)) {
            list = list.stream()
                    .filter(a -> domintCode.equalsIgnoreCase(a.getDomint()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("arrivals", list);
        model.addAttribute("selectedAirport", airportCode);
        model.addAttribute("selectedDomint", domintCode);
        return "arrival/arrival";
    }
}
