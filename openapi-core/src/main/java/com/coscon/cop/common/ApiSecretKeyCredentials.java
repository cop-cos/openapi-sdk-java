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
package com.coscon.cop.common;

import java.security.Principal;
import java.util.Objects;

/**
 * @author Chen Jipeng
 *
 */
public class ApiSecretKeyCredentials implements Credentials {
	private Namespace namespace;
	private Principal principal;
	private String secretKey;

	public ApiSecretKeyCredentials(Namespace namespace, final String apiKey, String secretKey) {
		super();
		Objects.requireNonNull(apiKey, "username may not be null");
		this.namespace = Objects.requireNonNull(namespace, "namespace may not be null");
		this.principal = new Principal() {
			private final String name = apiKey;
			@Override
			public String getName() {
				return name;
			}
		};
		this.secretKey = Objects.requireNonNull(secretKey, "password may not be null");
	}

	@Override
	public Namespace getNamespace() {
		return this.namespace;
	}

	@Override
	public Principal getPrincipal() {
		return this.principal;
	}

	@Override
	public String getPassword() {
		return this.secretKey;
	}

	@Override
	public String toString() {
		return "UsernamePasswordCredentials [namespace=" + namespace + ", principal=" + principal + "]";
	}

}
