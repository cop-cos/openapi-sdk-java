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
package com.coscon.cop.common.http;

import java.io.IOException;

import org.apache.commons.lang3.RegExUtils;

import com.coscon.cop.common.CopLog;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class provides logging support for Cop-related communication via
 * {@link Interceptor}.
 * 
 * @author Chen Jipeng
 *
 */
public abstract class CopHttpLogInterceptor extends AbstractCopAwareInterceptor implements Interceptor {
	/**
	 * Constructs a new instance with given logger name.
	 * 
	 * @param name of logger.
	 */
	public CopHttpLogInterceptor() {
		super();
	}

	protected abstract CopLog getLogger();

	/**
	 * @return the debugEnabled
	 */
	public boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	protected void info(final String message) {
		getLogger().info(message);
	}

	@Override
	protected Response intercept0(Chain chain) throws IOException {
		Request request = chain.request();
		if (isDebugEnabled()) {
			StringBuilder messageBuilder = new StringBuilder();
			messageBuilder.append("Send request. Request url: ").append(request.url())
					.append(", request headers information: ").append(request.headers());
			String message = RegExUtils.replaceAll(messageBuilder.toString(), "\n", ";");
			info(message);
		}
		Response response = chain.proceed(request);
		if (isDebugEnabled()) {
			StringBuilder messageBuilder = new StringBuilder();
			messageBuilder.append("Recieve response. Response url: ").append(request.url())
					.append(", response headers: ").append(response.headers()).append(", response body information: ")
					.append(response.body());
			String message = RegExUtils.replaceAll(messageBuilder.toString(), "\n", ";");
			info(message);
		}
		return response;
	}

}
