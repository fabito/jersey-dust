package com.github.fabito.jersey.dust;

public class RhinoDustTest extends AbstractDustTest {

	@Override
	Dust dust() {
		return DustFactory.rhinoDust();
	}

}