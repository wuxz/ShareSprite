/*
 * Copyright (c) 2009 Stephen Tu <stephen_tu@berkeley.edu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.zhuaiwa.api.netty.exception;

import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiNotification;
import com.zhuaiwa.api.Common.ApiRequest;


@SuppressWarnings("serial")
public class RpcException extends Exception {
	
	private final ApiHeader header;
	private final ApiRequest request;
	private final ApiNotification notification;
	
	public RpcException(Throwable t, ApiHeader header, ApiRequest request, String message) {
		this(header, request, message);
		initCause(t);
	}
	
    public RpcException(Throwable t, ApiHeader header, ApiNotification notification, String message) {
        this(header, notification, message);
        initCause(t);
    }
	
	public RpcException(ApiHeader header, ApiRequest request, String message) {
		super(message);
		this.header = header;
		this.request = request;
        this.notification = null;
	}
    
    public RpcException(ApiHeader header, ApiNotification notification, String message) {
        super(message);
        this.header = header;
        this.request = null;
        this.notification = notification;
    }

    public ApiNotification getNotification() {
        return notification;
    }

	public ApiRequest getRequest() {
		return request;
	}

	public ApiHeader getHeader() {
		return header;
	}
	
}
