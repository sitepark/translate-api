package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JsonResultTest {

	@Test
	void testErrorResult() {

		JsonResult<Object, String> result = JsonResult.error(400, "test");

		assertEquals(400, result.getStatusCode(), "unexpected status code");
		assertThrows(IllegalStateException.class, () -> {
			result.getSuccessValue();
		}, "success value should not returned");
		assertEquals("test", result.getErrorValue());
		assertFalse(result.isSuccess(), "the result should not be a success");
	}

	@Test
	void testNullErrorValue() {
		assertThrows(AssertionError.class, () -> {
			JsonResult.error(400, null);
		}, "null error not allowed");
	}

	@Test
	void testSuccessResult() {

		JsonResult<String, Object> result = JsonResult.success(200, "test");

		assertEquals(200, result.getStatusCode(), "unexpected status code");
		assertThrows(IllegalStateException.class, () -> {
			result.getErrorValue();
		}, "error value should not returned");
		assertEquals("test", result.getSuccessValue());
		assertTrue(result.isSuccess(), "the result should be a success");
	}

	@Test
	void testNullSuccessValue() {
		assertThrows(AssertionError.class, () -> {
			JsonResult.success(400, null);
		}, "null error not allowed");
	}


}
