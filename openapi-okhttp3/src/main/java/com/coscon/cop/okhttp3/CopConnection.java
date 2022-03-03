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
import com.coscon.cop.common.CopConstants;

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
 * @author Chen Jipeng
 *
 */
public abstract class CopConnection {

	private OkHttpClient.Builder httpClientBuilder;
	private OkHttpClient httpClient;

	protected CopConnection(int connTimeout, int readTimeout, int writeTimeout) {
		this.httpClientBuilder = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(connTimeout))
				.readTimeout(Duration.ofSeconds(readTimeout)).writeTimeout(Duration.ofSeconds(writeTimeout));

	}

	protected abstract void initialize();

	public void addInterceptor(Interceptor interceptor) {
		this.httpClientBuilder.addInterceptor(interceptor);
	}
	public void setProxy(Proxy proxy) {
		this.httpClientBuilder.proxy(proxy);
	}

	public void setProxyAuthenticator(Authenticator authenticator) {
		this.httpClientBuilder.proxyAuthenticator(authenticator);
	}
	/**
	 * Call this method to build an internal client.
	 * <p>Programmer needs call this method before communicate with COP service.
	 */
	public CopConnection buildInternalClient() {
		this.httpClient = this.httpClientBuilder.build();
		this.httpClientBuilder = null;
		return this;
	}

	protected void checkInternalClient() throws CopClientSDKException {
		if (this.httpClient == null) {
			throw new CopClientSDKException("httpClient is not ready to use, call #buildInternalClient before use.");
		}
	}

	public Response doRequest(Request request) throws IOException, CopClientSDKException{
		checkInternalClient();
		return this.httpClient.newCall(request).execute();
	}
}
