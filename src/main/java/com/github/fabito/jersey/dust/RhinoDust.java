package com.github.fabito.jersey.dust;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

/**
 * Dust implementation
 * @author fabio
 * @see <a href="https://gist.github.com/vybs/1624130">Gist</a>
 */
class RhinoDust extends AbstractDust {
	
	public final String MODULE = RhinoDust.class.getName();
	private Scriptable globalScope;

	RhinoDust(InputStream dustStream) {
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

	@Override
	String doCompile(DustTemplate dustTemplate) throws Exception {
		return compileTemplate(dustTemplate.name(), dustTemplate.template());
	}

	@Override
	void doLoadSource(DustTemplate dustTemplate) throws Exception {
		loadTemplate(dustTemplate.name(), dustTemplate.template());
	}

	@Override
	void doRender(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		render(dustTemplate.name(), mapper.writeValueAsString(contextObject), stringWriter);
	}
		
	protected String compileTemplate(String name, String rawSource) {
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

	protected void loadTemplate(String name, String rawSource) {

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

	protected void render(String name, String json, Writer writer) {
		Context dustContext = Context.enter();

		Scriptable renderScope = dustContext.newObject(globalScope);
		renderScope.setParentScope(globalScope);

		try {
			renderScope.put("writer", renderScope, writer);
			renderScope.put("json", renderScope, json);
			renderScope.put("name", renderScope, name);

			dustContext.evaluateString(renderScope, RENDER_SCRIPT,
					"JDustCompiler", 0, null);

		} catch (JavaScriptException e) {
			// Fail hard on any render time error for dust templates
			throw new RuntimeException(e);
		} finally {
			Context.exit();
		}
	}

}
