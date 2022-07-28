package com.example.kirby.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHAUtils {
	public static final String ENCODE_TYPE_HMAC_SHA_256 = "HmacSHA256";
	public static final String ENCODE_UTF_8_LOWER = "utf-8";
	public static final String ENCODE_UTF_8_UPPER = "UTF-8";
	private static final Logger LOGGER = LoggerFactory.getLogger(SHAUtils.class);

	private static final String strType = "SHA-256";

	public static String getSHA256Str(final String strText) {
		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				String SHA256 = byteToHex(messageDigest.digest());
				return SHA256.toUpperCase();

			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	 * public static String getSHA256Str(String secret, String message) throws
	 * Exception { if (StringUtils.isEmpty(secret)) { return null; }
	 * LOGGER.info("secret: " + secret + " , message: " + message); String
	 * encodeStr; try { // HMAC_SHA256 加密 Mac HMAC_SHA256 =
	 * Mac.getInstance(ENCODE_TYPE_HMAC_SHA_256); SecretKeySpec secre_spec = new
	 * SecretKeySpec(secret.getBytes(ENCODE_UTF_8_UPPER), ENCODE_TYPE_HMAC_SHA_256);
	 * HMAC_SHA256.init(secre_spec); byte[] bytes =
	 * HMAC_SHA256.doFinal(message.getBytes(ENCODE_UTF_8_UPPER)); if (bytes == null
	 * && bytes.length < 1) { return null; } LOGGER.info("bytes is :" + bytes); //
	 * 字节转换为16进制字符串 String SHA256 = byteToHex(bytes); if
	 * (StringUtils.isEmpty(SHA256)) { return null; } LOGGER.info("SHA256 is :" +
	 * SHA256); // base64 String BASE64 =
	 * Base64.getEncoder().encodeToString(SHA256.getBytes(ENCODE_UTF_8_UPPER));
	 * LOGGER.info("BASE64 is :" + BASE64); if (StringUtils.isEmpty(BASE64)) {
	 * return null; }
	 * 
	 * // url encode encodeStr = URLEncoder.encode(BASE64, ENCODE_UTF_8_LOWER); }
	 * catch (Exception e) { throw new Exception("get 256 info error ...."); }
	 * return encodeStr; }
	 */

	private static String byteToHex(byte[] bytes) {

		if (bytes == null) {
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xff);
			if (temp.length() == 1) {
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
}
