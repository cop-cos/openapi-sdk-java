package com.coscon.cop.common;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coscon.cop.common.exception.CopClientSDKException;
import com.coscon.cop.common.exception.CopServerBusinessException;
import com.coscon.cop.common.setting.ClientSettings;
import com.google.gson.Gson;

/**
 * @author Chen Jipeng
 *
 */
public class CopClientInOkHttpPositiveTest {

	private CommonCopClient copClient = null;
	private Namespace ns = Namespace.COP_PUBLIC_PP;
	private Gson gson = new Gson();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ClientSettings settings = new ClientSettings();
		settings.setDebugEnabled(true);
		copClient = new CommonCopClient(ns,
				new ApiSecretKeyCredentials(ns, System.getenv("cop.pp.apiKey"), System.getenv("cop.pp.secretKey")),
				settings);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		copClient = null;
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doGet(com.coscon.cop.common.Namespace, java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testDoGetNamespaceStringMapOfStringListOfString() {
		// Get
		String content;
		try {
			content = copClient.doGet("/info/tracking/6309441170?numberType=bl");
			CommonResponse response = gson.fromJson(content, CommonResponse.class);
			assertEquals(0, response.getCode());
			assertTrue("response should contains 6309441170", content.contains("6309441170"));
			
			Map<String, List<String>> extraHeaders = new HashMap<>();
			extraHeaders.put("X-Coscon-Authorization", Arrays.asList("SHOULD_NOT_SEND_TO_SERVER"));
			extraHeaders.put("X-OK", Arrays.asList("SHOULD_SEND_TO_SERVER"));
			
			content = copClient.doGet("/info/tracking/6309441170?numberType=bl",extraHeaders);
			response = gson.fromJson(content, CommonResponse.class);
			assertEquals(0, response.getCode());
			assertTrue("response should contains 6309441170", content.contains("6309441170"));
		} catch (CopClientSDKException e) {
			fail(e.getMessage());
		} catch (CopServerBusinessException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doPost(com.coscon.cop.common.Namespace, java.lang.String, java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testDoPostNamespaceStringStringMapOfStringListOfString() {
		String payload = "{" + "  \"cargoCategory\": \"REEFER\", " + "  \"startDate\": \"2021-06-01T00:00:00.000Z\","
				+ "  \"endDate\": \"2021-07-30T00:00:00.000Z\"," + "  \"fndCityId\": \"738872886233842\","
				+ "  \"porCityId\": \"738872886232873\"," + "  \"page\": 1," + "  \"size\": 20" + "}";
		Map<String, List<String>> extraHeaders = new HashMap<>();
		extraHeaders.put("X-Coscon-Authorization", Arrays.asList("SHOULD_NOT_SEND_TO_SERVER"));
		extraHeaders.put("X-OK", Arrays.asList("SHOULD_SEND_TO_SERVER"));
		/*
		 * Unable to identify user -- from Synconhub service
		 */
		assertThrows(CopServerBusinessException.class,
				() -> copClient.doPost("/synconhub/product/reefer/search", payload,extraHeaders));

	}
}
