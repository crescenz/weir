package it.uniroma3.weir.configuration;

import it.uniroma3.weir.model.Experiment;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import it.uniroma3.preferences.Constant;
/**
 * An immutable class that uses a singleton paradigm to model a
 * configuration: these are a set of generic system-wide configuration
 * properties,  i.e., they should not be related to an experiment.
 * <br/>
 * It checks that's loaded only once for each execution.
 *
 */
public class WeirConfig {

	static private WeirConfig instance;
	
	private Configuration configuration;
	
	private Experiment current; // the experiment currently executed

	private WeirConfig(Configuration config) {
		/* default values in the configuration file with
		 * higher priority than built-in default values  */
		CompositeConfiguration cc = new CompositeConfiguration();
		cc.addConfiguration(config);
		cc.addConfiguration(Constants.defaultConfiguration());
		this.configuration = cc;
	}

	static public WeirConfig load(URL config) throws ConfigurationException {
		if (instance!=null)
			throw new IllegalStateException("Configuration was already loaded!");
		return instance = new WeirConfig(new PropertiesConfiguration(config));
	}

	static public WeirConfig getInstance() {
		if (instance==null)
			throw new IllegalStateException("Configuration not loaded yet!");
		return instance;
	}

	static public Experiment getCurrentExperiment() {
		return getInstance().current;
	}

	public void setCurrentExperiment(Experiment current) {
		this.current = current;
	}

	static public Configuration getConfiguration() {
		return getInstance().configuration;
	}
	
	static public String getString(Constant name) {
		return getConfiguration().getString(name.key());		
	}

	static public int getInteger(Constant name) {
		return getConfiguration().getInt(name.key());		
	}
		
	static public boolean getBoolean(Constant name) {
		return getConfiguration().getBoolean(name.key());		
	}
	
	static public double getDouble(Constant name) {
		return getConfiguration().getDouble(name.key());		
	}
	
	static public List<String> getList(Constant name) {
		return Arrays.asList(getConfiguration().getStringArray(name.key()));
	}
	
	static public <E extends Enum<E>> List<E> getEnumList(Class<E> cls, Constant name) {
		List<E> result = new LinkedList<>();
		for (String ename : getList(name)) {
			result.add(Enum.valueOf(cls,ename.toUpperCase()));
		}
		return result;
	}
	
	// JFT: just for testing
	static public void reset() {
		instance = null;
	}

	// JFT: just for testing
	public void  setProperty(Constant name, String value) {
		this.configuration.setProperty(name.key(), value);
	}

}
