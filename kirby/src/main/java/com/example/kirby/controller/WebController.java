package com.example.kirby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	// @ResponseBody
	@RequestMapping("/public")
	public String GetPublic() {
		return "public";
	}

	@RequestMapping("/deviceinfo")
	public String getDeviceInfo() {
		return "deviceinfo";
	}
}
