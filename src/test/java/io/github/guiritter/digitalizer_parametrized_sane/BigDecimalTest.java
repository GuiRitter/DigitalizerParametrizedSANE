package io.github.guiritter.digitalizer_parametrized_sane;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class BigDecimalTest {

	@Test
	public void testComparable() {
		var list = Arrays.stream(new BigDecimal[] { TEN, ONE }).sorted().collect(toList());

		assertEquals(ONE, list.get(0));
		assertEquals(TEN, list.get(1));
	}

	@Test
	public void testToString() {
		assertEquals("10", TEN + "");
	}
}
