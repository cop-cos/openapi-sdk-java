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
import java.net.Proxy;
import java.time.Duration;

import org.apache.commons.io.IOUtils;

import com.coscon.cop.common.CopClientSDKException;

import okhttp3.Authenticator;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class CopConnection {

	private OkHttpClient.Builder httpClientBuilder;
	private OkHttpClient httpClient;

	private static final String HEADER_REQUEST_ID = "X-Coscon-RequestId";
	private final MediaType defaultMediaType=MediaType.parse("application/json;charset=UTF-8");
	
	public CopConnection(int connTimeout, int readTimeout, int writeTimeout) {
		this.httpClientBuilder = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(connTimeout))
				.readTimeout(Duration.ofSeconds(readTimeout)).writeTimeout(Duration.ofSeconds(writeTimeout));

	}

	/**
	 * @return
	 */
	private Interceptor newSignRequestInterceptor() {
		return new Interceptor() {
			protected Response convertToRepeatableResponse(Response response) throws IOException {
				ResponseBody body = response.body();
				if (body == null) {
					return response;
				}
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					IOUtils.copy(body.byteStream(), baos);
					baos.flush();
					return response.newBuilder().body(ResponseBody.create(baos.toByteArray(), body.contentType()))
							.build();
				}
			}

			@Override
			public Response intercept(Chain chain) throws IOException {
				return chain.proceed(chain.request());
			}
		};
	}

	public void addInterceptor(Interceptor interceptor) {
		this.httpClientBuilder.addInterceptor(interceptor);
	}

	public void addNetworkInterceptor(Interceptor interceptor) {
		this.httpClientBuilder.addNetworkInterceptor(interceptor);
	}

	public void setProxy(Proxy proxy) {
		this.httpClientBuilder.proxy(proxy);
	}

	public void setProxyAuthenticator(Authenticator authenticator) {
		this.httpClientBuilder.proxyAuthenticator(authenticator);
	}

	/**
	 * 
	 * @return
	 */
	public CopConnection ready() {
		this.httpClient = this.httpClientBuilder.build();
		this.httpClientBuilder = null;
		return this;
	}

	public Response doRequest(Request request) throws CopClientSDKException {
		Response response = null;
		try {
			response = this.httpClient.newCall(request).execute();
		} catch (IOException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}
		return response;
	}

	public Response doGetRequest(String url) throws CopClientSDKException {
		Request request = null;
		try {
			request = new Request.Builder().url(url).get().build();
		} catch (IllegalArgumentException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}
		return this.doRequest(request);
	}

	public Response getRequest(String url, Headers headers) throws CopClientSDKException {
		Request request = null;
		try {
			request = new Request.Builder().url(url).headers(headers).get().build();
		} catch (IllegalArgumentException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}

		return this.doRequest(request);
	}

	public Response postRequest(String url, String body) throws CopClientSDKException {
		MediaType contentType = defaultMediaType;
		Request request = null;
		try {
			request = new Request.Builder().url(url).post(RequestBody.create(body, contentType)).build();
		} catch (IllegalArgumentException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}

		return this.doRequest(request);
	}

	public Response postRequest(String url, String body, Headers headers) throws CopClientSDKException {
		MediaType contentType = MediaType.parse(headers.get("Content-Type"));
		if(contentType ==null) {
			contentType = defaultMediaType;
		}
		Request request = null;
		try {
			request = new Request.Builder().url(url).post(RequestBody.create(body, contentType)).headers(headers)
					.build();
		} catch (IllegalArgumentException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}

		return this.doRequest(request);
	}

	public Response postRequest(String url, byte[] body, Headers headers) throws CopClientSDKException {
		MediaType contentType = MediaType.parse(headers.get("Content-Type"));
		if(contentType ==null) {
			contentType = defaultMediaType;
		}
		Request request = null;
		try {
			request = new Request.Builder().url(url).post(RequestBody.create(body, contentType)).headers(headers)
					.build();
		} catch (IllegalArgumentException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), null,
					request.header(HEADER_REQUEST_ID));
		}

		return this.doRequest(request);
	}
}
