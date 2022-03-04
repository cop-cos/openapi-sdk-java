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

import java.util.Objects;

import org.apache.commons.lang3.EnumUtils;

import com.coscon.cop.common.Credentials;
import com.coscon.cop.common.Namespace;
import com.coscon.cop.common.exception.CopClientSDKException;

/**
 * @author Chen Jipeng / chenjp2@coscon.com
 *
 */
public interface Signer {

	/**
	 * Sign request with a given {@link CredentialsProvider}
	 * @param provider to determine {@link Credentials}
	 * @param request origin request entity.
	 * @return request with signature
	 * @throws CopClientSDKException when error occurs
	 */
	Object sign(CredentialsProvider provider, Object request) throws CopClientSDKException;
	
	/**
	 * Check whether need signature for the specific uri request.
	 * 
	 * @param uri of request
	 * @return result <code>true</code> if the request requires signature.
	 */
	default boolean acceptRequest(String uri) {
		Objects.requireNonNull(uri, "uri may not be null");
		for(Namespace ns:EnumUtils.getEnumList(Namespace.class)) {
			if(uri.toLowerCase().startsWith(ns.getRootUrl().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
