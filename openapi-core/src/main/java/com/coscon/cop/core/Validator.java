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

import java.io.IOException;

/**
 * @author Chen Jipeng / chenjp2@coscon.com
 *
 */
public interface Validator {
	/**
	 * Validate response.
	 * 
	 * @param response
	 *            from cop server.
	 * @return result returns <code>true</code> if the response is valid.
	 * @throws IOException
	 */
	public boolean validate(Object response) throws IOException;
}
