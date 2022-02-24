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
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.Namespace;
import com.coscon.cop.core.SignAlgorithm;
import com.coscon.cop.core.Signer;
import com.coscon.cop.core.Validator;
import com.coscon.cop.internal.CopClientBase;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class CopClient extends CopClientBase implements Closeable {
	@Override
	public void close() {
		httpClient = null;
		httpClientBuilder = null;
		signer = null;
		super.close();
	}

	public static final String COP_SDK_VERSION = "COP_SDK_Okhttp/0.0.1";
	private OkHttpClient.Builder httpClientBuilder;

	private CopClient() {
		super();
	}

	@Override
	protected void initialize() {
		httpClientBuilder = new OkHttpClient.Builder();
		httpClientBuilder.callTimeout(30, TimeUnit.SECONDS);
		setResponseHandler(defaultResponseHandler);
		setSignMethod(SignAlgorithm.HMAC_SHA1);
	}

	private static final ResponseHandler<String> defaultResponseHandler = new ResponseHandler<String>() {

		@Override
		public String handleResponse(Response response) throws IOException {
			final int status = response.code();
			final String reasonPhrase = response.message();
			if (status >= 200 && status < 300) {
				String content = response.body().string();
				response.close();
				return content;
			} else {
				response.close();
				throw new IOException("http response - status:" + status + ", reason phrase:" + reasonPhrase);
			}
		}
	};

	/**
	 * @return the httpClientBuilder
	 */
	public OkHttpClient.Builder getHttpClientBuilder() {
		return Objects.requireNonNull(this.httpClientBuilder, "httpClientBuilder may not be null");
	}

	public static CopClient newInstance() {
		return new CopClient();
	}

	private OkHttpClient httpClient = null;
	private Signer signer = null;

	@Override
	protected Signer getSigner() {
		Objects.requireNonNull(signer, "signer may not be null");
		return signer;
	}

	@Override
	public void buildHttpClient() throws ClientException {
		if (httpClient == null) {
			List<Protocol> protocols = new ArrayList<>();
			protocols.add(okhttp3.Protocol.HTTP_1_1);
			this.httpClientBuilder = getHttpClientBuilder().protocols(protocols);
			signer = new CopClientSigner(getSignMethod());
			httpClientBuilder.addNetworkInterceptor(newSignRequestInterceptor());
			httpClient = getHttpClientBuilder().build();
		} else {
			throw new ClientException("Unable to overwrite existing executor/httpclient");
		}
	}

	/**
	 * @return the httpClient
	 */
	public OkHttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public CopClient withCredentials(Namespace namespace, String apiKey, String secretKey) throws ClientException {
		return (CopClient) super.withCredentials(namespace, apiKey, secretKey);
	}

	/**
	 * @return
	 */
	private Interceptor newSignRequestInterceptor() {
		return new Interceptor() {
			protected Response convertToRepeatableResponse(Response response) throws IOException {
				try (ResponseBody body = response.body(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					IOUtils.copy(body.byteStream(), baos);
					baos.flush();
					return response.newBuilder()
							.body(ResponseBody.create(baos.toByteArray(), response.body().contentType())).build();
				}
			}

			@Override
			public Response intercept(Chain chain) throws IOException {
				if (getSigner().acceptRequest(chain.request().url().toString())) {
					Request request = (Request) getSigner().sign(getCredentialsProvider(), chain.request());
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
		};
	}

	@Override
	public String doGet(Namespace namespace, String relativeUri) throws ClientException {
		return doGet(namespace, relativeUri, null);
	}

	@Override
	public String doGet(Namespace namespace, String relativeUri, Map<String, List<String>> extraHeaders)
			throws ClientException {
		try {
			Response response = doGetWithResponse(namespace, relativeUri, extraHeaders);
			return getResponseHandler().handleResponse(response);
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Response doGetWithResponse(Namespace namespace, String relativeUri) throws ClientException {
		return doGetWithResponse(namespace, relativeUri, null);
	}

	@Override
	public Response doGetWithResponse(Namespace namespace, String relativeUri, Map<String, List<String>> extraHeaders)
			throws ClientException {
		Request get = new Request.Builder().url(namespace.getPrefix() + relativeUri).get().build();
		get = populateHeaders(get, extraHeaders);
		try {
			return getHttpClient().newCall(get).execute();
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String doPost(Namespace namespace, String relativeUri, String payload) throws ClientException {
		return doPost(namespace, relativeUri, payload, null);
	}

	@Override
	public String doPost(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException {
		try {
			Response response = doPostWithResponse(namespace, relativeUri, payload, extraHeaders);
			return getResponseHandler().handleResponse(response);
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Response doPostWithResponse(Namespace namespace, String relativeUri, String payload) throws ClientException {
		return doPostWithResponse(namespace, relativeUri, payload, null);
	}

	@Override
	public Response doPostWithResponse(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException {
		Request post = new Request.Builder().url(namespace.getPrefix() + relativeUri)
				.post(RequestBody.create(payload, MediaType.parse("application/json"))).build();
		post = populateHeaders(post, extraHeaders);
		try {
			return getHttpClient().newCall(post).execute();
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	protected Request populateHeaders(Request request, Map<String, List<String>> extraHeaders) {
		if (extraHeaders != null && !extraHeaders.isEmpty()) {
			Request.Builder newBuilder = request.newBuilder();
			for (Map.Entry<String, List<String>> headers : extraHeaders.entrySet()) {
				String hName = headers.getKey();
				for (String hValue : headers.getValue()) {
					newBuilder.addHeader(hName, hValue);
				}
			}
			return newBuilder.build();
		} else {
			return request;
		}
	}

	@Override
	public void setResponseHandler(Object handler) {
		if (handler instanceof ResponseHandler) {
			super.setResponseHandler(handler);
		} else {
			Objects.requireNonNull(handler, "handler may not be null");
			throw new IllegalArgumentException("handler may not CallBack intance");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResponseHandler<String> getResponseHandler() {
		return (ResponseHandler<String>) super.getResponseHandler();
	}
}
