package com.sf.publicpage.service;

import com.sf.publicpage.model.ItemArrival;
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
public class ArrivalService {

    private final RestTemplate restTemplate;

    // ใช้ constructor injection เพื่อรับ RestTemplate
    public ArrivalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int counter = 0;
    public int minrecords = 10;

    public List<ItemArrival> getDataArrival(String airportCode) {
        List<ItemArrival> list = new ArrayList<>();
        try {
            String filename = String.format(
                    "http://cedafid:8099/WebSocketHome/DownloadFile?filename=datafile/%sARRIVAL.txt", airportCode);
            String ARRDATA = getURLText(filename);
            System.out.println("ARRDATA: " + ARRDATA);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(ARRDATA);
            JsonNode data = root.path("DATA");
            counter = 0;

            for (JsonNode arrdata : data) {
                String FTYP = arrdata.path("FTYP").asText();
                String SIBT = arrdata.path("SIBT").asText();
                String MAINFLNO = arrdata.path("MAINFLNO").asText();
                String AIBT = arrdata.path("AIBT") + "";

                boolean timeframeCheck = true; // เงื่อนไขนี้อาจถูกแก้ไขตามต้องการ
                if (counter < minrecords)
                    timeframeCheck = true;

                // boolean cancelrolloff = FTYP.equals("X");
                boolean cancelrolloff = false;
				if(FTYP.equals("X") && !CheckUTC10mins(SIBT)) {
					cancelrolloff=true;
				}

                boolean mainflight = MAINFLNO.equals("1");

                boolean aibtrolloff = false;
                if (AIBT.trim().length() > 0 && !CheckUTC120mins(AIBT)) {
                    aibtrolloff = true;
                }

                if (!cancelrolloff && mainflight && !aibtrolloff && timeframeCheck) {
                    ItemArrival obj = new ItemArrival();
                    // ส่งรหัสสายการบินแทนรูป
                    obj.setAirline(extractAirlineCode(arrdata.path("ALC2").asText()));
                    // obj.setCheckIn(arrdata.path("REMCTR").asText());
                    obj.setFlight(arrdata.path("FLNO").asText());
                    obj.setFlightFrom(arrdata.path("CITYNAMEALL").asText());
                    obj.setRemark(arrdata.get("REMARK").asText());
                    obj.setBelt(arrdata.path("BELT").asText());
                    // obj.setGate(arrdata.path("GATE").asText());
                    obj.setTimeArrival(arrdata.path("STA").asText());
                    obj.setEstimate(arrdata.path("ETA").asText());
                    
                    obj.setDomint(arrdata.path("DOMINT").asText());
                    
                    // ตรวจสอบค่าของ jfno
                    String JFNO = arrdata.path("JFNO").asText();
                    if (!JFNO.isEmpty()) {
                        obj.setJfnoList(extractJfnoList(arrdata.path("JFNO").asText()));
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

    public static boolean CheckUTC10mins(String UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        
        SimpleDateFormat sp = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar t = Calendar.getInstance();
        try {
            t.setTime(sp.parse(UTCTime));
            t.add(Calendar.HOUR, 7);
            if(t.before(cal)) {//Old Flight
                return false;
            }else {
                return true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return true;
        }
    }
    
    public static boolean CheckUTC120mins(String UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -120);

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
