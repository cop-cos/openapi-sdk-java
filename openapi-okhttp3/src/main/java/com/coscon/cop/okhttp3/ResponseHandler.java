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

import java.io.IOException;


import okhttp3.Response;

/**
 * Handler that encapsulates the process of generating a response object
 * from a {@link Response}
 * @author <a href="mailto:chenjp2@coscon.com">Chen Jipeng</a>
 *
 */
public interface ResponseHandler<T> {
	/**
	 *Processes an {@link Response} and returns some value
     * corresponding to that response.
     *
     * @param response The response to process
     * @return A value determined by the response
     *
     * @throws IOException in case of a problem or the connection was aborted
	 */
	T handleResponse(Response response) throws IOException;
}
