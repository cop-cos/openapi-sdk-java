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
package com.coscon.cop.httpclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.Namespace;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class CopClientInApacheHttpTest2 {

	private CopClient copClient = CopClient.newInstance();
	private Namespace ns = Namespace.COP_PUBLIC_PP;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		copClient.withCredentials(ns, "FakeApiKey", "FakeSecretKey");
		copClient.buildHttpClient();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		copClient.close();
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#buildHttpClient()}.
	 */
	@Test
	public void testBuildHttpClient() {
		assertThrows(ClientException.class, () -> copClient.buildHttpClient());
	}

	/**
	 * Test method for {@link com.coscon.cop.httpclient.CopClient#close()}.
	 */
	@Test
	public void testClose() {
		copClient.close();
		copClient.close();
		assertThrows(NullPointerException.class, () -> copClient.getHttpClientBuilder());
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doGet(com.coscon.cop.core.Namespace, java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testDoGetNamespaceStringMapOfStringListOfString() {
		
		assertThrows(ClientException.class, ()->copClient.doGet(ns, "/info/tracking/6309441170?numberType=bl"));
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doPost(com.coscon.cop.core.Namespace, java.lang.String, java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testDoPostNamespaceStringStringMapOfStringListOfString() {
		String payload = "{" + "  \"cargoCategory\": \"REEFER\", " + "  \"startDate\": \"2021-06-01T00:00:00.000Z\","
				+ "  \"endDate\": \"2021-07-30T00:00:00.000Z\"," + "  \"fndCityId\": \"738872886233842\","
				+ "  \"porCityId\": \"738872886232873\"," + "  \"page\": 1," + "  \"size\": 20" + "}";

		assertThrows(ClientException.class, () -> copClient.doPost(ns, "/synconhub/product/reefer/search", payload));
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#withCredentials(com.coscon.cop.core.Namespace, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testWithCredentialsNamespaceStringString() {
		assertThrows(ClientException.class,
				() -> copClient.withCredentials(ns, System.getenv("cop.pp.apiKey"), System.getenv("cop.pp.secretKey")));
		try {
			copClient.withCredentials(Namespace.COP_INTERNAL_PROD, "hello", "world");
		} catch (ClientException e) {
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#getHttpClientBuilder()}.
	 */
	@Test
	public void testGetHttpClientBuilder() {
		assertNotNull(copClient.getHttpClientBuilder());
		copClient.close();
		assertThrows(NullPointerException.class, () -> copClient.getHttpClientBuilder());
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doGetWithResponse(com.coscon.cop.core.Namespace, java.lang.String, java.util.Map)}.
	 * 
	 * @throws ClientException
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testDoGetWithResponseNamespaceStringMapOfStringListOfString()
			throws ClientException, ParseException, IOException {

		Map<String, List<String>> headers = new HashMap<>();
		headers.put("x-consumer-custom-id", Arrays.asList(this.getClass().getName(), "x-consumer-custom-id"));
		headers.put("x-consumer-username", Arrays.asList(this.getClass().getName(), "x-consumer-username"));
		headers.put("x-coscon-digest", Arrays.asList(this.getClass().getName(), "x-consumer-digest"));
		headers.put("x-coscon-CopClientTest", Arrays.asList(this.getClass().getName(), "CopClientTest02"));
		headers.put("x-real-ip", Arrays.asList(this.getClass().getName(), "x-real-ip"));
		headers.put("x-forwarded-for", Arrays.asList(this.getClass().getName(), "x-forwarded-for"));
		HttpResponse response = copClient.doGetWithResponse(ns, "/info/tracking/6309441170?numberType=bl", headers);

		MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.anyOf(CoreMatchers.is(401),CoreMatchers.is(500)));
	}

	/**
	 * Test method for
	 * {@link com.coscon.cop.httpclient.CopClient#doPostWithResponse(com.coscon.cop.core.Namespace, java.lang.String, java.lang.String, java.util.Map)}.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testDoPostWithResponseNamespaceStringStringMapOfStringListOfString()
			throws ParseException, IOException {
		try {
			String payload = "{" + "  \"cargoCategory\": \"REEFER\", "
					+ "  \"startDate\": \"2021-06-01T00:00:00.000Z\"," + "  \"endDate\": \"2021-07-30T00:00:00.000Z\","
					+ "  \"fndCityId\": \"738872886233842\"," + "  \"porCityId\": \"738872886232873\","
					+ "  \"page\": 1," + "  \"size\": 20" + "}";
			Map<String, List<String>> headers = new HashMap<>();
			headers.put("x-consumer-custom-id", Arrays.asList(this.getClass().getName(), "x-consumer-custom-id"));
			headers.put("x-consumer-username", Arrays.asList(this.getClass().getName(), "x-consumer-username"));
			headers.put("x-coscon-digest", Arrays.asList(this.getClass().getName(), "x-consumer-digest"));
			headers.put("x-coscon-CopClientTest", Arrays.asList(this.getClass().getName(), "CopClientTest02"));
			headers.put("x-real-ip", Arrays.asList(this.getClass().getName(), "x-real-ip"));
			headers.put("x-forwarded-for", Arrays.asList(this.getClass().getName(), "x-forwarded-for"));
			HttpResponse response = copClient.doPostWithResponse(ns, "/synconhub/product/reefer/search", payload,
					headers);
			assertEquals(401, response.getStatusLine().getStatusCode());
		} catch (ClientException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
