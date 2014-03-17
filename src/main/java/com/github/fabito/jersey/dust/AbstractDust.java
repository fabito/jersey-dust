package com.github.fabito.jersey.dust;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDust implements Dust {

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
		return doCompile(dustTemplate);
	}

	abstract String doCompile(DustTemplate dustTemplate) throws Exception;

	@Override
	public void loadSource(DustTemplate dustTemplate) throws Exception {
		if (!dustTemplate.isCompiled()) {
			compile(dustTemplate);
		}
		doLoadSource(dustTemplate);
		registeredTemplates.put(dustTemplate.name(), dustTemplate);
	}

	abstract void doLoadSource(DustTemplate dustTemplate) throws Exception;

	@Override
	public void render(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		doRender(dustTemplate, contextObject, stringWriter);
	}

	abstract void doRender(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception;

}