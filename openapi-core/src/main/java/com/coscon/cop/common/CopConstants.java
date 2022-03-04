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

/**
 * This class defines cop constants.
 * @author Chen Jipeng
 *
 */
public final class CopConstants {
	/**
	 * Defalut constructor to prevent instantiation.
	 */
	private CopConstants() {
		throw new IllegalAccessError("constructor may never be invoked");
	}
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final int HTTP_STATUS_OK=200;
	public static final String HTTP_HEADER_REQUEST_ID = "X-Coscon-Request-ID";
	public static final String HTTP_HEADER_CLIENT_SDK_VERION="X-Cop-Client-SDK";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
}
