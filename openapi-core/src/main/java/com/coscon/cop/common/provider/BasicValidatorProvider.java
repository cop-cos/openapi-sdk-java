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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.coscon.cop.common.Namespace;

/**
 * @author Chen Jipeng
 *
 */
public class BasicValidatorProvider implements ValidatorProvider {
	private final ConcurrentHashMap<Namespace, Validator> validatorMap;
	/**
	 * 
	 */
	public BasicValidatorProvider() {
		super();
		this.validatorMap = new ConcurrentHashMap<>();
	}
	@Override
	public void setValidator(Namespace namespace, Validator validator) {
		validatorMap.put(namespace, validator);
	}
	@Override
	public Validator getValidator(Namespace namespace) {
		return validatorMap.get(namespace);
	}
	@Override
	public Validator getValidator(String url) {
		Objects.requireNonNull(url, "url may not be null");
		for (Map.Entry<Namespace, Validator> e : validatorMap.entrySet()) {
			if(url.toLowerCase().startsWith(e.getKey().getRootUrl().toLowerCase())) {
				return e.getValue();
			}
		}
		return null;
	}
	@Override
	public void clear() {
		validatorMap.clear();
	}
	
}
