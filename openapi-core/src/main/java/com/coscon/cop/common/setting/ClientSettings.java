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
	private int readTimout;
	private int writeTimeout;
	private boolean debugEnabled;
	private SignAlgorithm signMethod;
	/**
	 * Creates a clientSetting with default settings.
	 */
	public ClientSettings() {
		this(SignAlgorithm.HMAC_SHA1,false,30,10,10);
	}
	
	/**
	 * @param debugEnabled the debugEnabled to set
	 */
	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	/**
	 * Creates a clientSetting
	 * @param signMethod 
	 * @param debugEnabled
	 * @param connectionTimeout
	 * @param readTimeout
	 * @param writeTimeout
	 */
	public ClientSettings(SignAlgorithm signMethod, boolean debugEnabled, int connectionTimeout, int readTimeout, int writeTimeout) {
		super();
		this.signMethod = Objects.requireNonNull(signMethod,"signMethod may not be null");
		this.debugEnabled = debugEnabled;
		this.connectionTimeout = connectionTimeout;
		this.readTimout = readTimeout;
		this.writeTimeout = writeTimeout;
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
	public int getReadTimeout() {
		return readTimout;
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
	 * @return
	 */
	public String getProxyHost() {
		return null;
	}

	/**
	 * @return
	 */
	public int getProxyPort() {
		return 0;
	}

	/**
	 * @return
	 */
	public String getProxyUsername() {
		return null;
	}

	/**
	 * @return
	 */
	public String getProxyPassword() {
		return null;
	}

	/**
	 * @return
	 */
	public SignAlgorithm getSignMethod() {
		return signMethod;
	}

}
