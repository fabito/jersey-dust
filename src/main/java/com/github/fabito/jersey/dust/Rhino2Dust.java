package com.github.fabito.jersey.dust;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

/**
 * Dust implementation
 * @author fabio
 * @see <a href="https://gist.github.com/vybs/1624130">Gist</a>
 */
class Rhino2Dust extends AbstractDust {
	
	private static final String DEFAULT_SOURCE_NAME = "JDustCompiler";
	public final String MODULE = Rhino2Dust.class.getName();
	private Scriptable globalScope;
	private NativeObject dust;
	private Function compile;
	private Function render;
	private Function loadSource;
	private Function callbackTemplate;

	Rhino2Dust(InputStream dustStream) {
		try {
			Reader dustReader = new InputStreamReader(dustStream, "UTF-8");
			Context dustEngineContext = Context.enter();
			dustEngineContext.setOptimizationLevel(9);
			try {
				globalScope = dustEngineContext.initStandardObjects();
				dustEngineContext.evaluateReader(globalScope, dustReader,
						"dust-full.min.js", 0, null);
				dustEngineContext.evaluateString(globalScope, "var dustRenderCallback = function(writer) {return function(err, data) {if(err) {throw new Error(err);}else {writer.write( data );}}}", DEFAULT_SOURCE_NAME, 0, null);
				Object dustObj = globalScope.get("dust", globalScope);
				if (!(dustObj instanceof NativeObject)) {
	                throw new IllegalStateException("dust is undefined.");
	            }
				dust = (NativeObject) dustObj;
				compile = (Function) dust.get("compile", globalScope);
				render = (Function) dust.get("render", globalScope);
				loadSource = (Function) dust.get("loadSource", globalScope);
				callbackTemplate = (Function) globalScope.get("dustRenderCallback", globalScope);
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
		Context dustContext = Context.enter();
		Scriptable compileScope = dustContext.newObject(globalScope);
		compileScope.setParentScope(globalScope);
		Object functionArgs[] = { dustTemplate.template(), dustTemplate.name() };
		Object result = compile.call(dustContext, compileScope, compileScope, functionArgs);
		Context.exit();
		return Context.toString(result);
	}
	
	@Override
	void doLoadSource(DustTemplate dustTemplate) throws Exception {
		Context dustContext = Context.enter();
		Scriptable compileScope = dustContext.newObject(globalScope);
		compileScope.setParentScope(globalScope);
		Object functionArgs[] = { dustTemplate.compiled() };
		loadSource.call(dustContext, compileScope, compileScope, functionArgs);
		Context.exit();
	}

	@Override
	void doRender(DustTemplate dustTemplate, Object contextObject,
			Writer stringWriter) throws Exception {
		Context dustContext = Context.enter();
		Scriptable compileScope = dustContext.newObject(globalScope);
		Function cb = (Function) callbackTemplate.construct(dustContext, compileScope, new Object[] { stringWriter });
		compileScope.setParentScope(globalScope);
		globalScope.put("writer", globalScope, stringWriter);
		NativeObject ctx = new JSONUtils().toObject(mapper.writeValueAsString(contextObject));
		Object functionArgs[] = { dustTemplate.name(), ctx, cb };
		render.call(dustContext, compileScope, compileScope, functionArgs);
		Context.exit();
	}


}
