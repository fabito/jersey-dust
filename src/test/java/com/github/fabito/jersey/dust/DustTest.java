package com.github.fabito.jersey.dust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class DustTest {

	
	private static final String DUST_FULL_2_3_4_MIN_JS = "/dust-full-2.3.4.min.js";
	private Dust dust;

	@Test
	public void test() throws Throwable {
		InputStream dustStream = DustTest.class.getResourceAsStream(DUST_FULL_2_3_4_MIN_JS);
		DustEngine  dustEngine  = new DustEngine(dustStream);
//		System.out.println(DustEngine.compileTemplate("hello_world", "Hello World!"));
		
		dustEngine.loadTemplate("hello_world", "Hello World!");
		dustEngine.loadTemplate("reference", "{?one}{one}{/one}");
		
		StringWriter writer = new StringWriter();
		dustEngine.render("hello_world", "{}" , writer);
		writer.close();
		assertEquals("Hello World!", writer.toString());
		
		writer = new StringWriter();
		dustEngine.render("reference", "{\"one\": 0 }" , writer);
		writer.close();
		assertEquals("0", writer.toString());
	
//		writer = new StringWriter();
//		dustEngine.render("reference2222", "{\"one\": 0 }" , writer);
		
	}

	@Before
	public void setup() throws Throwable {
		InputStream dustStream = DustTest.class.getResourceAsStream(DUST_FULL_2_3_4_MIN_JS);
		dust = new Jsr223Dust(dustStream);
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
	
	public static class One {
		Integer one = 0;
		public Integer getOne() {
			return one;
		}
	}

}
