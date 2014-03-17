package com.github.fabito.jersey.dust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractDustTest {

	protected Dust dust;

	public static class One {
		Integer one = 0;
		public Integer getOne() {
			return one;
		}
	}

	public AbstractDustTest() {
		super();
	}
	
	@Before
	public void setup() throws Throwable {
		dust = dust();
	}
	
	@Test
	public void compile() throws Throwable {
		DustTemplate dustTemplate = new DustTemplate("hello_world", "Hello World!");
		String compiled = dust.compile(dustTemplate);
		assertNotNull(compiled);
		assertEquals("(function(){dust.register(\"hello_world\",body_0);function body_0(chk,ctx){return chk.write(\"Hello World!\");}return body_0;})();", compiled);
	}

	@Test
	public void loadSource() throws Throwable {
		DustTemplate dustTemplate = new DustTemplate("hello_world", "Hello World!");
		dust.loadSource(dustTemplate);
	}

	@Test
	public void renderWithEmpty() throws Throwable {
		String source = "Hello World!";
		DustTemplate dustTemplate = new DustTemplate("hello_world", source);
		dust.loadSource(dustTemplate);
		StringWriter writer = new StringWriter();
		dust.render(dustTemplate, ImmutableMap.<String, Object>of(), writer );
		assertEquals(source, writer.toString());
	}
	
	@Test
	public void render() throws Throwable {
		DustTemplate dustTemplate = new DustTemplate("reference", "{?one}{one}{/one}");
		dust.loadSource(dustTemplate);
		StringWriter writer = new StringWriter();
		dust.render(dustTemplate, new One(), writer );
		writer.close();
		assertEquals("0", writer.toString());
	}

	@Test
	public void renderWithJavaBean() throws Throwable {
		DustTemplate dustTemplate = new DustTemplate("reference2", "{?one}{two}{/one}");
		dust.loadSource(dustTemplate);
		StringWriter writer = new StringWriter();
		dust.render(dustTemplate, ImmutableMap.<String, Object>of("one",  0, "two", 2), writer );
		writer.close();
		assertEquals("2", writer.toString());
	}

	abstract Dust dust() throws Exception;

}