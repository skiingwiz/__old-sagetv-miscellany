package main;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Episode;
import data.Series;




public class FileParser {
	private static final Pattern[] FILE_NAME_PATTERNS = new Pattern[] {
		Pattern.compile(".*[Ss]([0-9]?[0-9])[Xx _]?[Ee]([0-9]?[0-9]).*"),
		Pattern.compile(".*?([0-9]?[0-9])[Xx]([0-9]?[0-9]).*?"),
		Pattern.compile(".*?([0-9]?[0-9])([0-9][0-9]).*?")
		};
	
	/**
	 * 
	 * NB: The file passed in does <b>NOT</b> have to exist.
	 * 
	 * @param file
	 * @return
	 */
	public static Episode parse(File file) {
		Episode retVal = new Episode();
		File parent = file.getParentFile();
		
		if( parent.getName().toLowerCase().startsWith("season") ) {
			parent = parent.getParentFile();
			//TODO get the season from here, and compare it to the season found below.
		}
		
		Series s = new Series();
		s.setName(parent.getName());
		retVal.setSeries(s);
		
		for(Pattern p : FILE_NAME_PATTERNS) {
			Matcher m = p.matcher(file.getName());
			if(m.matches()) {
				String season = m.group(1);
				String episode = m.group(2);
				
				retVal.setSeasonNum(Integer.parseInt(season));
				retVal.setEpisodeNum(Integer.parseInt(episode));
				break;
			}
		}
		
		
		return retVal;
	}
}
