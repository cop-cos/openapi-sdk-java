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

import java.util.List;
import java.util.Map;

import com.coscon.cop.common.CopClientSDKException;
import com.coscon.cop.common.CopServerBusinessException;
import com.coscon.cop.common.Credentials;
import com.coscon.cop.common.HttpMethods;
import com.coscon.cop.common.Namespace;

import okhttp3.Request;

/**
 * @author Chen Jipeng
 *
 */
public class CommonCopClient extends AbstractCopClient {

	/**
	 * Creates a common cop client by specific {@link Namespace} and
	 * {@link Credentials}
	 * 
	 * @param ns
	 * @param credential
	 */
	public CommonCopClient(Namespace ns, Credentials credentials) {
		super(ns, "", "", credentials);
	}
	public CommonCopClient(Namespace ns, Credentials credentials,ClientSettings clientSettings) {
		super(ns, "", "", credentials,clientSettings);
	}
	public String doGet(String relateUrlPath)
			throws CopClientSDKException, CopServerBusinessException {
		return doGet(relateUrlPath,null);
	}
	public String doGet(String relateUrlPath, Map<String, List<String>> extraHeaders)
			throws CopClientSDKException, CopServerBusinessException {
		return stringBody(makeCall(createRequest(HttpMethods.GET, relateUrlPath, "", extraHeaders)));
	}
	public String doPost(String relateUrlPath, String jsonPayload)
			throws CopClientSDKException, CopServerBusinessException {
		return doPost(relateUrlPath, jsonPayload,null);
	}
	public String doPost(String relateUrlPath, String jsonPayload, Map<String, List<String>> extraHeaders)
			throws CopClientSDKException, CopServerBusinessException {
		return stringBody(makeCall(createRequest(HttpMethods.POST, relateUrlPath, jsonPayload, extraHeaders)));
	}
}
