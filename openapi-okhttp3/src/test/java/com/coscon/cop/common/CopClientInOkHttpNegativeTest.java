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

import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coscon.cop.common.exception.CopClientSDKException;
import com.coscon.cop.common.setting.ClientSettings;
import com.google.gson.Gson;

/**
 * @author Chen Jipeng
 *
 */
public class CopClientInOkHttpNegativeTest {

	private CommonCopClient copClient = null;
	private Namespace ns = Namespace.COP_PUBLIC_PP;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ClientSettings settings = new ClientSettings();
		settings.setDebugEnabled(true);
		copClient = new CommonCopClient(ns,
				new ApiSecretKeyCredentials(ns, "Hello", "World"),
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
			assertThrows(CopClientSDKException.class,()-> copClient.doGet("/info/tracking/6309441170?numberType=bl"));
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

		/*
		 * Unable to identify user -- from Synconhub service
		 */
		assertThrows(CopClientSDKException.class,
				() -> copClient.doPost("/synconhub/product/reefer/search", payload));
	}
}
