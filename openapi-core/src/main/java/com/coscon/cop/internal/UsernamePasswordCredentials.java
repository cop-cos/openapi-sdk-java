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

import com.coscon.cop.core.Credentials;
import com.coscon.cop.core.Namespace;

/**
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public class UsernamePasswordCredentials implements Credentials {
	private Namespace namespace;
	private BasicUserPrincipal principal;
	private String password;

	public UsernamePasswordCredentials(Namespace namespace, String username, String password) {
		super();
		this.namespace = Objects.requireNonNull(namespace, "namespace may not be null");
		this.principal = new BasicUserPrincipal(Objects.requireNonNull(username, "username may not be null"));
		this.password = Objects.requireNonNull(password, "password may not be null");
	}

	@Override
	public Namespace getNamespace() {
		return this.namespace;
	}

	@Override
	public BasicUserPrincipal getPrincipal() {
		return this.principal;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((principal == null) ? 0 : principal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsernamePasswordCredentials other = (UsernamePasswordCredentials) obj;
		if (namespace != other.namespace)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UsernamePasswordCredentials [namespace=" + namespace + ", principal=" + principal + "]";
	}

}
