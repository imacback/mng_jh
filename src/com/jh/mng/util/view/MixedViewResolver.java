package com.jh.mng.util.view;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class MixedViewResolver implements ViewResolver {

	private static final Logger			logger	= Logger.getLogger(MixedViewResolver.class);
	
	private Map<String, ViewResolver> resolvers;
	private String defaultResolver = "jsp";

	public void setResolvers(Map<String, ViewResolver> resolvers)
	{
		this.resolvers = resolvers;
	}

	public View resolveViewName(String viewName, Locale locale) throws Exception
	{
		int n = viewName.lastIndexOf('.');

		if (n == (-1)) throw new Exception("No ViewResolver");

		String suffix = viewName.substring(n + 1);
		ViewResolver resolver = resolvers.get(suffix);

		if (resolver != null)return resolver.resolveViewName(viewName, locale);
		
		logger.warn("No ViewResolver for " + suffix);
		resolver = resolvers.get(defaultResolver);
		return resolver.resolveViewName(viewName, locale);

		//throw new Exception("No ViewResolver for " + suffix);
	}

	public String getDefaultResolver()
	{
		return defaultResolver;
	}

	public void setDefaultResolver(String defaultResolver)
	{
		this.defaultResolver = defaultResolver;
	}

}
