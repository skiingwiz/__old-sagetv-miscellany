package main;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.TextFileWriter;
import data.Episode;
import data.Series;


public class PropertiesFileWriter {
	/** The separator used between items for properties that support 
	 * multiple entries 
	 */
	public static final String ITEM_SERPARATOR = ";";
	public static final String MAP_SEPARATOR = ":";
	public static final String FILE_ENDING = ".properties";
	
	public static final String TITLE = "Title";
	public static final String ALBUM = "Album";
	public static final String ARTIST = "Artist";
	public static final String ALBUM_ARTIST = "AlbumArtist";
	public static final String COMPOSER = "Composer";
	public static final String TRACK = "Track";
	public static final String TOTAL_TRACKS = "TotalTracks";
	public static final String YEAR = "Year";
	public static final String COMMENT = "Comment";
	public static final String GENRE = "Genre";
	public static final String GENRE_ID = "GenreID";
	public static final String LANGUAGE = "Language";
	public static final String RATED = "Rated";
	public static final String RUNNING_TIME = "RunningTime";
	public static final String DURATION = "Duration";
	public static final String DESCRIPTION = "Description";
	public static final String ACTOR = "Actor";
	public static final String LEAD_ACTOR = "Lead\\ Actor";
	public static final String SUPPORTING_ACTOR = "Supporting\\ Actor";
	public static final String ACTRESS = "Actress";
	public static final String LEAD_ACTRESS = "Lead\\ Actress";
	public static final String SUPPORTING_ACTRESS = "Supporting\\ Actress";
	public static final String GUEST = "Guest";
	public static final String GUEST_STAR = "Guest\\ Star";
	public static final String DIRECTOR = "Director";
	public static final String PRODUCER = "Producer";
	public static final String WRITER = "Writer";
	public static final String CHOREOGRAPHER = "Choreographer";
	public static final String SPORTS_FIGURE = "Sports\\ Figure";
	public static final String COACH = "Coach";
	public static final String HOST = "Host";
	public static final String EXECUTIVE_PRODUCER = "Executive\\ Producer";
	private static final String ORIGINAL_AIR_DATE = "OriginalAirDate";
	private static final String EPISODE_TITLE = "EpisodeTitle";
	private static final String SEASON_NUMBER = "SeasonNumber";
	private static final String EPISODE_NUMBER = "EpisodeNumber";
	private static final String MEDIA_TYPE = "MediaType";
	private static final String MEDIA_TITLE= "MediaTitle";
	
	private static final String X_ORIGINAL_FILE = "x-OriginalFileName";
	private static final String X_GENERATOR = "x-Generator";
	
	private static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	 
	
	public void writeFile(File videoFile, Episode e) throws IOException {
		//Example of correct properties file naming:
		// Video file: Movie.avi
		// Properties file: Movie.avi.properties
		//This is different than other conventions in Sage, so be careful
		String filename = videoFile.getPath() + FILE_ENDING;
		TextFileWriter out = new TextFileWriter(filename);

		String title = formatTitle(e);
		out.print(TITLE).print(MAP_SEPARATOR).println(title);
		String desc = e.getDescription();
		if(desc != null) {
			desc = desc.replaceAll("\n", "");

			out.print(DESCRIPTION).print(MAP_SEPARATOR).println(desc);
		}

		out.print(MEDIA_TITLE).print(MAP_SEPARATOR).println(e.getSeries().getName());
		out.print(EPISODE_TITLE).print(MAP_SEPARATOR).println(e.getEpisodeTitle());
		out.print(SEASON_NUMBER).print(MAP_SEPARATOR).println(e.getSeasonNum());
		out.print(EPISODE_NUMBER).print(MAP_SEPARATOR).println(e.getEpisodeNum());
		
		Date firstAir = e.getFirstAirDate();
		if(firstAir != null) {
			out.print(YEAR).print(MAP_SEPARATOR).println(YEAR_FORMAT.format(firstAir));
			out.print(ORIGINAL_AIR_DATE).print(MAP_SEPARATOR).println(DATE_FORMAT.format(firstAir));
		}
		
		writeArray(out, WRITER, e.getWriters());
		writeArray(out, GUEST_STAR, e.getGuestStars());
		
		Series s = e.getSeries();
		if(s != null) {
			writeArray(out, GENRE, s.getGenre());
			writeArray(out, ACTOR, s.getActors());
			if(s.getContentRating() != null)
				out.print(RATED).print(MAP_SEPARATOR).println(s.getContentRating());
			
			//Don't output running time.  Sage takes that as the time of the video,
			//which it isn't (since the video has commercials removed).  This causes
			//Sage to be inaccurate in it's determination of when to mark things
			//as watched automatically
			//out.print(RUNNING_TIME).print(MAP_SEPARATOR).println(s.getRuntime());
		}
		
		out.print(MEDIA_TYPE).print(MAP_SEPARATOR).println("TV");
		out.print(X_ORIGINAL_FILE).print(MAP_SEPARATOR).println(e.getOriginalFile());
		out.print(X_GENERATOR).print(MAP_SEPARATOR).println(Version.getFullNameVersion());
		out.close();
	}
	
	private void writeArray(TextFileWriter out, String property, String[] arr) {
		if(arr != null && arr.length > 0){
			StringBuffer sb = new StringBuffer();
			for(String s : arr) {
				sb.append(s).append(ITEM_SERPARATOR);
			}
			
			out.print(property).print(MAP_SEPARATOR).println(sb.toString());
		}
	}

	//TODO option for specifying title format
	private String formatTitle(Episode e) {
		/*int num = (e.getSeasonNum() * 100) + e.getEpisodeNum();
		DecimalFormat df = new DecimalFormat("#000");
		return df.format(num) + " " + e.getEpisodeTitle();
		*/
		return e.getEpisodeTitle();
	}
}
