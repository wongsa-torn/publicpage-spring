package com.sf.publicpage;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties")  // ดึงจาก module ของ Wildfly ที่คุณตั้งไว้
public class ExternalConfigLoader {
    // ไม่มี method ก็ได้ Spring จะโหลด properties เข้ามาให้เอง
}