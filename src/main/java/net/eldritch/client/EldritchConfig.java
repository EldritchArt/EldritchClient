package net.eldritch.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;

public class EldritchConfig {
	private HashMap<String, HashMap<String, String>> configuration;
	
	public EldritchConfig() {
		configuration = new HashMap<String, HashMap<String, String> >();
		
		loadConfigFile();
	}
	
	final String configFilename = "./config/EldritchConfig.cfg";
	/**
	 * Loads a config file already created. Call only at the start, as others make references to the internal hashmaps!!!
	 */
	public void loadConfigFile() {
		File configFile = new File(configFilename);
		if (!configFile.exists()) {
			try {configFile.createNewFile(); } catch (IOException e) {e.printStackTrace();}
		}
		
		try {
			Scanner lineScanner = new Scanner(configFile);
			
			String group = null;
			HashMap<String, String> groupOptions = null;
			//read all the options. A $ indicates a new group
			while (lineScanner.hasNextLine()) {
				String line = lineScanner.nextLine();
				if (0==line.length() || '#'==line.charAt(0)) continue;
				if ('$'==line.charAt(0)) {
					if (null != group) configuration.put(group, groupOptions);
					group = line.substring(1);
					groupOptions = new HashMap<String, String>();
				} else {
					setOption(groupOptions, line);
				}
			}
			if (null != group) configuration.put(group, groupOptions);
			
			lineScanner.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveConfigFile() {
		File configFile = new File(configFilename);
		if (!configFile.exists()) throw new IllegalStateException();
		
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
			writer.print("");
			
			Set set = configuration.entrySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext()) {
				//save the heading, then the contents
				Map.Entry mentry = (Map.Entry)iterator.next();
				String group = (String)mentry.getKey();
				HashMap<String, String> groupOptions = (HashMap<String, String>) mentry.getValue();
				writer.println("$"+group);
				Set set2 = groupOptions.entrySet();
				Iterator iterator2 = set2.iterator();
				while (iterator2.hasNext()) {
					Map.Entry mentry2 = (Map.Entry) iterator2.next();
					writer.println(mentry2.getKey()+":"+mentry2.getValue());
				}
				writer.println();
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called by each thing that requires configuration to show what that configuration's default is
	 */
	public void initializeOptions(String group, String[] options) {
		if (configuration.containsKey(group)) return;
		
		HashMap<String, String> groupOptions = new HashMap<String, String>();
		for (String option : options) setOption(groupOptions, option);
		
		configuration.put(group, groupOptions);
	}
	
	/**
	 * Takes in an option of the form "name:value" and sets it accordingly
	 */
	public static void setOption(HashMap<String, String> groupOptions, String option) {
		String[] options = option.split(":");
		if (options.length != 2) throw new IllegalStateException();
		if (null == groupOptions) throw new IllegalStateException();
		groupOptions.put(options[0], options[1]);
	}
	
	/**
	 * Returns the hashmap associated with a particular group. Group must be initialized.
	 */
	public HashMap<String, String> getOptionGroup(String group) {
		if (null==group) throw new IllegalStateException();
		return configuration.get(group);
	}
	
	public String[] getOptionGroups() {
		String[] retval = new String[configuration.size()];
		Set set = configuration.entrySet();
		Iterator iterator = set.iterator();
		int i = 0;
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			retval[i++] = (String)mentry.getKey();
		}
		return retval;
	}
}
