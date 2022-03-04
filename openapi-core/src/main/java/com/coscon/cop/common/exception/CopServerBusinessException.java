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
package com.coscon.cop.common.exception;

/**
 * Thrown to indicates that CopClientSDK receives an exception.
 * @author Chen Jipeng
 *
 */
public class CopServerBusinessException extends Exception  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -135390628985214923L;
	private final int code;
	private final String requestId;
	/**
	 * Creates an {@link CopServerBusinessException}.
	 * @param requestId
	 * @param code
	 * @param message
	 */
	public CopServerBusinessException(String requestId, int code, String message) {
		super(message);
		this.code = code;
		this.requestId=requestId;
	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	@Override
	public String toString() {
		return "CopServerSideException [getCode()=" + getCode() + ", getMessage()=" + getMessage() + "]";
	}
}
