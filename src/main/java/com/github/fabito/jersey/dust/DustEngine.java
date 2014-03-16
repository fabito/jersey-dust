package com.github.fabito.jersey.dust;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DustEngine implements Dust {
	
	public final String MODULE = DustEngine.class.getName();
	private Scriptable globalScope;
	private ObjectMapper mapper = new ObjectMapper();
	

	public DustEngine(InputStream dustStream) {
		try {
			Reader dustReader = new InputStreamReader(dustStream, "UTF-8");
			Context dustEngineContext = Context.enter();
			dustEngineContext.setOptimizationLevel(9);
			try {
				globalScope = dustEngineContext.initStandardObjects();
				dustEngineContext.evaluateReader(globalScope, dustReader,
						"dust-full.min.js", 0, null);
			} finally {
				Context.exit();
			}
		} catch (IOException ex) {
			throw new RuntimeException(
					" ERROR : Unable to load dust engine resource: ", ex);
		}
	}

	public String compileTemplate(String name, String rawSource) {
		Context dustContext = Context.enter();
		try {
			Scriptable compileScope = dustContext.newObject(globalScope);
			compileScope.setParentScope(globalScope);
			compileScope.put("rawSource", compileScope, rawSource);
			compileScope.put("name", compileScope, name);

			try {
				return (String) dustContext.evaluateString(compileScope,
						"(dust.compile(rawSource, name))", "JDustCompiler", 0,
						null);
			} catch (JavaScriptException e) {
				// Fail hard on any compile time error for dust templates
				throw new RuntimeException(e);
			}
		} finally {
			Context.exit();
		}
	}

	public void loadTemplate(String name, String rawSource) {

		Context dustContext = Context.enter();
		try {
			Scriptable compileScope = dustContext.newObject(globalScope);
			compileScope.setParentScope(globalScope);
			compileScope.put("rawSource", compileScope, rawSource);
			compileScope.put("name", compileScope, name);

			try {
				dustContext.evaluateString(compileScope,
						"(dust.loadSource(dust.compile(rawSource, name)))",
						"JDustCompiler", 0, null);
			} catch (JavaScriptException e) {
				// Fail hard on any compile time error for dust templates
				throw new RuntimeException(e);
			}
		} finally {
			Context.exit();
		}
	}

	public void render(String name, String json, Writer writer) {
		Context dustContext = Context.enter();

		Scriptable renderScope = dustContext.newObject(globalScope);
		renderScope.setParentScope(globalScope);

		String renderScript = ("{   dust.render( name,  JSON.parse(json) , function( err, data) { if(err) { throw new Error(err);} else { writer.write( data );}  } );   }");

		try {
			renderScope.put("writer", renderScope, writer);
			renderScope.put("json", renderScope, json);
			renderScope.put("name", renderScope, name);

			dustContext.evaluateString(renderScope, renderScript,
					"JDustCompiler", 0, null);

		} catch (JavaScriptException e) {
			// Fail hard on any render time error for dust templates
			throw new RuntimeException(e);
		} finally {
			Context.exit();
		}
	}

	@Override
	public String compile(DustTemplate dustTemplate) throws Exception {
		return compileTemplate(dustTemplate.name(), dustTemplate.template());
	}

	@Override
	public void loadSource(DustTemplate dustTemplate) throws Exception {
		loadTemplate(dustTemplate.name(), dustTemplate.template());
	}

	@Override
	public void render(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		render(dustTemplate.name(), mapper.writeValueAsString(contextObject), stringWriter);
		
	}


}
