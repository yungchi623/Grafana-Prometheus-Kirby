package com.example.kirby.ip.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.kirby.model.util.Auth;
import com.example.kirby.model.util.SHAUtils;

public class AuthSercurityInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthSercurityInterceptor.class);
	// @Value("${auth.secret.info:12345}")
	private String secret = "ilove4gamers";
	// @Value("${auth.need:true}")
	private Boolean needAuth = true;

	// HEADER Authorization
	private static final String AUTH_HEADER = "Authorization";

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Boolean getNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(Boolean needAuth) {
		this.needAuth = needAuth;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (needAuth == null || !needAuth) {
			return true;
		}
		if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			return true;
		}
		Auth auth = ((HandlerMethod) handler).getMethodAnnotation(Auth.class);
		if (auth == null || !auth.validate()) {
			return true;
		}

		String authorization = request.getHeader(AUTH_HEADER);
		// LOGGER.info("authorization is :" + authorization);
		String[] info = authorization.trim().split(",");
		if (info == null || info.length < 1) {
			throw new AuthenticationException("error .....");
		}
		String timestamp = null;
		String sign = null;
		for (int i = 0; i < info.length; i++) {
			String str = info[i].trim();
			if (StringUtils.isEmpty(str)) {
				continue;
			}
			String[] strSplit = str.split("-");
			if (strSplit == null || strSplit.length != 2) {
				// continue;
				throw new AuthenticationException("split error...");
			}
			timestamp = strSplit[0];
			sign = strSplit[1];
		}

		LocalDateTime nowDateTime = LocalDateTime.now(ZoneOffset.UTC);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		LocalDateTime inputDatetime = LocalDateTime.parse(timestamp, dateTimeFormatter);

		if (ChronoUnit.MINUTES.between(inputDatetime, nowDateTime) > 10) {
			// LOGGER.info("compare is :" + ChronoUnit.MINUTES.between(inputDatetime,
			// nowDateTime));
			throw new AuthenticationException("timestamp is invaild ...");
		}

		if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(sign)) {
			throw new AuthenticationException("error timestamp or sign is null");
		}
		String sha256Str = SHAUtils.getSHA256Str(timestamp + secret);
		if (StringUtils.isEmpty(sha256Str)) {
			throw new Exception("sha256Str is null ...");
		}
		if (!sha256Str.equals(sign.toUpperCase())) {
			throw new AuthenticationException("sign error...");
		}
		return super.preHandle(request, response, handler);
	}

}
