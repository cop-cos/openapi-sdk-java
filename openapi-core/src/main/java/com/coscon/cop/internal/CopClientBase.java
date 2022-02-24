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
package com.coscon.cop.internal;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.coscon.cop.core.ClientException;
import com.coscon.cop.core.CredentialsProvider;
import com.coscon.cop.core.Namespace;
import com.coscon.cop.core.SignAlgorithm;
import com.coscon.cop.core.Signer;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public abstract class CopClientBase implements Closeable {
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final String MIME_TYPE_APPLICATION_JSON = "application/json";
	private CredentialsProvider credentialsProvider;

	private ValidatorProvider validatorProvider;

	private Signer signer;

	protected CopClientBase() {
		super();
		credentialsProvider = new BasicCredentialsProvider();
		validatorProvider = new BasicValidatorProvider();
		initialize();
	}

	protected abstract void initialize();
	/**
	 * Build an internal httpClient instance, can only invoke once for each
	 * CopClient's life-cycle.
	 * @throws ClientException
	 */
	public abstract void buildHttpClient() throws ClientException;
	
	private Object responseHandler;
	/**
	 * Specify an response handler.
	 * @param handler to handle http response. Choose suitable handler instance for different httpClient driver.
	 */
	public void setResponseHandler(Object responseHandler) {
		this.responseHandler = Objects.requireNonNull(responseHandler, "responseHandler may not be null");
	}
	protected Object getResponseHandler() {
		return this.responseHandler;
	}
	@Override
	public void close() {
		this.credentialsProvider = null;
		this.validatorProvider = null;
		this.signer = null;
	}

	protected CredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	protected Signer getSigner() {
		Objects.requireNonNull(signer, "signer may not be null");
		return signer;
	}

	protected ValidatorProvider getValidatorProvider() {
		return validatorProvider;
	}

	private SignAlgorithm signMethod = null;

	/**
	 * @return the signMethod
	 */
	protected SignAlgorithm getSignMethod() {
		return signMethod;
	}

	protected static final SignAlgorithm defaultSignMethod = SignAlgorithm.HMAC_SHA1;

	/**
	 * Sets signature method.
	 * 
	 * @param algorithm
	 */
	public void setSignMethod(SignAlgorithm algorithm) {
		this.signMethod = Objects.requireNonNull(algorithm, "signMethod may not be null");
	}

	/**
	 * sets signer
	 * @param signer target {@link Signer}
	 * @throws ClientException if try to overwrite an existing signer
	 */
	protected void setSigner(Signer signer) throws ClientException {
		Objects.requireNonNull(signer, "signer may not be null");
		if (Objects.isNull(this.signer)) {
			this.signer = signer;
		} else {
			throw new ClientException("Unable to overwrite signer");
		}
	}

	/**
	 * Sets credentials.
	 * @param namespace scope of credentials.
	 * @param apiKey
	 * @param secretKey
	 * @return self
	 * @throws ClientException when try to overwrite an existing apiKey/namespace pair.
	 */
	public CopClientBase withCredentials(Namespace namespace, String apiKey, String secretKey) throws ClientException {
		if(Objects.nonNull(getCredentialsProvider().getCredentials(namespace))) {
			throw new ClientException("Unable to overwrite credentials for namespace:"+namespace);
		}
		getCredentialsProvider().setCredentials(namespace,
				new UsernamePasswordCredentials(namespace, apiKey, secretKey));
		return this;
	}
	/*
	 * *****************************************************************************
	 */

	public abstract String doGet(Namespace namespace, String relativeUri) throws ClientException;

	public abstract String doGet(Namespace namespace, String relativeUri, Map<String, List<String>> extraHeaders)
			throws ClientException;

	public abstract Object doGetWithResponse(Namespace namespace, String relativeUri) throws ClientException;

	public abstract Object doGetWithResponse(Namespace namespace, String relativeUri,
			Map<String, List<String>> extraHeaders) throws ClientException;

	public abstract String doPost(Namespace namespace, String relativeUri, String payload) throws ClientException;

	public abstract String doPost(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException;

	public abstract Object doPostWithResponse(Namespace namespace, String relativeUri, String payload)
			throws ClientException;

	public abstract Object doPostWithResponse(Namespace namespace, String relativeUri, String payload,
			Map<String, List<String>> extraHeaders) throws ClientException;
}
