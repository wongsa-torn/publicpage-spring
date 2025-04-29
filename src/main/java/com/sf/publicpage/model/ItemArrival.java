package com.sf.publicpage.model;

import lombok.Data;
import java.util.List;  // เพิ่มการ import ที่นี่

@Data
public class ItemArrival {
    private String airline;
    // private String checkIn;
    private String flight;
    private String flightFrom;
    // private String gate;
    private String belt;
    private String status;
    private String statusGate;
    private String timeArrival;
    private String estimate;
    private String remark;

    private List<String> jfnoList;  // แก้เป็น List<String> แทน String

    // Getter and Setter
    public List<String> getJfnoList() {
        return jfnoList;
    }

    public void setJfnoList(List<String> jfnoList) {
        this.jfnoList = jfnoList;
    }
    
    // public String getRow() {
    //     return row;
    // }

    // public void setRow(String row) {
    //     this.row = row;
    // }
    
}
