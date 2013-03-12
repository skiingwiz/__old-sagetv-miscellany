package util.opt;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class OptionParser {
	public enum Action {
		STORE_TRUE,
		STORE_FALSE,
		STORE_VALUE
	}
	
	public static final char NO_SHORTNAME = 0;
	public static final String NO_LONGNAME = null;
	
	private static final String OPTION_HEADER = "Option";
	private static final String DESCRIPTION_HEADER = "Description";

	/**
	 * Break up the string into lines of a length less than
	 * or equal to the given length.
	 * 
	 * @param str The <code>String</code> to be broken
	 * @param len The maximum length of lines
	 * @return An array of <code>String</code>s representing
	 * the given <code>String</code> broken into lines.
	 */
	private static String[] breakupString(String str, int len) {
		BreakIterator iter = BreakIterator.getLineInstance();
		iter.setText(str);
		
		List<String> retVal = new ArrayList<String>();
		
		int start = 0;
		int end;
		
		for(int pos = len; pos <= str.length(); pos += len) {
			end = iter.preceding(pos);
			retVal.add(str.substring(start, end));
			start = end;
		}
		
		if(start < str.length()) {
			retVal.add(str.substring(start));
		}
		
		return retVal.toArray(new String[retVal.size()]);
	}
	
	private Map<Character, Option> shortToOption = new HashMap<Character, Option>();
	private Map<String, Option> longToOption = new HashMap<String, Option>();
	private List<Option> options = new ArrayList<Option>();
	private String usage;
	
	/** The maximum length for lines in the usage message */
	private int lineLength = 80;
	
	/**
	 * Construct a new <code>OptionParser</code>.  This option parser will use
	 * the default help handling and have a maximum line length of 80 characters.
	 */
	public OptionParser() {
		setHelpAware(true);
	}
	
	/**
	 * Get the maximum length of the lines printed by this option parser.
	 * @return The maximum line length
	 */
	public int getLineLength() {
		return lineLength;
	}
	
	/**
	 * Set the maximum length of the lines printed by this option parser.
	 * @param lineLength The maximum line length
	 */
	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
	}
	
	/**
	 * Determine if this parser is help aware.  If it is, it will automatically add options
	 * for -h and --help to print a usage message and exit.
	 * 
	 * @return <code>true</code> if this <code>OptionParser</code> is help aware, 
	 * <code>false</code> if it is not.
	 */
	public boolean isHelpAware() {
		return longToOption.containsKey("help");
	}
	
	/**
	 * Set if this parser is help aware.  If it is, it will automatically add options
	 * for -h and --help to print a usage message and exit.
	 * 
	 * @param helpAware Whether or not this parser should be help aware.
	 */
	public void setHelpAware(boolean helpAware) {
		if(helpAware && !isHelpAware())
			addOption('h', "help", "help", "Print this usage message", Action.STORE_TRUE);
		else if(!helpAware && isHelpAware())
			removeOption("help");
	}
	
	public boolean removeOption(String longName) {
		Option o = longToOption.get(longName);
		longToOption.remove(longName);
		shortToOption.remove(o.shortName);
		
		return options.remove(o);
	}
	
	public void addOption(char shortName, String longName, String value, String desc, Action action) {
		addOption(shortName, longName, value, desc, action, null);
	}
	
	public void addOption(char shortName, String longName, String value, String desc, Action action, Object def) {
		Option o = new Option();
		o.shortName = shortName;
		o.longName = longName;
		o.value = value;
		o.action = action;
		o.def = def;
		o.desc = desc;
		
		if(shortName != NO_SHORTNAME) {
			shortToOption.put(shortName, o);
		}
		
		if(longName != NO_LONGNAME) {
			longToOption.put(longName, o);
		}
		
		options.add(o);
	}
	
	public Options parse(String[] arr) throws InvalidOptionException {
		Options retVal = new Options();
		
		if(arr != null) {
			boolean doneWithOptions = false;
			int positional = 0;
			
			
			for(int i = 0; i < arr.length; i++) {
				String s = arr[i];
				if(s.equals("--")) {
					doneWithOptions = true;
				} else if(doneWithOptions) {
					retVal.addPositional(++positional, s);
				} else if(s.startsWith("--")) {
					Option o = longToOption.get(s.substring(2));
					if(o == null)
						throw new InvalidOptionException("Invalid Option: " + s);
					
					switch(o.action) {
					case STORE_TRUE:
						retVal.add(o.value, true);
						break;
					case STORE_FALSE:
						retVal.add(o.value, false);
						break;
					case STORE_VALUE:
						if(arr.length <= i + 1 || arr[i+1].startsWith("-")) {
							//There is no value specified for this option
							// Either there are no more values or the next value
							// is another option
							if(o.def == null)
								throw new InvalidOptionException("Option " + s + " requires a value");
							
							retVal.add(o.value, o.def);
						} else {
							retVal.add(o.value, arr[++i]);
						}
						break;
					}
				} else if(s.startsWith("-")) {
					char[] chars = s.toCharArray();
					boolean optWithValue = false;
					for(int j = 1/* skip the - */; j < chars.length; j++) {
						Option o = shortToOption.get(chars[j]);
						if(o == null)
							throw new InvalidOptionException("Invalid Option: " + chars[j]);
						
						switch(o.action) {
						case STORE_TRUE:
							retVal.add(o.value, true);
							break;
						case STORE_FALSE:
							retVal.add(o.value, false);
							break;
						case STORE_VALUE:
							//Use optWithValue to allow short options that require values to no have to be
							// the last option specified in a list.  e.g. using the familiar tar command, you
							// could freely specify "tar -tfv file.tar".  Note that f takes the value file.tar.
							if(optWithValue) {
								if(o.value == null)
									throw new InvalidOptionException("Option " + chars[j] + " requires a value, but a previous option in this section also required a value");
								
								retVal.add(o.value, o.def);
							} else {
								if(arr.length <= i + 1 || arr[i+1].startsWith("-")) {
									//There is no value specified for this option
									// Either there are no more values or the next value
									// is another option
									if(o.def == null)
										throw new InvalidOptionException("Option " + chars[j] + " requires a value");
									
									retVal.add(o.value, o.def);
								} else {
									retVal.add(o.value, arr[++i]);
									optWithValue = true;
								}
							}
							break;
						}
					}
				} else {
					retVal.addPositional(++positional, s);
				}
			}
		}
		
		//now add any defaults that weren't specified
		for(Option o : options)
			if(o.def != null && retVal.get(o.value) == null)
				retVal.add(o.value, o.def);

		if(retVal.getBoolean("help")) {
			printUsage(System.out);
			System.exit(0);
		}
		return retVal;
	}
	
	public void printUsage(PrintStream out) {
		if(usage != null && usage.length() > 0) {
			out.println(usage);
			out.println();
		}
		
		int longestOption = 0;
		for(Option o : options) {
			if(o.longName != null) {
				int len = o.longName.length();
				if(len > longestOption)
					longestOption = len;
			}
		}
		
		if(OPTION_HEADER.length() > longestOption) {
			longestOption = OPTION_HEADER.length();
		}
		
		//Add some space
		longestOption += 2;
		
		final String pad1 = "     ";
		final String pad2 = "   ";
		int descLength = lineLength - longestOption - pad1.length() - pad2.length();
		
		String header = String.format(pad1 + "%1$-" + longestOption + "s" + pad2 + "%2$-" + descLength + "s", 
				OPTION_HEADER, DESCRIPTION_HEADER);
		
		out.println(header);
		char[] dash = new char[header.length()];
		Arrays.fill(dash, '-');
		out.println(dash);
		for(Option o : options) {
			
			String[] desc = breakupString(o.desc, descLength);
			String lineFormat = "%1$-2s   %2$-" + longestOption + "s   %3$-" + descLength + "s";
			out.println(String.format(lineFormat, 
					o.shortName == NO_SHORTNAME ? "" : "-" + o.shortName, 
					o.longName == NO_LONGNAME ? "" : "--" + o.longName,
					desc[0]));
			
			//Print the rest of the description
			for(int i = 1; i < desc.length; i++) {
				out.println(String.format(lineFormat, "", "", desc[i].trim()));
			}
			
		}
	}

	/**
	 * The the Usage message for these options
	 * @param usage The message to set
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	/**
	 * The internal representation of an option.
	 */
	private class Option {
		public String desc;
		char shortName;
		String longName;
		String value;
		Action action;
		public Object def;
	}

	/**
	 * Load options from a properties file.
	 * 
	 * @param filename The name of the file to load
	 * @return An <code>Options</code> object representing the given file contents
	 * @throws InvalidOptionException If an option in the given file is not valid for this
	 * <code>OptionParser</code>
	 */
	public Options load(String filename) throws InvalidOptionException {
		Properties p = new Properties();
		
		try {
			p.load(new FileReader(filename));
		} catch(IOException ioe) {
			
		}
		
		Options retVal = new Options();
		Set<Map.Entry<Object, Object>> entries = p.entrySet();
		for(Map.Entry<Object, Object> e : entries) {
			String key = e.getKey().toString();
			Option o = longToOption.get(key);
			if(o == null)
				throw new InvalidOptionException("Invalid Option: " + key);
			
			switch(o.action) {
			case STORE_TRUE:
				retVal.add(o.value, true);
				break;
			case STORE_FALSE:
				retVal.add(o.value, false);
				break;
			case STORE_VALUE:
				String val = e.getValue().toString();
				if(val.isEmpty()) {
					//There is no value specified for this option
					if(o.def == null)
						throw new InvalidOptionException("Option " + key + " requires a value");
					
					retVal.add(o.value, o.def);
				} else {
					retVal.add(o.value, val);
				}
				break;
			}
		}
		
		return retVal;
	}
}