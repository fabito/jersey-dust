package com.github.fabito.jersey.dust;

import java.io.InputStream;

import javax.script.ScriptException;

public class DustFactory {

	private static final String DUST_FULL_2_3_4_MIN_JS = "/dust-full-2.3.4.min.js";

	public static Dust rhinoDust() {
		return new RhinoDust(inputStream());
	}

	private static InputStream inputStream() {
		return Dust.class.getResourceAsStream(DUST_FULL_2_3_4_MIN_JS);
	}

	public static Dust jsr223Dust() throws ScriptException {
		return new Jsr223Dust(inputStream());
	}

}
