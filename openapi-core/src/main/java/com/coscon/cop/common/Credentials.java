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

/**
 * Credentials of each COP client, the only legal identification issued by COP administration.
 * <p>Each credentials contains {@link Namespace} of affected scope, {@link Principal}, and password.
 * @author Chen Jipeng
 *
 */
public interface Credentials {
	/**
	 * Returns {@link Namespace} of current credential.
	 * @return {@link Namespace} represents credential scope
	 */
	Namespace getNamespace();
	/**
	 * Returns principal of current credential.
	 * @return principal identified by its name.
	 */
	Principal getPrincipal();
	/**
	 * Returns secret password to establish principal identity
	 * @return secret password
	 */
	String getPassword();
}
