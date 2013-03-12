package main;
import util.opt.InvalidOptionException;
import util.opt.OptionParser;
import util.opt.Options;


public class GlobalConfig {
	public static final String VERBOSE = "verbose";
	public static final String LANGUAGE = "language";
	public static final String RENAME = "rename";
	public static final String RENAME_PATTERN = "rename-pattern";
	public static final String CLEAR_CACHE = "clearcache";
	public static final String RECURSE = "recurse";
	public static final String SERIES_BANNERS = "series-banners";
	public static final String SEASON_BANNERS = "season-banners";
	public static final String EXCLUDED_ENDINGS = "excluded-endings";
	public static final String MIRROR_FILE_LIFE = "mirror-file-life";
	public static final String IMAGE_FORMAT = "image-format";
	public static final String FANART_DIR = "fanart-dir";
	public static final String PROPERTIES_FILE = "properties-file";
	public static final String FANART = "fanart";
	public static final String VERSION = "print-version";
	
	private static OptionParser parser;
	private static Options options;
	
	public static void setOptions(Options options) {
		GlobalConfig.options = options;
	}
	
	public static Options getOptions() {
		if(options == null) {
			try {
				parse(null);
			} catch (InvalidOptionException e) {
				//TODO log this
			}
		}
		return options;
	}
	
	public static void parse(String[] args) throws InvalidOptionException {
		if(parser == null) {
			setupOptions();
		}
		
		options = parser.parse(args);
	}
	
	private static void setupOptions() {
		//TODO add option to touch file
		
		parser = new OptionParser();
		parser.setUsage("Usage: Main [options] file [file ...]");
		
		parser.addOption('v', "verbose", GlobalConfig.VERBOSE, "Log (more) details of processing", OptionParser.Action.STORE_TRUE);
		parser.addOption('l', "language", GlobalConfig.LANGUAGE, "Set the language for episode information.  Default is English", OptionParser.Action.STORE_VALUE, "en");
		parser.addOption('n', "no-rename", GlobalConfig.RENAME, "Do not rename files as a part of processing", OptionParser.Action.STORE_FALSE, "true");

		parser.addOption('c', "clear-cache", GlobalConfig.CLEAR_CACHE, "Clear TheTvDb.com cache before processing", OptionParser.Action.STORE_TRUE);
		parser.addOption('x', "no-recurse", GlobalConfig.RECURSE, "Do not recurse through subdirectories when a directory is given.", OptionParser.Action.STORE_FALSE, "true");
		parser.addOption('s', "series-banners", GlobalConfig.SERIES_BANNERS, "Download series banners.", OptionParser.Action.STORE_TRUE);
		parser.addOption('b', "season-banners", GlobalConfig.SEASON_BANNERS, "Download season banners.", OptionParser.Action.STORE_TRUE);

		parser.addOption('i', "image-format", GlobalConfig.IMAGE_FORMAT, "The format for downloaded images.  Default is jpeg.", OptionParser.Action.STORE_VALUE, "jpeg");
		
		parser.addOption(OptionParser.NO_SHORTNAME, "no-properties", GlobalConfig.PROPERTIES_FILE, "Don't write .properties files", OptionParser.Action.STORE_FALSE, "true");
		parser.addOption(OptionParser.NO_SHORTNAME, "no-fanart", GlobalConfig.FANART, "Don't download fanart", OptionParser.Action.STORE_FALSE, "true");
		
		parser.addOption(OptionParser.NO_SHORTNAME, "fanart-dir", GlobalConfig.FANART_DIR, "The base directory for fanart.", OptionParser.Action.STORE_VALUE, "./");
		
		parser.addOption(OptionParser.NO_SHORTNAME, "rename-pattern", GlobalConfig.RENAME_PATTERN, "The pattern to use when renaming the media file.", OptionParser.Action.STORE_VALUE, "%season-num%x%episode-num% - %episode-title%");
		parser.addOption(OptionParser.NO_SHORTNAME, "excluded-endings", GlobalConfig.EXCLUDED_ENDINGS, "The endings of files to be excluded. Separate multiple endings using |", OptionParser.Action.STORE_VALUE, ".properties|.!ut");
		
		parser.addOption(OptionParser.NO_SHORTNAME, "version", GlobalConfig.VERSION, "Print the version and exit", OptionParser.Action.STORE_TRUE);

		
		parser.addOption(OptionParser.NO_SHORTNAME, "thetvdb-mirror-file-life", GlobalConfig.MIRROR_FILE_LIFE, "The amount of time to cache theTvDb.com's mirror file before re-requesting it (seconds). Default is 30 days.", OptionParser.Action.STORE_VALUE, 3600L * 24L * 30L);
	}
}
