package it.uniroma3.weir.fixture;

import it.uniroma3.weir.configuration.WeirConfig;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.varia.NullAppender;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class WeirTest {
		
	static final protected String WEIR_CONFIG_FILENAME = "./weir-testing-config.properties";
	
	@BeforeClass
	static public void setUpTestEnvironment() throws ConfigurationException, IOException {
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		System.setProperty("java.util.logging.manager", "it.uniroma3.hlog.HypertextualLogManager");

		/* load WEIR system-wide configuration */
		final URL configURL = ClassLoader.getSystemResource(WEIR_CONFIG_FILENAME);	
		try {
			WeirConfig.load(configURL);
		} catch (ConfigurationException cfgEx) {
			throw new RuntimeException("Cannot read WEIR configuration: "+WEIR_CONFIG_FILENAME,cfgEx);
		}
	}
	
	@AfterClass
	static public void tearDownTestEnviroment() {
		WeirConfig.reset();
	}

}
