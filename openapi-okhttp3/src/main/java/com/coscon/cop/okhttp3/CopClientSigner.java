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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.coscon.cop.common.CopClientSDKException;
import com.coscon.cop.common.Credentials;
import com.coscon.cop.common.HttpMethods;
import com.coscon.cop.common.SignAlgorithm;
import com.coscon.cop.common.Signer;
import com.coscon.cop.common.provider.CredentialsProvider;
import com.coscon.cop.internal.HmacPureExecutor;

import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author Chen Jipeng
 *
 */
class CopClientSigner implements Signer {
	private final SignAlgorithm signMethod;
	public CopClientSigner(SignAlgorithm method) {
		super();
		this.signMethod = Objects.requireNonNull(method,"signMethod may not be null");
	}

	private String parseRequestLine(Request request) {
		HttpUrl url = request.url();
		StringBuilder requestLineBuilder = new StringBuilder();
		requestLineBuilder.append(request.method()).append(' ');
		if (!request.isHttps()) {
			throw new IllegalStateException("Unsecure request is not allowed");
		} else {
			String path = url.encodedPath();
			requestLineBuilder.append(path);
			String query = url.encodedQuery();
			if (StringUtils.isNotEmpty(query)) {
				requestLineBuilder.append('?').append(query);
			}
		}
		requestLineBuilder.append(' ').append(Protocol.HTTP_1_1.toString().toUpperCase());
		return requestLineBuilder.toString();
	}

	@Override
	public Object sign(final CredentialsProvider provider, Object rawRequest) throws CopClientSDKException {
		if (!(rawRequest instanceof Request)) {
			throw new IllegalArgumentException("Request request is expected.");
		}
		Request request = (Request) rawRequest;
		Objects.requireNonNull(request.url(), "request url may not be null");

		final Credentials credentials = provider.getCredentials(request.url().toString());
		if (Objects.isNull(credentials)) {
			throw new CopClientSDKException("Unable to find suitable credentials");
		}

		byte[] httpContent = new byte[0];
		RequestBody body = request.body();
		if (body != null && HttpMethods.POST.getMethod().equalsIgnoreCase(request.method())) {
			try (final Buffer buffer = new Buffer()) {
				body.writeTo(buffer);
				httpContent = buffer.readByteArray();
			} catch (IOException e) {
				throw new CopClientSDKException(e.getMessage());
			}
		}

		final CopClientSigner signerSelf = this;
		HmacPureExecutor executor = new HmacPureExecutor() {

			@Override
			protected String getSecretKey() {
				return credentials.getPassword();
			}
			@Override
			protected String getApiKey() {
				return credentials.getPrincipal().getName();
			}

			@Override
			public SignAlgorithm getHmacAlgorithm() {
				return signerSelf.signMethod;
			}

		};
		Map<String, String> headers = executor.buildHmacHeaders(parseRequestLine(request), httpContent);
		Request.Builder newBuilder = request.newBuilder();
		if (headers != null) {
			for (Entry<String, String> e : headers.entrySet()) {
				newBuilder = newBuilder.header(e.getKey(), e.getValue());
			}
		}
		return newBuilder.build();
	}
}
