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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class CopClientUtils {

	private CopClientUtils() {
	}

	/**
	 * @see org.apache.http.message.BasicNameValuePair
	 */
	public static List<NameValuePair> paramToPair(Map<String, List<String>> params) {
		List<NameValuePair> result = Collections.emptyList();
		if (params != null && !params.isEmpty()) {
			result = new ArrayList<>(params.size());
			for (Map.Entry<String, List<String>> entry : params.entrySet()) {
				List<String> values = entry.getValue();
				if (values != null && !values.isEmpty()) {
					String key = entry.getKey();
					for (String value : values) {
						result.add(new NameValuePair(key, value));
					}
				}
			}
		}
		return result;
	}

	public static String buildGetUrl(String url, Map<String, List<String>> params, String encoding)
			throws UnsupportedEncodingException {
		if (params == null || params.isEmpty()) {
			return url;
		}
		List<NameValuePair> nameValues = paramToPair(params);
		StringBuilder urlBuilder = new StringBuilder();
		if (!url.contains("?")) {
			urlBuilder.append(url);
			urlBuilder.append("?");
		} else {
			urlBuilder.append(url);
			if(!url.endsWith("?")&&!url.endsWith("&")) {
				urlBuilder.append("&");
			}
		}
		
		for (NameValuePair node : nameValues) {
			urlBuilder.append(URLEncoder.encode(node.getName(), "utf-8")).append("=").append(URLEncoder.encode(node.getValue(), "utf-8")).append("&");
		}
		return url;
	}
}
