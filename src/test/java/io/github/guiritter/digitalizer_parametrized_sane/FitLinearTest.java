package io.github.guiritter.digitalizer_parametrized_sane;

import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.FIT_LINEAR_X;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.FIT_LINEAR_Y;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.SANE_X_MAX;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.SANE_X_MAX_STRING;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.SANE_Y_MAX;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.SANE_Y_MAX_STRING;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.WIA_PAPER_X_MAX;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.WIA_PAPER_X_MIN;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.WIA_PAPER_Y_MAX;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.WIA_PAPER_Y_MIN;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.xToString;
import static io.github.guiritter.digitalizer_parametrized_sane.DigitalizerParametrizedSANE.yToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FitLinearTest {
	
	@Test
	public void test() {
		assertEquals(0, FIT_LINEAR_X.f(WIA_PAPER_X_MIN), 0.01);
		assertEquals(SANE_X_MAX.doubleValue(), FIT_LINEAR_X.f(WIA_PAPER_X_MAX), 0.01);

		assertEquals(0, FIT_LINEAR_Y.f(WIA_PAPER_Y_MIN), 0.0001);
		assertEquals(SANE_Y_MAX.doubleValue(), FIT_LINEAR_Y.f(WIA_PAPER_Y_MAX), 0.0001);
	}

	@Test
	public void toStringTest() {
		assertEquals(SANE_X_MAX_STRING, xToString(SANE_X_MAX.doubleValue()));
		assertEquals(SANE_Y_MAX_STRING, yToString(SANE_Y_MAX.doubleValue()));
		assertEquals(SANE_X_MAX_STRING, xToString(215.89));
		assertEquals(SANE_X_MAX_STRING, xToString(215.91));
	}
}
