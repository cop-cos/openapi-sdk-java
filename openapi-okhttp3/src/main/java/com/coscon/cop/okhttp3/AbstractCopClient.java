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
package com.coscon.cop.okhttp3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.coscon.cop.common.CopClientSDKException;
import com.coscon.cop.common.CopServerSideException;
import com.coscon.cop.core.CommonResponse;
import com.coscon.cop.core.Namespace;
import com.coscon.cop.core.Validator;
import com.coscon.cop.internal.BasicCredentialsProvider;
import com.coscon.cop.internal.BasicValidatorProvider;
import com.coscon.cop.internal.Credentials;
import com.coscon.cop.internal.CredentialsProvider;
import com.coscon.cop.internal.Signer;
import com.coscon.cop.internal.ValidatorProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import okhttp3.Authenticator;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

/**
 * @author Chen Jipeng
 *
 */
public class AbstractCopClient {

	public static final int HTTP_RSP_OK = 200;
	public static final String HEADER_REQUEST_ID = "X-Coscon-RequestId";
	public static final String SDK_VERSION = "COP_CLIENT_SDK_JAVA_0.0.1";
	private Credentials credential;
	private Namespace namespace;
	private String endpoint;
	private String service;
	private String region;
	private String path;
	private String sdkVersion;
	private String apiVersion;
	protected Gson gson;
	private ClientSettings clientSettings;
	private final CopHttpLogInterceptor logger;
	private final Signer signer;
	private BasicCredentialsProvider credentialsProvider;
	private BasicValidatorProvider validatorProvider;

	public AbstractCopClient(Namespace ns, String endpoint, String version, Credentials credential, String region) {
		this(ns,endpoint, version, credential, region, new ClientSettings());
	}

	public AbstractCopClient(Namespace ns, String endpoint, String version, Credentials credential, String region,
			ClientSettings profile) {
		this.namespace = ns;
		this.credential = credential;
		this.clientSettings = profile;
		this.endpoint = endpoint;
		this.service = endpoint.split("\\.")[0];
		this.region = region;
		this.path = "/";
		this.sdkVersion = SDK_VERSION;
		this.apiVersion = version;
		this.gson = new GsonBuilder().create();
		this.logger = new CopHttpLogInterceptor(this.getClass().getName(), this.clientSettings.isDebugEnabled());
		this.signer = new CopClientSigner(getClientSettings().getSignMethod());
		this.credentialsProvider = new BasicCredentialsProvider();
		this.validatorProvider = new BasicValidatorProvider();
		this.credentialsProvider.setCredentials(ns, this.credential);
	}

/**
 * @return the signer
 */
protected Signer getSigner() {
	return signer;
}
	public String call(String jsonPayload, Map<String, List<String>> extraHeaders)
			throws CopClientSDKException, CopServerSideException {

		byte[] requestPayload = jsonPayload.getBytes(StandardCharsets.UTF_8);
		HashMap<String, List<String>> headers = this.getHeaders(extraHeaders, jsonPayload);
		headers.put("Content-Type", Arrays.asList("application/json; charset=utf-8"));

		String url = this.clientSettings.getProtocol() + this.getEndpoint() + this.path;
		return this.getResponseBody(url, headers, requestPayload);
	}

	/**
	 * @return the clientSettings
	 */
	public ClientSettings getClientSettings() {
		return clientSettings;
	}

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	protected HashMap<String, List<String>> getHeaders() {
		return getHeaders(null);
	}

	protected HashMap<String, List<String>> getHeaders(Map<String, List<String>> extraHeaders) {
		return getHeaders(extraHeaders, null);
	}

	protected HashMap<String, List<String>> getHeaders(Map<String, List<String>> extraHeaders, String payload) {

		return null;
	}

	protected void prepareConnection(CopConnection conn) {
		/**
		 * Registers request/response logger
		 */
		conn.addInterceptor(logger);
		/**
		 * Registers cop signer
		 */
		conn.addInterceptor(new AbstractCopAwareInterceptor() {
			protected Response convertToRepeatableResponse(Response response) throws IOException {
				ResponseBody body = response.body();
				if(body == null) {
					return response;
				}
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) { 
						IOUtils.copy(body.byteStream(), baos); 
					baos.flush();
					return response.newBuilder()
							.body(ResponseBody.create(baos.toByteArray(), body.contentType())).build();
				}
			}

			@Override
			protected Response intercept0(Chain chain) throws IOException {
				if (getSigner().acceptRequest(chain.request().url().toString())) {
					Request request;
					try {
						request = (Request) getSigner().sign(getCredentialsProvider(), chain.request());
					} catch (CopClientSDKException e) {
						throw new IOException("COP request sign failed.");
					}
					Response response = chain.proceed(request);
					if (response.code() >= 200 && response.code() < 300) {
						response = convertToRepeatableResponse(response);
						Validator validator = getValidatorProvider().getValidator(request.url().toString());
						if (validator != null && !validator.validate(response)) {
							throw new IOException("COP response validation failed.");
						}
					}
					return response;
				} else {
					return chain.proceed(chain.request());
				}
			}
		});
		trySetupProxy(conn);
	}

	/**
	 * @return
	 */
	protected ValidatorProvider getValidatorProvider() {
		return validatorProvider;
	}

	/**
	 * @return
	 */
	protected CredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	private String getResponseBody(String url, Map<String, List<String>> headers, byte[] body)
			throws CopClientSDKException, CopServerSideException {
		CopConnection conn = new CopConnection(getClientSettings().getConnTimeout(),
				getClientSettings().getReadTimeout(), getClientSettings().getWriteTimeout());
		prepareConnection(conn);
		Builder hb = new Headers.Builder();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			for (String value : entry.getValue()) {
				hb.add(entry.getKey(), value);
			}
		}
		Response resp = conn.postRequest(url, body, hb.build());
		if (resp.code() != HTTP_RSP_OK) {
			String msg = "response code is " + resp.code() + ", not 200";
			throw new CopClientSDKException(msg, "ServerSideError");
		}
		String respbody = null;
		try {
			respbody = resp.body().string();
		} catch (IOException e) {
			String msg = "Cannot transfer response body to string, because Content-Length is too large, or Content-Length and stream length disagree.";
			logger.info(msg);
			throw new CopClientSDKException(msg + " " + e.getClass().getName());
		}
		CommonResponse commonResponse = null;
		try {
			Type errType = new TypeToken<CommonResponse>() {
			}.getType();
			commonResponse = gson.fromJson(respbody, errType);
		} catch (JsonSyntaxException e) {
			String msg = "json is not a valid representation for an object of type";
			logger.info(msg);
			throw new CopClientSDKException(msg, e, resp.header(HEADER_REQUEST_ID), e.getClass().getName());
		}
		if (commonResponse.getCode() != 0) {
			throw new CopServerSideException(commonResponse.getCode(), commonResponse.getMessage());
		}
		return respbody;
	}

	/**
	 * @param conn
	 */
	private void trySetupProxy(CopConnection conn) {
		String host = this.getClientSettings().getProxyHost();
		int port = this.getClientSettings().getProxyPort();

		if (host == null || host.isEmpty()) {
			return;
		}
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
		conn.setProxy(proxy);

		final String username = this.getClientSettings().getProxyUsername();
		final String password = this.getClientSettings().getProxyPassword();
		if (username == null || username.isEmpty()) {
			return;
		}
		conn.setProxyAuthenticator(new Authenticator() {
			@Override
			public Request authenticate(Route route, Response response) throws IOException {
				return response.request().newBuilder()
						.header("Proxy-Authorization", okhttp3.Credentials.basic(username, password)).build();
			}

		});
	}

}
