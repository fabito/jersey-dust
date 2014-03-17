package com.github.fabito.jersey.dust;

public class Rhino2DustTest extends AbstractDustTest {

	@Override
	Dust dust() {
		return DustFactory.rhino2Dust();
	}

}