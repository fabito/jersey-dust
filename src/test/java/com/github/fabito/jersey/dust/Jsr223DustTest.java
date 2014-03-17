package com.github.fabito.jersey.dust;



public class Jsr223DustTest extends AbstractDustTest {

	@Override
	Dust dust() throws Exception {
		return DustFactory.jsr223Dust();
	}

}
