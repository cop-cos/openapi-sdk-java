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
package com.coscon.cop.core;

/**
 * Supported hmac-algorithm.
 * 
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 */
public enum HmacAlgorithm {
	/**
	 * Represents HmacSHA1
	 */
	HMAC_SHA1("HmacSHA1", "hmac-sha1"),
	/**
	 * Represents HmacSHA256
	 */
	HMAC_SHA256("HmacSHA256", "hmac-sha256"),
	/**
	 * Represents HmacSHA384
	 */
	HMAC_SHA384("HmacSHA384", "hmac-sha384"),
	/**
	 * Represents HmacSHA512
	 */
	HMAC_SHA512("HmacSHA512", "hmac-sha512");
	/**
	 * Return supported hmac-algorithm.
	 * 
	 * @param algo
	 * @return value of HmacAlgorithm, return <tt>null</null> if the algorithm
	 *         is not supported.
	 * 
	 */
	public static HmacAlgorithm validate(String algo) {
		for (HmacAlgorithm e : values()) {
			if (e.getAlgorithm().equals(algo)) {
				return e;
			}
		}
		return null;
	}

	private String algorithm;

	private String ref;

	private HmacAlgorithm(String algo, String ref) {
		this.algorithm = algo;
		this.ref = ref;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	public String getRef() {
		return ref;
	}
}
