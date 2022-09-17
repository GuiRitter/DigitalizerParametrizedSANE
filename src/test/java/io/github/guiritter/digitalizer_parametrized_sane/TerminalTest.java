package io.github.guiritter.digitalizer_parametrized_sane;

import static java.lang.System.out;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.github.guiritter.external_execution_output_treatment.ExternalExecutionOutputTreatment;

public class TerminalTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		var terminal = new ExternalExecutionOutputTreatment() {
			@Override
			public void treatErrorLine(String line) {
				out.println(line);
			}

			@Override
			public void treatInputLine(String line) {
				out.println(line);
			}
		};

		terminal.execute("mkdir", "/home/guir/test with space");
	}
}
