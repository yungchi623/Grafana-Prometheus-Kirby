package com.example.kirby.ip.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.kirby.model.util.Auth;
import com.example.kirby.model.util.IpUtil;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class IpAPIs {

	@Auth
	@RequestMapping(value = "/get-public-ip", method = RequestMethod.GET)
	public ResponseEntity<?> getPublicIp(HttpServletRequest request) {
		return ResponseEntity.ok(IpUtil.getIpAddr(request));
	}
}
