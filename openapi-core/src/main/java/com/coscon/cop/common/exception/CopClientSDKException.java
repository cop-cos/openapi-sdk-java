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
 * Thrown to indicates that CopClientSDK receives an exception while communicating with COP.
 * @author Chen Jipeng
 * 
 * @since 0.0.2
 */
public class CopClientSDKException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2810237877928159011L;
	private final String errorCode;
	private final String requestId;

	/**
	 * Constructs a new {@link CopClientSDKException} instance with the specified
	 * detail message and an unique request id
	 * <p>
	 * 
	 * @param message the detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method).
	 */
	public CopClientSDKException(String message) {
		this(message, (Throwable) null);
	}

	/**
	 * Constructs a new {@link CopClientSDKException} instance with the specified
	 * detail message and an unique request id
	 * <p>
	 * 
	 * @param message the detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method).
	 * @param cause   the cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method). (A <tt>null</tt> value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public CopClientSDKException(String message, Throwable cause) {
		this(message, cause, "");
	}

	/**
	 * Constructs a new {@link CopClientSDKException} instance with the specified
	 * detail message and an unique request id
	 * <p>
	 * 
	 * @param message the detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method).
	 * @param cause   the cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method). (A <tt>null</tt> value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public CopClientSDKException(String message, Throwable cause, String requestId) {
		this(message, cause, requestId, "");
	}
	/**
	 * Constructs a new CopClientSDKException with the specified detail message,
	 * cause, code and requestIds.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message   the detail message (which is saved for later retrieval by
	 *                  the {@link #getMessage()} method).
	 * @param cause     the cause (which is saved for later retrieval by the
	 *                  {@link #getCause()} method). (A <tt>null</tt> value is
	 *                  permitted, and indicates that the cause is nonexistent or
	 *                  unknown.)
	 * 
	 * @param requestId the requestId (which is unique for each request).
	 * @param errorCode the error code of exception reason.
	 */
	public CopClientSDKException(String message, Throwable cause, String requestId, String errorCode) {
		super(message, cause);
		this.requestId = requestId;
		this.errorCode = errorCode;
	}

	/**
	 * Constructs a new {@link CopClientSDKException} instance with the specified
	 * detail message and an unique request id
	 * <p>
	 * 
	 * @param message   the detail message (which is saved for later retrieval by
	 *                  the {@link #getMessage()} method).
	 * @param requestId the requestId (which is unique for each request).
	 */
	public CopClientSDKException(String message, String requestId) {
		this(message, null, requestId, "");
	}


	public String getErrorCode() {
		return errorCode;
	}

	public String getRequestId() {
		return requestId;
	}

	@Override
	public String toString() {
		return "CopClientSDKException [getErrorCode()=" + getErrorCode() + ", getRequestId()=" + getRequestId()
				+ ", getMessage()=" + getMessage() + "]";
	}
	
}
