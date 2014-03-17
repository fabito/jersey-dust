package com.github.fabito.jersey.dust;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsr223Dust implements Dust {

	//FIXME 
	private static final String RENDER_SCRIPT = "{ dust.render( name, JSON.parse(json), function( err, data) { if(err) { throw new Error(err);} else { writer.write( data );}});}";

	private static final Logger logger = Logger.getLogger(Jsr223Dust.class.getName());
	
	private Object dust;
	private ScriptEngine engine;
	private Map<String, DustTemplate> registeredTemplates = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();

	Jsr223Dust(Reader reader) throws ScriptException {
		engine = getJavaScriptEngine();
        engine.eval(reader);
        this.dust = engine.get("dust");
	}
	
	Jsr223Dust(InputStream dustStream) throws ScriptException {
		this(new InputStreamReader(dustStream));
	}
	
	@Override
	public String compile(DustTemplate dustTemplate) throws Exception {
		String compiled = invocable().invokeMethod(dust, "compile", dustTemplate.template(), dustTemplate.name()).toString();
		dustTemplate.compiled(compiled);
		return compiled;
	}

	@Override
	public void loadSource(DustTemplate dustTemplate) throws Exception {
		if (!dustTemplate.isCompiled()) {
			compile(dustTemplate);
		}
		invocable().invokeMethod(dust, "loadSource", dustTemplate.compiled());
		registeredTemplates.put(dustTemplate.name(), dustTemplate);
	}

	@Override
	public void render(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("writer", stringWriter);
		bindings.put("json",  mapper.writeValueAsString(contextObject));
		bindings.put("name",  dustTemplate.name());
		engine.eval(RENDER_SCRIPT, bindings);
	}

	private ScriptEngine getJavaScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("JavaScript");
	}

	private Invocable invocable() {
		return (Invocable) engine;
	}

}