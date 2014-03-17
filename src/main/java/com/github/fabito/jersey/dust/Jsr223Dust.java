package com.github.fabito.jersey.dust;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class Jsr223Dust extends AbstractDust  {

	Object dust;
	ScriptEngine engine;
	
	Jsr223Dust(Reader reader) throws ScriptException {
		engine = getJavaScriptEngine();
        engine.eval(reader);
        this.dust = engine.get("dust");
	}
	
	Jsr223Dust(InputStream dustStream) throws ScriptException {
		this(new InputStreamReader(dustStream));
	}
	
	private ScriptEngine getJavaScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("JavaScript");
	}

	Invocable invocable() {
		return (Invocable) engine;
	}

	@Override
	String doCompile(DustTemplate dustTemplate) throws Exception {
		String compiled = invocable().invokeMethod(dust, "compile", dustTemplate.template(), dustTemplate.name()).toString();
		dustTemplate.compiled(compiled);
		return compiled;
	}

	@Override
	void doLoadSource(DustTemplate dustTemplate) throws Exception {
		invocable().invokeMethod(dust, "loadSource", dustTemplate.compiled());;
	}

	@Override
	void doRender(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("writer", stringWriter);
		bindings.put("json", mapper.writeValueAsString(contextObject));
		bindings.put("name", dustTemplate.name());
		engine.eval(RENDER_SCRIPT, bindings);
	}

}