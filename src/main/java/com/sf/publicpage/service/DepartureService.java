package com.sf.publicpage.service;

import com.sf.publicpage.model.ItemDeparture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.text.ParseException;

@Service
public class DepartureService {

    private final RestTemplate restTemplate;

    // ใช้ constructor injection เพื่อรับ RestTemplate
    public DepartureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int counter = 0;
    public int minrecords = 15;
    public static int end = 5;

    // public List<ItemDeparture> getDataDepartureDMK() {
    // List<ItemDeparture> list = new ArrayList<>();
    // try {
    // String DEPDATA = getURLText(
    // "http://cedafid:8099/WebSocketHome/DownloadFile?filename=datafile/BKKDEPARTURE.txt");
    public List<ItemDeparture> getDataDeparture(String airportCode) {
        List<ItemDeparture> list = new ArrayList<>();
        try {
            String filename = String.format(
                    "http://cedafid:8099/WebSocketHome/DownloadFile?filename=datafile/%sDEPARTURE.txt", airportCode);
            String DEPDATA = getURLText(filename);
            System.out.println("DEPDATA: " + DEPDATA);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(DEPDATA);
            JsonNode data = root.path("DATA");
            counter = 0;

            boolean forceAddAll = data.size() < minrecords;

            for (JsonNode depdata : data) {
                String FTYP = depdata.path("FTYP").asText();
                String SOBT = depdata.path("SOBT").asText();
                String MAINFLNO = depdata.path("MAINFLNO").asText();

                // boolean timeframeCheck = true; // เงื่อนไขนี้อาจถูกแก้ไขตามต้องการ
                // if (counter < minrecords)
                // timeframeCheck = true;
                boolean timeframeCheck = CheckUTCEnd(SOBT);
                if (forceAddAll) {
                    timeframeCheck = true;
                }

                boolean cancelrolloff = false;
                // boolean cancelrolloff = FTYP.equals("X");
                if (FTYP.equals("X") && !CheckUTC10mins(SOBT)) {
                    cancelrolloff = true;
                }

                boolean mainflight = MAINFLNO.equals("1");

                if (!cancelrolloff && mainflight && timeframeCheck) {
                    ItemDeparture obj = new ItemDeparture();
                    // ส่งรหัสสายการบินแทนรูป
                    // obj.setAirline(extractAirlineCode(depdata.path("ALLFLNO").asText()));
                    obj.setAirline(extractAirlineCode(depdata.path("ALC2").asText()));

                    obj.setCheckIn(depdata.path("REMCTR").asText());
                    obj.setFlight(depdata.path("FLNO").asText());
                    obj.setFlightTo(depdata.path("CITYNAMEALL").asText());

                    obj.setRemark(depdata.get("REMCTR").asText());
                    obj.setStatusGate(depdata.get("REMGATE").asText());

                    obj.setRow(depdata.path("CHKROW").asText());
                    obj.setGate(depdata.path("GATE").asText());
                    obj.setTimeDepart(depdata.path("STD").asText());
                    obj.setEstimate(depdata.path("ETD").asText());

                    obj.setDomint(depdata.path("DOMINT").asText());

                    // ตรวจสอบค่าของ jfno
                    String JFNO = depdata.path("JFNO").asText();
                    if (!JFNO.isEmpty()) {
                        obj.setJfnoList(extractJfnoList(depdata.path("JFNO").asText()));
                        // obj.setJfnoList(extractJfnoList(depdata.path("ALLFLNO").asText()));

                        System.out.println("JFNO List: " + obj.getJfnoList()); // ตรวจสอบค่า jfnoList
                    } else {
                        obj.setJfnoList(new ArrayList<>());
                    }

                    // เพิ่มการตั้งค่า row
                    list.add(obj);
                    if (counter++ >= 120)
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // เมธอดนี้จะเรียกใช้งาน RestTemplate เพื่อดึงข้อมูลจาก URL
    private String getURLText(String url) {
        return restTemplate.getForObject(url, String.class); // ใช้ restTemplate ที่สร้างไว้ใน constructor
    }

    // เพิ่ม helper method สำหรับแยกรหัสสายการบินจาก flight number
    private String extractAirlineCode(String allFlno) {
        if (allFlno == null || allFlno.isEmpty())
            return null;

        String[] parts = allFlno.split("\\|");
        if (parts.length == 0)
            return null;

        String firstFlno = parts[0].trim();
        String[] airlineParts = firstFlno.split(" ");
        if (airlineParts.length == 0)
            return null;

        return airlineParts[0]; // ส่งแค่รหัสสายการบิน
    }

    // Helper method สำหรับแยก flight numbers จาก JFNO
    // private List<String> extractJfnoList(String jfnoRaw) {
    // if (jfnoRaw == null || jfnoRaw.trim().isEmpty()) return new ArrayList<>();

    // String[] jfnoArray = jfnoRaw.split("\\|");
    // List<String> result = new ArrayList<>();

    // for (String flno : jfnoArray) {
    // if (flno != null && !flno.trim().isEmpty()) {
    // result.add(flno.trim());
    // }
    // }
    // return result;
    // }

    private List<String> extractJfnoList(String jfnoRaw) {
        if (jfnoRaw == null || jfnoRaw.trim().isEmpty())
            return new ArrayList<>();

        String[] jfnoParts = jfnoRaw.trim().split("\\s{2,}");
        List<String> result = new ArrayList<>();

        for (String flno : jfnoParts) {
            if (!flno.trim().isEmpty()) {
                String code = flno.split("\\s+")[0]; // เอาเฉพาะตัวอักษรหน้าเลข เช่น "BR" จาก "BR 3945"
                String imgPath = "/images/image/" + code + "_S.gif";
                result.add("<span class='flex items-center gap-2'>"
                        + "<img src='" + imgPath
                        + "' class='w-[100px] h-[46px] object-cover rounded border border-white'/>"
                        + flno + "</span>");
            }
        }
        return result;
    }

    public static boolean CheckUTCEnd(String UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, end * 60);// 5 Hrs.

        SimpleDateFormat sp = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar t = Calendar.getInstance();
        try {
            t.setTime(sp.parse(UTCTime));
            t.add(Calendar.HOUR, 7);
            if (t.before(cal)) {// Old Flight
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return true;
        }
    }

    public static boolean CheckUTC10mins(String UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);

        SimpleDateFormat sp = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar t = Calendar.getInstance();
        try {
            t.setTime(sp.parse(UTCTime));
            t.add(Calendar.HOUR, 7);
            if (t.before(cal)) {// Old Flight
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return true;
        }
    }

}
