package com.github.fabito.jersey.dust;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class AbstractDust implements Dust {

	protected static final String RENDER_SCRIPT = "{ dust.render( name, JSON.parse(json), function( err, data) { if(err) { throw new Error(err);} else { writer.write( data );}});}";

	private static final Logger logger = Logger.getLogger(Jsr223Dust.class
			.getName());
	
	private Map<String, DustTemplate> registeredTemplates = new HashMap<>();
	
	//FIXME remove objectmapper dependency and avoid json seerialization and desserialization
	protected ObjectMapper mapper = new ObjectMapper();

	public AbstractDust() {
		super();
	}

	@Override
	public String compile(DustTemplate dustTemplate) throws Exception {
		logger.fine("Compiling template: " + dustTemplate);
		String compiled = doCompile(dustTemplate);
		dustTemplate.compiled(compiled);
		logger.fine("Compiled template: " + dustTemplate);
		return compiled;
	}

	abstract String doCompile(DustTemplate dustTemplate) throws Exception;

	@Override
	public void loadSource(DustTemplate dustTemplate) throws Exception {
		logger.fine("Registering template: ." + dustTemplate);
		if (!dustTemplate.isCompiled()) {
			logger.fine("Template not compiled yet.");
			compile(dustTemplate);
		}
		doLoadSource(dustTemplate);
		registeredTemplates.put(dustTemplate.name(), dustTemplate);
	}

	abstract void doLoadSource(DustTemplate dustTemplate) throws Exception;

	@Override
	public void render(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		logger.fine("Rendering template: ." + dustTemplate);
		if (!registeredTemplates.containsKey(dustTemplate.name())) {
			logger.fine("Template not registered.");
			loadSource(dustTemplate);
		}
		doRender(dustTemplate, contextObject, stringWriter);
	}

	abstract void doRender(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception;

}