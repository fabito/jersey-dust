package com.github.fabito.jersey.dust;


import java.io.Writer;

/**
 * @author fabio
 * FIXME make methods throw DustException
 */
public interface Dust {

	String compile(DustTemplate dustTemplate) throws Exception;

	void loadSource(DustTemplate dustTemplate) throws Exception;

	void render(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception;

}
