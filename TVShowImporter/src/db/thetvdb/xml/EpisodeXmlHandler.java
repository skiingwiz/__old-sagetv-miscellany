package db.thetvdb.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import data.Episode;
import db.thetvdb.Utilities;

public class EpisodeXmlHandler extends DefaultHandler {
	private static final DateFormat FIRST_AIRED_DF = new SimpleDateFormat("yyyy-MM-dd");
	
	private String curTag;
	private Episode episode;
	
	public EpisodeXmlHandler(Episode episode) {
		this.episode = episode;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		//’'
		if(curTag == null) {
			//do nothing
		//} else if(curTag.equalsIgnoreCase("id")) {
		} else if(curTag.equalsIgnoreCase("episodename")) {
			String s = Utilities.normalizeString(new String(ch, start, length));
			String title = episode.getEpisodeTitle();
			
			if(title == null)
				title = s;
			else 
				title += s;
			
			episode.setEpisodeTitle(title);
		} else if(curTag.equalsIgnoreCase("firstaired")) {
			String date = new String(ch, start, length);
			try {
				episode.setFirstAirDate(FIRST_AIRED_DF.parse(date));
			} catch (ParseException e) {
				System.err.println("Failed to parse First Aired Date.  String: " + date);
			}
		} else if(curTag.equalsIgnoreCase("gueststars")) {
			episode.setGuestStars(Utilities.makeArray(new String(ch, start, length)));
		} else if(curTag.equalsIgnoreCase("overview")) {
			//This can be called multiple times since it is long
			String str = Utilities.normalizeString(new String(ch, start, length));
			String desc = episode.getDescription();
			
			if(desc == null) 
				desc = str;
			else
				desc += str;
				
			episode.setDescription(desc);
		} else if(curTag.equalsIgnoreCase("rating")) {
			episode.setRating(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("writer")) {
			episode.setWriters(Utilities.makeArray(new String(ch, start, length)));
		//} else if(curTag.equalsIgnoreCase("filename")) {
		} else if(curTag.equalsIgnoreCase("episodenumber")) {
			String s = new String(ch, start, length);
			try {
				episode.setEpisodeNum(Integer.parseInt(s));
			} catch(NumberFormatException nfe) {
				System.err.print("Could not parse episode number. String: " + s);
			}
		} else if(curTag.equalsIgnoreCase("seasonnumber")) {
			String s = new String(ch, start, length);
			try {
				episode.setSeasonNum(Integer.parseInt(s));
			} catch(NumberFormatException nfe) {
				System.err.print("Could not parse season number. String: " + s);
			}
		}
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		super.endElement(arg0, arg1, arg2);
		
		curTag = null;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		
		curTag = name;
	}
}
