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

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * This class provides a basic CopAware implementation of {@link Interceptor}.
 * <p>To implements 
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public abstract class AbstractCopAwareInterceptor implements Interceptor {

	protected boolean accept(Chain chain) {
		return true;
	}
	@Override
	public final Response intercept(Chain chain) throws IOException {
		if(accept(chain)) {
			return intercept0(chain);
		} else {
			return chain.proceed(chain.request());
		}
	}
	protected abstract Response intercept0(Chain chain) throws IOException;
}
