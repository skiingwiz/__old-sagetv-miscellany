package data;

import java.io.File;
import java.util.Date;


public class Episode {
//TODO what about multipart episodes?
	
	protected Series series;
	protected int seasonNum;
	protected int episodeNum;
	protected String episodeTitle;
	private String rating;
	private String description;
	private String[] writer;
	private String[] guestStars;
	private Date firstAirDate;
	private String originalFile;
	
	public String[] getWriter() {
		return writer;
	}
	public void setWriter(String[] writer) {
		this.writer = writer;
	}
	public Series getSeries() {
		return series;
	}
	public int getSeasonNum() {
		return seasonNum;
	}
	public void setSeasonNum(int seasonNum) {
		this.seasonNum = seasonNum;
	}
	public int getEpisodeNum() {
		return episodeNum;
	}
	public void setEpisodeNum(int episodeNum) {
		this.episodeNum = episodeNum;
	}
	public String getEpisodeTitle() {
		return episodeTitle;
	}
	public void setEpisodeTitle(String episodeTitle) {
		this.episodeTitle = episodeTitle;
	}

	public String[] getWriters() {
		return writer;
	}
	public void setWriters(String[] writer) {
		this.writer = writer;
	}
	
	public String[] getGuestStars() {
		return guestStars;
	}
	
	public void setGuestStars(String[] guestStars) {
		this.guestStars = guestStars;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Show=").append(series == null ? "No Series" : series.getName()).append(",");
		sb.append("Season=").append(seasonNum).append(",");
		sb.append("Episode=").append(episodeNum);
		
		return sb.toString();
	}
	
	public String getRating() {
		return rating;
	}
	
	public void setRating(String rating) {
		this.rating = rating;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getFirstAirDate() {
		return firstAirDate;
	}
	
	public void setFirstAirDate(Date date) {
		firstAirDate = date;
	}
	public void setSeries(Series s) {
		this.series = s;
	}
	public void setOriginalFile(File file) {
		originalFile = file.getAbsolutePath();
	}
	public String getOriginalFile() {
		return originalFile;
	}
}