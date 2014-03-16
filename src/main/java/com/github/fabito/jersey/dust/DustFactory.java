package com.github.fabito.jersey.dust;

import java.io.InputStream;

public class DustFactory {

	private static final String DUST_FULL_2_3_4_MIN_JS = "/dust-full-2.3.4.min.js";

	public static Dust rhinoDust() {
		InputStream dustStream = Dust.class.getResourceAsStream(DUST_FULL_2_3_4_MIN_JS);
		return new RhinoDust(dustStream);
	}

}
