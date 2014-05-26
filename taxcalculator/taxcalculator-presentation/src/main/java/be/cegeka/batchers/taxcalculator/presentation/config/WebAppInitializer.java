package be.cegeka.batchers.taxcalculator.presentation.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

	public static final String DISPATCHER_SERVLET_NAME = "dispatcher";
	public static final String DISPATCHER_SERVLET_MAPPING = "/rest/*";

	private static final Logger LOG = LoggerFactory.getLogger(WebAppInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) {
		setUpAppEnvToDefaultIfNotProvided();

		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(WebAppContext.class);

		ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(ctx));
		dispatcherServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = dispatcherServlet.addMapping(DISPATCHER_SERVLET_MAPPING);
		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				LOG.error("Mapping conflict: " + s);
			}
			throw new IllegalStateException("'webservice' cannot be mapped to '/'");
		}
	}

	private void setUpAppEnvToDefaultIfNotProvided() {
		if (isBlank(System.getProperty("APP_ENV"))) {
			System.setProperty("APP_ENV", "default");
		}
		if (isBlank(System.getProperty("log_dir"))) {
			System.setProperty("log_dir", "target/logs/");
		}
	}
}
