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
package com.coscon.cop.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.coscon.cop.common.exception.CopClientSDKException;
import com.coscon.cop.common.exception.CopServerBusinessException;
import com.coscon.cop.common.http.AbstractCopAwareInterceptor;
import com.coscon.cop.common.http.CopClientSigner;
import com.coscon.cop.common.http.CopConnection;
import com.coscon.cop.common.http.CopHttpLogInterceptor;
import com.coscon.cop.common.provider.BasicCredentialsProvider;
import com.coscon.cop.common.provider.BasicValidatorProvider;
import com.coscon.cop.common.provider.CredentialsProvider;
import com.coscon.cop.common.provider.Signer;
import com.coscon.cop.common.provider.Validator;
import com.coscon.cop.common.provider.ValidatorProvider;
import com.coscon.cop.common.setting.ClientSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Chen Jipeng
 *
 */
public class AbstractCopClient {
	public static final MediaType defaultMediaType = MediaType.parse("application/json; charset=UTF-8");
	public static final String SDK_VERSION = "COP_CLIENT_SDK_JAVA_r${revision}";
	private Credentials credential;
	private Namespace namespace;
	private String endpoint;
	private String service;
	protected final Gson gson;
	private final ClientSettings clientSettings;
	private final CopLog logger;
	private final Signer signer;
	private BasicCredentialsProvider credentialsProvider;
	private BasicValidatorProvider validatorProvider;

	public AbstractCopClient(Namespace ns, String endpoint, String version, Credentials credential) {
		this(ns, endpoint, version, credential, new ClientSettings());
	}

	public AbstractCopClient(Namespace ns, String endpoint, String version, Credentials credential,
			ClientSettings clientSettings) {
		Objects.requireNonNull(ns, "namespace may not be null");
		Objects.requireNonNull(credential, "credentials may not be null");
		Objects.requireNonNull(clientSettings, "clientSettings may not be null");
		if (ns != credential.getNamespace()) {
			throw new IllegalArgumentException("namespace between client and credentials may not be same");
		}
		this.namespace = ns;
		this.credential = credential;
		this.clientSettings = clientSettings;
		this.endpoint = endpoint;
		this.service = endpoint.split("\\.")[0];
		this.gson = new GsonBuilder().create();
		this.logger = new CopLog(this.getClass().getName(), this.clientSettings.isDebugEnabled());
		this.signer = new CopClientSigner(this.clientSettings.getSignMethod());
		this.credentialsProvider = new BasicCredentialsProvider();
		this.validatorProvider = new BasicValidatorProvider();
		this.credentialsProvider.setCredentials(ns, this.credential);
		this.copConnection = makeCopConnection();
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @return the signer
	 */
	protected Signer getSigner() {
		return signer;
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

	private CopConnection copConnection;

	/**
	 * Returns a internal delegate {@link CopConnection}
	 * 
	 * @return the copConnection
	 */
	protected CopConnection getCopConnection() {
		return this.copConnection;
	}

	private CopConnection makeCopConnection() {
		CopConnection conn = new CopConnection(getClientSettings().getConnectionTimeout(),
				getClientSettings().getReadTimeout(), getClientSettings().getWriteTimeout()) {
		};
		prepareConnection(conn);
		return conn;
	}

	private final AbstractCopAwareInterceptor logInterceptor = new CopHttpLogInterceptor() {
		@Override
		protected boolean accept(Chain chain) {
			return getSigner().acceptRequest(chain.request().url().toString());
		}

		protected CopLog getLogger() {
			return AbstractCopClient.this.logger;
		}
	};
	private final AbstractCopAwareInterceptor copSignerInterceptor = new AbstractCopAwareInterceptor() {
		@Override
		protected boolean accept(Chain chain) {
			return getSigner().acceptRequest(chain.request().url().toString());
		}

		protected Response convertToRepeatableResponse(Response response) throws IOException {
			ResponseBody body = response.body();
			if (body == null) {
				return response;
			}
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				IOUtils.copy(body.byteStream(), baos);
				baos.flush();
				return response.newBuilder().body(ResponseBody.create(baos.toByteArray(), body.contentType())).build();
			}
		}

		@Override
		protected Response intercept0(Chain chain) throws IOException {
			Request request = chain.request();

			Request.Builder newBuilder = request.newBuilder();
			final String requestId = UUID.randomUUID().toString();
			request = newBuilder.addHeader(CopConstants.HTTP_HEADER_REQUEST_ID, requestId).build();
			try {
				request = (Request) getSigner().sign(getCredentialsProvider(), request);
			} catch (CopClientSDKException e) {
				throw new IOException("COP request sign failed.", e);
			}
			Response response = chain.proceed(request);
			response = response.newBuilder().addHeader(CopConstants.HTTP_HEADER_REQUEST_ID, requestId).build();
			if (response.code() >= 200 && response.code() < 300) {
				response = convertToRepeatableResponse(response);
				Validator validator = getValidatorProvider().getValidator(request.url().toString());
				if (validator != null && !validator.validate(response)) {
					throw new IOException("COP response validation failed.");
				}
			}
			return response;
		}
	};

	/**
	 * Performs preparation for {@link CopConnection} before communication.
	 * 
	 * @param conn {@link CopConnection} instance.
	 */
	protected void prepareConnection(CopConnection conn) {
		/**
		 * Registers cop signer
		 */
		conn.addInterceptor(copSignerInterceptor);

		/**
		 * Registers request/response logger
		 */
		conn.addInterceptor(logInterceptor);

		trySetupProxy(conn);

		conn.buildInternalClient();
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

	/**
	 * Makes http-call directly and returns native http-response.
	 * 
	 * @param request entity represents a HTTP request.
	 * @return response entity represents a HTTP response
	 * @throws CopClientSDKException
	 */
	protected Response makeCall(Request request) throws CopClientSDKException {
		try {
			return getCopConnection().doRequest(request);
		} catch (IOException e) {
			throw new CopClientSDKException(e.getClass().getName() + "-" + e.getMessage(), e,
					request.header(CopConstants.HTTP_HEADER_REQUEST_ID));
		}
	}

	/**
	 * Processes response and returns its string body.
	 * 
	 * @param response The response to process
	 * @return string body of response
	 * @throws CopClientSDKException      in case of COP exception
	 * @throws CopServerBusinessException in case of business exception from server
	 *                                    side
	 */
	protected String stringBody(Response response) throws CopClientSDKException, CopServerBusinessException {
		if (response.code() != CopConstants.HTTP_STATUS_OK) {
			String msg = "HTTP Response code is " + response.code() + ", message is " + response.message();
			throw new CopClientSDKException(msg, response.header(CopConstants.HTTP_HEADER_REQUEST_ID),
					"RequestNotProcessed");
		}
		String respbody = null;
		try {
			ResponseBody body = response.body();
			if (body == null) {
				throw new CopClientSDKException("response body may be null");
			}
			respbody = body.string();
		} catch (IOException e) {
			String msg = "Cannot transfer response body to string, because Content-Length is too large, or Content-Length and stream length disagree.";
			logger.info(msg);
			throw new CopClientSDKException(msg + " " + e.getClass().getName(), e);
		}
		CommonResponse commonResponse = null;
		try {
			Type errType = new TypeToken<CommonResponse>() {
			}.getType();
			commonResponse = gson.fromJson(respbody, errType);
		} catch (JsonSyntaxException e) {
			String msg = "Response content is not an accepted json object";
			logger.info(msg);
			throw new CopClientSDKException(msg, e, response.header(CopConstants.HTTP_HEADER_REQUEST_ID),
					e.getClass().getName());
		}
		if (commonResponse.getCode() != 0) {
			throw new CopServerBusinessException(response.header(CopConstants.HTTP_HEADER_REQUEST_ID),
					commonResponse.getCode(), commonResponse.getMessage());
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

		conn.setProxyAuthenticator((route, response) -> response.request().newBuilder()
				.header("Proxy-Authorization", okhttp3.Credentials.basic(username, password)).build());
	}

	protected final Headers.Builder addHeaders(Headers.Builder builder, Map<String, List<String>> extraHeaders) {
		Objects.requireNonNull(builder, "builder may not be null");
		if (Objects.isNull(extraHeaders))
			return builder;
		for (Entry<String, List<String>> headerEntry : extraHeaders.entrySet()) {
			String key = headerEntry.getKey();
			List<String> values = headerEntry.getValue();
			if (Objects.nonNull(values)) {
				for (String value : values) {
					builder = builder.add(key, value);
				}
			}
		}
		return builder;
	}

	protected Request createRequest(SupportedHttpMethod method, String relativePath, String jsonPayload,
			Map<String, List<String>> extraHeaders) {
		return createRequest(method, relativePath, jsonPayload, defaultMediaType, extraHeaders);
	}

	/**
	 * 
	 * @param method
	 * @param relativePath
	 * @param jsonPayload
	 * @param mediaType
	 * @param extraHeaders
	 * @return
	 */
	protected Request createRequest(SupportedHttpMethod method, String relativePath, String jsonPayload,
			MediaType mediaType, Map<String, List<String>> extraHeaders) {
		Objects.requireNonNull(method, "http method may not be null");
		Objects.requireNonNull(mediaType, "mediaType may not be null");
		String url = this.namespace.getRootUrl() + relativePath;
		Builder hb = new Headers.Builder();
		hb = hb.add(CopConstants.HTTP_HEADER_ACCEPT, "application/json")
				.add(CopConstants.HTTP_HEADER_ACCEPT_CHARSET, "utf-8")
				.add(CopConstants.HTTP_HEADER_CONTENT_TYPE, "application/json; charset=utf-8")
				.add(CopConstants.HTTP_HEADER_CLIENT_SDK_VERION, SDK_VERSION);
		hb = addHeaders(hb, extraHeaders);

		switch (method) {
		case GET:
			return new Request.Builder().url(url).headers(hb.build()).get().build();
		case POST:
			return new Request.Builder().url(url).headers(hb.build()).post(RequestBody.create(jsonPayload, mediaType))
					.build();
		default:
			throw new IllegalArgumentException("Unsupported method:" + method.name());
		}
	}
}
