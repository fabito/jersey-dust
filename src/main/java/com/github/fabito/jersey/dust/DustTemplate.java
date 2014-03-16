package com.github.fabito.jersey.dust;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DustTemplate {

	String name;
	String source;
	String compiledSource;
	Boolean compiled;

	public DustTemplate(String name, String source) {
		super();
		this.name = name;
		this.source = source;
	}

	public DustTemplate(String path, InputStream resource) {
		this(path, getStringFromInputStream(resource));
	}

	public String name() {
		return name;
	}

	public String template() {
		return source;
	}

	public String compiled() {
		return compiledSource;
	}

	public void compiled(String compiled2) {
		compiledSource = compiled2;

	}

	public boolean isCompiled() {
		return compiledSource != null && compiledSource.length() > 0; 
	}

	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}