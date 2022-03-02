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

import java.util.Objects;

import com.coscon.cop.core.SignAlgorithm;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public abstract class BasicSigner implements Signer {
	protected static final String HEADER_USER_AGENT = "User-Agent";
	protected static final String HEADER_ACCEPT = "Accept";
	protected static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
	private SignAlgorithm method;
	
	/**
	 * @return the method
	 */
	protected SignAlgorithm getMethod() {
		return method;
	}
	public BasicSigner(SignAlgorithm method) {
		super();
		this.method = Objects.requireNonNull(method, "method may not be null");
	}
}
