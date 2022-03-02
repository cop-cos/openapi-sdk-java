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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import com.coscon.cop.common.CopClientSDKException;
import com.coscon.cop.core.SignAlgorithm;

/**
 * Hmac utility.
 * 
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 * @emailto ch_jp@msn.com
 */
public class HmacShaUtil {
	private HmacShaUtil() {
		
	}
	/**
	 * Create a signature for specific data with given algorithm and key.
	 * 
	 * @param hmacAlgorithm
	 * @param data
	 *            source data in bytes
	 * @param key
	 *            the secretkey
	 * @return bytes represents the hashing signature result.
	 * @throws ClientException
	 */
	public static byte[] signature(String hmacAlgorithm, byte[] data, String key) throws CopClientSDKException {
		try {
			SignAlgorithm algo = SignAlgorithm.validate(hmacAlgorithm);
			SecretKeySpec signingKey = new SecretKeySpec(StringUtils.getBytesUtf8(key), algo.getAlgorithm());
			Mac mac = Mac.getInstance(algo.getAlgorithm());
			mac.init(signingKey);
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CopClientSDKException(e.getMessage(), e);
		}
	}
	/**
	 * Create a signature for specific data.
	 * 
	 * @param hmacAlgorithm
	 * @param data
	 *            source data
	 * @param key
	 *            the secret key
	 * @return hexString represents the hashing signature result.
	 * @throws ClientException
	 */
	public static String signatureHex(String hmacAlgorithm, byte[] data, String key)
			throws CopClientSDKException{
		return Hex.encodeHexString(signature(hmacAlgorithm, data, key));
	}
}
