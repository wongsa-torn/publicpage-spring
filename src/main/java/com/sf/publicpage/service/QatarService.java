package com.sf.publicpage.service;

import com.sf.publicpage.model.ItemQatar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import java.text.ParseException;

@Service
public class QatarService {

    private final RestTemplate restTemplate;

    // ใช้ constructor injection เพื่อรับ RestTemplate
    public QatarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int counter = 0;
    public int minrecords = 10;

    public List<ItemQatar> getDataQatar() {
        List<ItemQatar> list = new ArrayList<>();
        try {
            SimpleDateFormat d1 = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat d2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            String filename = String.format(
                    "http://cedafid:8099/WebSocketHome/DownloadFile?filename=datafile/BKKDEPARTURE.txt");
            String DEPDATA = getURLText(filename);
            // System.out.println("DEPDATA: " + DEPDATA);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(DEPDATA);
            JsonNode data = root.path("DATA");
            counter = 0;

            for (JsonNode depdata : data) {
                String FTYP = depdata.path("FTYP").asText();

                String rawSOBT = depdata.path("SOBT").asText();
                String SOBT = rawSOBT.trim(); // อย่าใช้ replace("\"", "") ถ้า asText() ถูกใช้แล้ว
                // System.out.println("RAW SOBT: " + depdata.get("SOBT"));
                // System.out.println("CLEAN SOBT: " + SOBT);
                if (!SOBT.isEmpty()) {
                    try {
                        Date SOBTDate = d1.parse(SOBT);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(SOBTDate);
                        cal.add(Calendar.HOUR, 7);
                        String newTime = d2.format(cal.getTime());
                        SOBT = newTime;
                    } catch (ParseException e) {
                        System.err.println("❌ Parse ไม่ได้: '" + SOBT + "'");
                        continue; // ข้าม record นี้ไป
                    }
                }
                String MAINFLNO = depdata.path("MAINFLNO").asText();

                String FLNO = getFLNOByAirline(depdata.get("ALLFLNO") + "", "QR");

                String estimateTime = depdata.path("EOBT").asText().trim();
                if (!estimateTime.trim().equals("")) {
                    Date estDate = d1.parse(estimateTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(estDate);
                    cal.add(Calendar.HOUR, 7);
                    cal.getTime();
                    String newTime = d2.format(cal.getTime());
                    estimateTime = newTime;
                }

                // boolean cancelrolloff = FTYP.equals("X");
                boolean cancelrolloff = false;
                if (FTYP.equals("X") && !CheckUTC10mins(SOBT)) {
                    cancelrolloff = true;
                }

                boolean mainflight = MAINFLNO.equals("1");

                if (!cancelrolloff && mainflight && FLNO != null) {
                    ItemQatar obj = new ItemQatar();
                    // ส่งรหัสสายการบินแทนรูป

                    obj.setFlight(depdata.path("FLNO").asText());
                    obj.setFlightTo(depdata.path("CITYNAMEALL").asText());
                    obj.setGate(depdata.get("GATE").asText());
                    obj.setStatus(depdata.path("REMGATE").asText());
                    obj.setTimeDepart(SOBT);
                    obj.setEstimate(estimateTime);

                    // เพิ่มการตั้งค่า row
                    list.add(obj);
                    if (counter++ >= 120)
                        break;
                    System.out.println("list: " + list);

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

            e.printStackTrace();
            return true;
        }
    }

    public static String getFLNOByAirline(String input, String Airline) {
        String[] output = input.split("\\|");
        for (int i = 0; i < output.length; i++) {
            String FLNO = output[i];
            if (FLNO.contains(Airline)) {
                return FLNO;
            }
        }
        return null;
    }
}
