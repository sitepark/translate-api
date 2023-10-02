package com.sitepark.translate.provider.deepl;

public final class JsonResult<T, U> {

	public static <T, U> JsonResult<T, U> success(int statusCode, T successValue) {
		assert successValue != null : "successValue is null";
		return new JsonResult<>(statusCode, successValue, null);
	}

	public static <T, U> JsonResult<T, U> error(int statusCode, U errorValue) {
		assert errorValue != null : "errorValue is null";
		return new JsonResult<>(statusCode, null, errorValue);
	}

	private final int statusCode;
	private final T successValue;
	private final U errorValue;

	private JsonResult(int statusCode, T successValue, U errorValue) {
		this.statusCode = statusCode;
		this.successValue = successValue;
		this.errorValue = errorValue;
	}

	public boolean isSuccess() {
		return errorValue == null;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public T getSuccessValue() {
		if (!isSuccess()) {
			throw new IllegalStateException("No success value: JsonResult represents a non-successful result");
		}
		return successValue;
	}

	public U getErrorValue() {
		if (isSuccess()) {
			throw new IllegalStateException("No error value: JsonResult represents a successful result");
		}
		return errorValue;
	}
}
