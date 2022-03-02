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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.Namespace;
import com.coscon.cop.core.Validator;
import com.coscon.cop.internal.CopClientBase;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class CopClient extends CopClientBase implements Closeable {

	public static final String COP_SDK_VERSION = "COP_SDK_HttpClient/0.0.1";

	private static final String HTTP_CONTEXT_NAMESPACE = "http.context.cop.namespace";

	private CopClient() {
		super();

	}

	@Override
	protected void initialize() {
		httpClientBuilder = HttpClientBuilder.create();
		setSignMethod(defaultSignMethod);
		setResponseHandler(defaultResponseHandler);
	}

	public static CopClient newInstance() {
		CopClient client = new CopClient();
		client.initialize();
		return client;
	}

	private HttpClientBuilder httpClientBuilder;

	private CloseableHttpClient httpClient = null;

	private Executor executor = null;

	@Override
	public void buildHttpClient() throws ClientException {
		if (Objects.isNull(this.httpClient)) {
			getHttpClientBuilder().addInterceptorLast(newSignRequestInterceptor());
			getHttpClientBuilder().addInterceptorLast(newUnwrapResponseInterceptor());
			setSigner(new CopClientSigner(getSignMethod()));
			httpClient = getHttpClientBuilder().build();
			executor = Executor.newInstance(httpClient);
		} else {
			throw new ClientException("Unable to overwrite executor/httpclient");
		}
	}

	@Override
	public void close() {
		if (this.executor != null) {
			this.executor.clearAuth();
			this.executor.clearCookies();
			this.executor = null;
		}
		if (this.httpClient instanceof CloseableHttpClient) {
			try {
				((CloseableHttpClient) httpClient).close();
			} catch (IOException e) {
				//Do nothing.
			}
		}
		httpClient = null;
		httpClientBuilder = null;
	}

	/**
	 * @return the executor
	 */
	public Executor getExecutor() {
		Objects.requireNonNull(executor, "executor may not be null");
		return executor;
	}

	/**
	 * @return the httpClientBuilder
	 */
	public HttpClientBuilder getHttpClientBuilder() {
		return Objects.requireNonNull(this.httpClientBuilder, "httpClientBuilder may not be null");
	}

	/**
	 * @return interceptor to perform request signature.
	 */
	private HttpRequestInterceptor newSignRequestInterceptor() {
		return new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) throws IOException {
				if(request instanceof HttpRequestWrapper) {
					HttpRequestWrapper wrapper = (HttpRequestWrapper)request;
					StringBuilder uriBuilder = new StringBuilder();
					uriBuilder.append(wrapper.getTarget().toURI());
					uriBuilder.append(wrapper.getURI());
					if(getSigner().acceptRequest(uriBuilder.toString())) {
						getSigner().sign(getCredentialsProvider(), request);
					}
				}
			}
		};
	}

	/**
	 * @return interceptor to unwrap response.
	 */
	private HttpResponseInterceptor newUnwrapResponseInterceptor() {
		return new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
				String nsValue = (String) context.getAttribute(HTTP_CONTEXT_NAMESPACE);
				if (StringUtils.isBlank(nsValue)) {
					return;
				}
				Validator validator = getValidatorProvider().getValidator(nsValue);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() >= 200 && statusLine.getStatusCode() < 300) {
					HttpEntity entity = response.getEntity();
					if (Objects.nonNull(entity)) {
						response.setEntity(new BufferedHttpEntity(entity));
					}
					if (Objects.nonNull(validator) && !validator.validate(response)) {
						throw new HttpException("COP response validation failed.");
					}
				}
			}
		};
	}

	/*
	 * *****************************************************************************
	 * ********************
	 */

	@Override
	public String doGet(Namespace namespace, String relativeUri) throws ClientException {
		return doGet(namespace, relativeUri, null);
	}

	/**
	 * the default response handler
	 */
	private static final ResponseHandler<String> defaultResponseHandler = new ResponseHandler<String>() {
		@Override
		public String handleResponse(HttpResponse response) throws IOException {
			final int status = response.getStatusLine().getStatusCode();
			final String reasonPhrase = response.getStatusLine().getReasonPhrase();
			final HttpEntity entity = response.getEntity();
			if (status >= 200 && status < 300) {
				if (entity == null) {
					return "";
				} else {
					return EntityUtils.toString(entity, DEFAULT_CHARSET);
				}
			} else {
				EntityUtils.consume(entity);
				throw new IOException("http response - status:" + status + ", reason phrase:" + reasonPhrase);
			}
		}
	};

	@Override
	public void setResponseHandler(Object handler) {
		if (handler instanceof ResponseHandler<?>) {
			super.setResponseHandler(handler);
		} else {
			Objects.requireNonNull(handler, "handler may not be null");
			throw new IllegalArgumentException("handler may not ResponseHandler<String> intance");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResponseHandler<String> getResponseHandler() {
		return (ResponseHandler<String>) super.getResponseHandler();
	}

	@Override
	public String doGet(Namespace namespace, String relativeUri, Map<String, List<String>> extraHeaders)
			throws ClientException {
		Request get = Request.Get(namespace.getPrefix() + relativeUri);

		get = populateHeaders(get, extraHeaders);
		try {
			return getExecutor().execute(get).handleResponse(getResponseHandler());
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public String doPost(Namespace namespace, String relativeUri, String payload) throws ClientException {
		return doPost(namespace, relativeUri, payload, (Map<String, List<String>>) null);
	}

	@Override
	public String doPost(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException {
		Request post = Request.Post(namespace.getPrefix() + relativeUri)
				.body(new StringEntity(payload, ContentType.APPLICATION_JSON));
		post = populateHeaders(post, extraHeaders);
		try {
			return getExecutor().execute(post).handleResponse(getResponseHandler());
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public HttpResponse doGetWithResponse(Namespace namespace, String relativeUri) throws ClientException {
		return doGetWithResponse(namespace, relativeUri, null);
	}

	@Override
	public HttpResponse doGetWithResponse(Namespace namespace, String relativeUri,
			Map<String, List<String>> extraHeaders) throws ClientException {

		Request get = Request.Get(namespace.getPrefix() + relativeUri);

		get = populateHeaders(get, extraHeaders);
		try {
			return getExecutor().execute(get).returnResponse();
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public HttpResponse doPostWithResponse(Namespace namespace, String relativeUri, String payload)
			throws ClientException {
		return doPostWithResponse(namespace, relativeUri, payload, (Map<String, List<String>>) null);
	}

	@Override
	public HttpResponse doPostWithResponse(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException {
		Request post = Request.Post(namespace.getPrefix() + relativeUri)
				.body(new StringEntity(payload, ContentType.APPLICATION_JSON));
		post = populateHeaders(post, extraHeaders);
		try {
			return getExecutor().execute(post).returnResponse();
		} catch (IOException e) {
			throw new ClientException(e.getLocalizedMessage(), e);
		}
	}

	protected Request populateHeaders(Request request, Map<String, List<String>> extraHeaders) {
		if (Objects.nonNull(extraHeaders) && !extraHeaders.isEmpty()) {
			for (Map.Entry<String, List<String>> headers : extraHeaders.entrySet()) {
				String hName = headers.getKey();
				for (String hValue : headers.getValue()) {
					request.addHeader(hName, hValue);
				}
			}
		}
		return request;
	}
}
