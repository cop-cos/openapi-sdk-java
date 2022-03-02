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

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.coscon.cop.core.Namespace;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class BasicCredentialsProvider implements CredentialsProvider {

	private final ConcurrentHashMap<Namespace, Credentials> credentialsMap;
	/**
	 * 
	 */
	public BasicCredentialsProvider() {
		super();
		this.credentialsMap = new ConcurrentHashMap<>();
	}
	@Override
	public void setCredentials(Namespace ns, Credentials credentials) {
		Objects.requireNonNull(ns, "namespace may not be null");
		Objects.requireNonNull(credentials, "credentials may not be null");
		Credentials old = credentialsMap.get(ns);
		if(! credentials.equals(old)) {
			credentialsMap.put(ns, credentials);
		}
	}

	@Override
	public Credentials getCredentials(Namespace ns) {
		return credentialsMap.get(ns);
	}
	@Override
	public Credentials getCredentials(String url) {
		Objects.requireNonNull(url, "url may not be null");
		for (Map.Entry<Namespace, Credentials> e : credentialsMap.entrySet()) {
			if(url.toLowerCase().startsWith(e.getKey().getPrefix().toLowerCase())) {
				return e.getValue();
			}
		}
		return null;
	}
	@Override
	public Credentials getCredentials(URI uri) {
		return getCredentials(uri.toString());
	}
	@Override
	public Credentials getCredentials(URL url) {
		return getCredentials(url.toString());
	}
	@Override
	public void clear() {
		credentialsMap.clear();
	}

}
