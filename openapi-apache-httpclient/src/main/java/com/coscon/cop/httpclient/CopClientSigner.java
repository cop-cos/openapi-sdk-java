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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.ContentType;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.Credentials;
import com.coscon.cop.core.CredentialsProvider;
import com.coscon.cop.core.SignAlgorithm;
import com.coscon.cop.core.Signer;
import com.coscon.cop.internal.BasicSigner;
import com.coscon.cop.internal.HmacPureExecutor;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
class CopClientSigner extends BasicSigner implements Signer {

	public CopClientSigner(SignAlgorithm method) {
		super(method);
	}


	@Override
	public Object sign(final CredentialsProvider provider, Object rawRequest) throws IOException {
		if (!(rawRequest instanceof HttpRequestWrapper)) {
			throw new IllegalArgumentException("HttpRequestWrapper request is expected.");
		}
		HttpRequestWrapper request = (HttpRequestWrapper) rawRequest;
		
		final Credentials credentials = provider
				.getCredentials(request.getTarget().toURI() + request.getURI().toString());
		if(Objects.isNull(credentials)) {
			throw new IOException("Unable to find suitable credentials");
		}
		
		byte[] httpContent = new byte[0];
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			if (entity != null) {
				httpContent = IOUtils.toByteArray(entity.getContent());
			}
		}
		try {
			final CopClientSigner signerSelf = this;
			HmacPureExecutor executor = new HmacPureExecutor() {
				
				@Override
				protected String getSecretKey() {
					return credentials.getPassword();
				}
				
				@Override
				public SignAlgorithm getHmacAlgorithm() {
					return signerSelf.getMethod();
				}
				
				@Override
				protected String getApiKey() {
					return credentials.getPrincipal().getName();
				}
			};
			Map<String, String> headers = executor.buildHmacHeaders(request.getRequestLine().toString(), httpContent);
			if (headers != null) {
				for (Entry<String, String> e : headers.entrySet()) {
					request.setHeader(e.getKey(), e.getValue());
				}
			}
			Header uaHeader = request.getFirstHeader(HEADER_USER_AGENT);

			if (uaHeader == null || StringUtils.isEmpty(uaHeader.getValue())) {
				request.setHeader(HEADER_USER_AGENT, CopClient.COP_SDK_VERSION);
			} else {
				if (!uaHeader.getValue().toUpperCase().contains(CopClient.COP_SDK_VERSION.toUpperCase())) {
					StringBuilder uaBuilder = new StringBuilder();
					uaBuilder.append(uaHeader.getValue()).append(" ").append(CopClient.COP_SDK_VERSION);
					request.setHeader(HEADER_USER_AGENT, uaBuilder.toString());
				}
			}
			request.setHeader(HEADER_ACCEPT,ContentType.APPLICATION_JSON.getMimeType());
			request.setHeader(HEADER_ACCEPT_CHARSET,ContentType.APPLICATION_JSON.getCharset().toString());
			return request;
		} catch (ClientException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
