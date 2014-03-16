package com.github.fabito.jersey.dust;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ViewProcessor;

@Provider
public class DustViewProcessor implements ViewProcessor<DustTemplate> {

	private static final Logger logger = Logger.getLogger(DustViewProcessor.class.getName());

	@Context
	private ServletContext servletContext;

	@Context 
	private ResourceConfig resourceConfig;
	
	private String basePath = "/WEB-INF/dust";

	private Dust dust;
	
	@Inject
	public DustViewProcessor(Dust dust) {
		this.dust = dust;
		logger.info(this.getClass().getSimpleName() + " installed.");
//		String path = (String) resourceConfig.getProperties().get(
//				"DUST_TEMPLATES_DEFAULT_PATH");
//		
//		logger.fine("Root path got from resourceConfig: " + path);
//		
//		if (path == null)
//			this.basePath = "";
//		else if (path.charAt(0) == '/') {
//			this.basePath = path;
//		} else {
//			this.basePath = "/" + path;
//		}
		
		logger.fine("BasePath after initialization: " + basePath);
	}

	public DustTemplate resolve(String path) {
        logger.finer( "Resolving dust template path (" + path + ")" );
        String filePath = path.substring(path.lastIndexOf('.') + 1 );
        filePath = filePath.endsWith( "dust" ) ? filePath : filePath + ".dust";

	     if (servletContext == null) {
	    	 return null;
	     }

		try {
            final String fullPath = basePath + filePath;
            InputStream resource = servletContext.getResourceAsStream(fullPath);
			if (resource != null) {
                logger.finer( "[Template found at: " + fullPath + "]" );
                return dustTemplate(resource, fullPath);
			} else {
                logger.warning( "Template not found [Given path: " + path + "] " +
                        "[Servlet context path: " + fullPath + "]" );
                return null;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	private DustTemplate dustTemplate(InputStream resource, String path) throws Exception {
		DustTemplate dustTemplate = new DustTemplate(path, resource);
		dust.loadSource(dustTemplate);
		return dustTemplate;
	}

	@Override
	public void writeTo(DustTemplate t, Viewable viewable, OutputStream out)
			throws IOException {

        logger.finer( "Evaluating dust template (" + t.name() + ") with model of type " +
                   ( viewable.getModel() == null ? "null" : viewable.getModel().getClass().getSimpleName() ) );
		// Commit the status and headers to the HttpServletResponse
		out.flush();
		OutputStreamWriter osw = new OutputStreamWriter(out);
		try {
			dust.render(t, viewable.getModel(), osw);
			osw.flush();
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			osw.close();
		}
	}
}