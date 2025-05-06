package com.sf.publicpage;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// กำหนดให้ `config-app.properties` อยู่ใน classpath ของโปรเจกต์
@PropertySource("classpath:config-app.properties")
// กำหนดให้ `config-common.properties` อยู่ในตำแหน่งภายนอกโปรเจกต์
@PropertySource("file:/D:/config/config-common.properties")
public class ExternalConfigLoader {
    // ไม่มี method ก็ได้ Spring จะโหลด properties เข้ามาให้เอง
}