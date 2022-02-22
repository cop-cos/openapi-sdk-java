/*
 * Copyright (c) 1998-2022 COSCO Shipping Lines CO., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.coscon.cop.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.SignAlgorithm;

/**
 * A Pure Excutor to perform HTTP hmac digest & encryption, used to protect COP
 * secret key over http(s) transportation.
 * <p>
 * Client application can construct an {@link HttpRequestInterceptor}, add
 * #buildHmacHeaders results as HTTP headers, and invoke
 * {@link HttpClientBuilder}.addInterceptorFirst to secure the HTTP operation in
 * background.
 * <p>
 * 
 * @author 陈吉鹏
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 * @emailto ch_jp@msn.com
 */
public abstract class HmacPureExecutor {
	public static final String X_DATE = "X-Coscon-Date";
	public static final String X_CONTENT_MD5 = "X-Coscon-Content-Md5";
	public static final String X_AUTHORIZATION = "X-Coscon-Authorization";
	public static final String X_DIGEST = "X-Coscon-Digest";

	public static final String REQUEST_LINE = "request-line";

	protected static final String[] HMAC_ADVISOR_KEYS = { X_DATE, X_CONTENT_MD5, X_AUTHORIZATION, X_DIGEST };

	/**
	 * Available HMAC algorithms are: <b>hmac-sha1</b>, <b>hmac-sha256</b>,
	 * <b>hmac-sha384</b>, <b>hmac-sha512</b>.
	 * <p>
	 * For hashing purpose, we choose the minimum cost algorithm 'hmac-sha1'.
	 */
	private static final SignAlgorithm DEFAULT_ALGORITHM = SignAlgorithm.HMAC_SHA1;
	private static final String X_COSCON_HMAC_HEADER = "X-Coscon-Hmac";

	/**
	 * @return the hmacAlgorithm
	 */
	public abstract SignAlgorithm getHmacAlgorithm() ;
	
	protected abstract String getApiKey() ;
	protected abstract String getSecretKey();

	/**
	 * date formatter follows API Gateway rule. RFC822
	 */
	private static final FastDateFormat X_DATE_FORMATTER = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss z",
			TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

	/**
	 * Internal representation of HmacAlgorithm
	 * 
	 * @param target
	 *            {@link SignAlgorithm}
	 * @return internal representation which sent to API Gateway.
	 */
	protected String getInternalHmacAlgorithm() {
		return getHmacAlgorithm().getRef();

	}

	/**
	 * Builds HMAC authentication key-value pairs.
	 * <p>
	 * To secure the COP transporation, the hmack key-value pairs shoud be added
	 * as HTTP Headers.
	 * 
	 * @param requestLine
	 * @param httpContent
	 *            content before perform content gzip/deflate.
	 * @return hmac k/v pairs.
	 * @throws ClientException
	 */
	public Map<String, String> buildHmacHeaders(String requestLine, byte[] httpContent) throws ClientException {
		StringBuilder buf = new StringBuilder();
		String guid = UUID.randomUUID().toString();
		String date = "";
		String encodedSignature = "";
		String hmacAuth = "";
		String digest = "";
		String guidMd5 = DigestUtils.md5Hex(guid);

		date = X_DATE_FORMATTER.format(new Date());
		
		/**
		 * MUST be 'SHA-256' encryption.
		 */
		digest = "SHA-256=" + Base64.encodeBase64String(DigestUtils.sha256(httpContent));

		buf.setLength(0);
		buf.append(X_DATE).append(": ").append(date);
		buf.append("\n").append(X_DIGEST).append(": ").append(digest);
		buf.append("\n").append(X_CONTENT_MD5).append(": ").append(guidMd5);
		buf.append("\n").append(requestLine);
		encodedSignature = Base64.encodeBase64String(
				HmacShaUtil.signature(getHmacAlgorithm().getAlgorithm(), buf.toString(), getSecretKey()));
		buf.setLength(0);
		buf.append("hmac username=\"").append(getApiKey())
				.append("\",algorithm=\"" + getInternalHmacAlgorithm() + "\",headers=\"").append(X_DATE).append(" ")
				.append(X_DIGEST).append(" ").append(X_CONTENT_MD5).append(" ").append(REQUEST_LINE)
				.append("\",signature=\"").append(encodedSignature).append("\"");

		hmacAuth = buf.toString();

		Map<String, String> headers = new HashMap<>();
		headers.put(X_DATE, date);
		headers.put(X_DIGEST, digest);
		headers.put(X_CONTENT_MD5, guidMd5);
		headers.put(X_AUTHORIZATION, hmacAuth);
		headers.put(X_COSCON_HMAC_HEADER, guidMd5);
		return headers;
	}
}
