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
package com.coscon.cop.common.provider;

import java.security.Principal;
import java.util.Objects;

/**
 * @author Chen Jipeng
 *
 */
public class BasicUserPrincipal implements Principal {

	private String username;
	/**
	 * 
	 */
	public BasicUserPrincipal(String username) {
		super();
		this.username = Objects.requireNonNull(username, "username may not be null");
	}
	@Override
	public String getName() {
		return username;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		BasicUserPrincipal other = (BasicUserPrincipal) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BasicUserPrincipal [username=" + username + "]";
	}
	
}
