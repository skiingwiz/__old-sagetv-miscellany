package main;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;

import data.Banner;
import data.Episode;
import db.DatabaseProcessingException;
import db.thetvdb.TheTvDbDatabase;

//TODO handle replacing previous files when there is a PROPER file downloaded
public class Main {
	private static TheTvDbDatabase db;

	public static void main(String[] args) throws Exception {
		GlobalConfig.parse(args);

		if(GlobalConfig.getOptions().getBoolean(GlobalConfig.VERSION)) {
			System.out.println(Version.getFullNameVersion());
			System.exit(0);
		}

		if(GlobalConfig.getOptions().getBoolean(GlobalConfig.CLEAR_CACHE)) {
			TheTvDbDatabase.clearCaches();
		}

		String lang = GlobalConfig.getOptions().getString(GlobalConfig.LANGUAGE);
		db = new TheTvDbDatabase(lang);
		db.initialize();


		for(String s : GlobalConfig.getOptions().getPositionals()) {
			processFile(new File(s));

		}
	}

	private static void processFile(File f) {
		if(f.exists()) {
			if(f.isDirectory()) {
				if(GlobalConfig.getOptions().getBoolean(GlobalConfig.RECURSE)) {
					for(File sf : f.listFiles()) {
						processFile(sf);
					}
				} else {
					System.out.println("Given file is a directory and there is no recursion.  File: " + f.getName());
				}
			} else {
				boolean process = true;
				String[] exEndings = GlobalConfig.getOptions().getString(GlobalConfig.EXCLUDED_ENDINGS).split("\\|");
				for(String s : exEndings) {
					if(f.getName().endsWith(s)) {
						process = false;

						if(GlobalConfig.getOptions().getBoolean(GlobalConfig.VERBOSE)) {
							StringBuffer sb = new StringBuffer();
							sb.append("Skipping file because of ending.  File: ");
							sb.append(f.getName()).append(" Ending: ");
							sb.append(s);
							System.out.println(sb.toString());
						}

						break;
					}
				}

				if(process) {
					try {
						Episode e = FileParser.parse(f);
						e = db.lookup(e.getSeries().getName(), e.getSeasonNum(), e.getEpisodeNum());
						if(e != null) {
							e.setOriginalFile(f);
							if(GlobalConfig.getOptions().getBoolean(GlobalConfig.RENAME)) {
								String renamePattern = GlobalConfig.getOptions().getString(GlobalConfig.RENAME_PATTERN);
								f = renameFile(f, e, renamePattern);
							}

							if(GlobalConfig.getOptions().getBoolean(GlobalConfig.PROPERTIES_FILE)) {
								writeFile(f, e);
							}

							if(GlobalConfig.getOptions().getBoolean(GlobalConfig.FANART)) {
								storeFanart(e);
							}
						}
					} catch(DatabaseProcessingException dpe) {
						System.err.println("Skipping file due to error.  file: " + f.getPath() + " Error: " + dpe.getLocalizedMessage());
					}
				}
			}
		} else {
			System.err.println("File: " + f.getPath() + " does not exist.");
		}
	}

	private static void storeFanart(Episode e) throws DatabaseProcessingException {
		String fanartDir = GlobalConfig.getOptions().getString(GlobalConfig.FANART_DIR);
		if(fanartDir == null) {
			System.err.println("Fanart is enabled, but no directory is specified.  Fanart will NOT be downloaded");
			return;
		}

		List<Banner> banners = db.getBannerInfo(e.getSeries().getName());
		for(Banner b : banners) {
			StringBuffer dir = new StringBuffer(fanartDir);
			if(!fanartDir.endsWith(File.separator)) {
				dir.append(File.separator);
			}
			dir.append(e.getSeries().getName()).append(File.separator);

			if(b.getSeason() != null) {
				int season = 0;
				try {
					season = Integer.parseInt(b.getSeason());
				} catch(NumberFormatException nfe) {
					System.err.println("Problem determining season for banner.  Season reported as: " +
							b.getSeason());
					continue;
				}

				if(season == e.getSeasonNum()) {
					dir.append("Season ").append(e.getSeasonNum());
					dir.append(File.separator);
				} else {
					continue;
				}
			}

			if(b.getBannerType().equalsIgnoreCase("season") ||
			   b.getBannerType().equalsIgnoreCase("seasonwide") ||
			   b.getBannerType().equalsIgnoreCase("poster")) {
				dir.append("Posters").append(File.separator);
			} else if(b.getBannerType().equalsIgnoreCase("fanart")) {
				dir.append("Backgrounds").append(File.separator);
			} else if(b.getBannerType().equalsIgnoreCase("series") &&
					b.getBannerType2().equalsIgnoreCase("graphical")) {
				dir.append("Banners").append(File.separator);
			} else {
				System.out.print("Skipping banner because it is of undesired type. Banner: ");
				System.out.print(b.getBannerName());
				System.out.print(" Type: ");
				System.out.print(b.getBannerType());
				System.out.print(" Type2: ");
				System.out.println(b.getBannerType2());
				continue;
			}

			System.out.println("Downloading banner " + b.getBannerName());
			try {
				db.downloadBanner(b, dir.toString());
			} catch(DatabaseProcessingException dbe) {
				//TODO make a separate exception that inherits from DatabaseProcessingException
				System.err.println(dbe.getLocalizedMessage());
			}
		}
	}

	private static File renameFile(File f, Episode e, String pattern) {
		//TODO option to specify decimal format in variable e.g. %season-num{00}%
		DecimalFormat df = new DecimalFormat("00");

		String newName = new String(pattern);

		newName = newName.replaceAll("%season-num%", df.format(e.getSeasonNum()));
		newName = newName.replaceAll("%episode-num%", df.format(e.getEpisodeNum()));
		newName = newName.replaceAll("%episode-title%",
				Matcher.quoteReplacement(e.getEpisodeTitle()));
		//get rid of unusable characters
		newName = newName.replaceAll("[/\\\\:?!;*]", " ");

		File parentFile = f.getParentFile();
		if(parentFile != null) {
			//TODO generic option to set path for file moving. Allow variable substitution
			if(!parentFile.getName().toLowerCase().startsWith("season")) {
				//assume that if it starts with "season" then it is a season folder
				String parent = parentFile.getPath();
				if(!parent.endsWith(File.separator)) {
					parent = parent + File.separator;
				}

				parentFile = new File(parent + "Season " + e.getSeasonNum());
				if(!parentFile.exists() && !parentFile.mkdir()) {
					throw new RuntimeException("Failed to make directory: " + parentFile.getAbsolutePath());
				}
			}

			String parent = parentFile.getPath();
			if(!parent.endsWith(File.separator)) {
				parent = parent + File.separator;
			}

			newName = parent + newName;
		}

		String oldName = f.getName();
		//preserve the original file extension
		newName += oldName.substring(oldName.lastIndexOf("."));


		System.out.println("Renaming " + f.getPath() + " to " + newName);
		File newFile = new File(newName);
		if(f.renameTo(newFile)) {
			System.out.println("File renaming successful.");
		} else {
			System.err.println("Failed to rename " + f.getName() + " to " + newName);
			newFile = f;
		}

		//update the last mod time to be sure sage re-import this
		newFile.setLastModified(System.currentTimeMillis());
		return newFile;
	}

	private static void writeFile(File f, Episode e) {
		try {
			new PropertiesFileWriter().writeFile(f, e);
		} catch (IOException ioe) {
			System.err.println("Could not write properties file.  Error: " + ioe.getMessage());
		}
	}
}
