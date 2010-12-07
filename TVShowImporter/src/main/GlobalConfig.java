package main;
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
	
	
	private static Options options;
	
	public static void setOptions(Options options) {
		GlobalConfig.options = options;
	}
	
	public static Options getOptions() {
		return options;
	}
}
