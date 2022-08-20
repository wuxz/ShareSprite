package com.zhuaiwa.api.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.RpcCallback;

public class FutureResponse<T> implements RpcCallback<T>, Future<T> {
	Object data;
	T t;
	FutureTask<T> ft;
	
	public FutureResponse() {
		ft = new FutureTask<T>(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return t;
			}
		});
	}

	@Override
	public void run(T t) {
		this.t = t;
		ft.run();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return ft.cancel(mayInterruptIfRunning);
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return ft.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return ft.get(timeout, unit);
	}

	@Override
	public boolean isCancelled() {
		return ft.isCancelled();
	}

	@Override
	public boolean isDone() {
		return ft.isDone();
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
