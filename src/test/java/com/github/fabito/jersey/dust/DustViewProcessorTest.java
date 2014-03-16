package com.github.fabito.jersey.dust;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.view.Viewable;

public class DustViewProcessorTest {

	private static final Logger logger = Logger.getLogger(DustViewProcessorTest.class.getName());

	
	private Dust dust;
    private DustViewProcessor viewProcessor;
    
    @Before
    public void init() {
    	dust = DustFactory.rhinoDust();
    	viewProcessor = new DustViewProcessor(dust);
    }
    
    @Test
    public void shouldResolveToNull() throws Exception {
        Viewable viewable = new Viewable("non.existent.template", new Context());
        DustTemplate template = viewProcessor.resolve(viewable.getTemplateName());
        assertNull(template);
    }

    @Test
    public void testResolveAndWriteTo() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Viewable viewable = new Viewable("/hello_world", new Context());
        DustTemplate template = viewProcessor.resolve(viewable.getTemplateName());
        logger.info(template.toString());
        viewProcessor.writeTo(template, viewable, stream);
        assertEquals("Hello World!!!", stream.toString());
    }
    
    public static class Context {
        public String value = "foo";
    }
}
