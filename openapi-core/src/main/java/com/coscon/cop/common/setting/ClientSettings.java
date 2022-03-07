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
package com.coscon.cop.common.setting;

import java.util.Objects;

import com.coscon.cop.internal.SignAlgorithm;

/**
 * @author Chen Jipeng
 *
 */
public class ClientSettings {
	private int connectionTimeout;
	private boolean debugEnabled;
	private final HttpProxy proxy;
	private int readTimout;
	private SignAlgorithm signMethod;
	private int writeTimeout;

	/**
	 * Creates a clientSetting with default settings.
	 */
	public ClientSettings() {
		this((HttpProxy) null);
	}

	public ClientSettings(HttpProxy proxy) {
		this(SignAlgorithm.HMAC_SHA1, proxy);
	}

	public ClientSettings(SignAlgorithm signMethod) {
		this(signMethod, (HttpProxy) null);
	}

	/**
	 * Creates a clientSetting
	 * 
	 * @param signMethod
	 * @param debugEnabled
	 * @param connectionTimeout
	 * @param readTimeout
	 * @param writeTimeout
	 */
	public ClientSettings(SignAlgorithm signMethod, boolean debugEnabled, int connectionTimeout, int readTimeout,
			int writeTimeout) {
		this(signMethod, debugEnabled, connectionTimeout, readTimeout, writeTimeout, null);
	}

	public ClientSettings(SignAlgorithm signMethod, boolean debugEnabled, int connectionTimeout, int readTimeout,
			int writeTimeout, HttpProxy proxy) {
		super();

		this.signMethod = Objects.requireNonNull(signMethod, "signMethod may not be null");
		if (Objects.isNull(proxy)) {
			this.proxy = new HttpProxy(null, -1, null, null);
		} else {
			this.proxy = proxy;
		}

		this.debugEnabled = debugEnabled;
		this.connectionTimeout = connectionTimeout;
		this.readTimout = readTimeout;
		this.writeTimeout = writeTimeout;
	}

	public ClientSettings(SignAlgorithm signMethod, HttpProxy proxy) {
		this(signMethod, false, 30, 10, 10, proxy);
	}

	/**
	 * @return
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @return
	 */
	public String getProxyHost() {
		return proxy.getHost();
	}

	/**
	 * @return
	 */
	public String getProxyPassword() {
		return proxy.getPassword();
	}

	/**
	 * @return
	 */
	public int getProxyPort() {
		return proxy.getPort();
	}

	/**
	 * @return
	 */
	public String getProxyUsername() {
		return proxy.getUsername();
	}

	/**
	 * @return
	 */
	public int getReadTimeout() {
		return readTimout;
	}

	/**
	 * @return
	 */
	public SignAlgorithm getSignMethod() {
		return signMethod;
	}

	/**
	 * @return
	 */
	public int getWriteTimeout() {
		return writeTimeout;
	}

	/**
	 * @return
	 */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * @param debugEnabled the debugEnabled to set
	 */
	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

}
