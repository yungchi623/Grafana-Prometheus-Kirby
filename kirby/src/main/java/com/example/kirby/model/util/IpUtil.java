package com.example.kirby.model.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(IpUtil.class);

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("Cdn-Src-Ip"); // 網宿cdn的真實ip
		if (ip == null || ip.length() == 0 || " unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP"); // 藍訊cdn的真實ip
		}
		if (ip == null || ip.length() == 0 || " unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For"); // 獲取代理ip
		}
		if (ip == null || ip.length() == 0 || " unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP"); // 獲取代理ip
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP"); // 獲取代理ip
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr(); // 獲取真實ip
		}
		return ip;
	}
}