package db.thetvdb.xml;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import data.Series;
import db.thetvdb.Utilities;

public class BaseSeriesXmlHandler extends DefaultHandler {
	private String curTag;
	private Series series;
	
	public BaseSeriesXmlHandler(Series series) {
		this.series = series;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		
		if(curTag == null) {
			//do nothing
		} else if(curTag.equalsIgnoreCase("id")) {
			//series.setId(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("actors")) {
			series.setActors(Utilities.makeArray(new String(ch, start, length)));
		} else if(curTag.equalsIgnoreCase("airs_dayofweek")) {
			series.setAirDay(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("airs_time")) {
			series.setAirTime(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("contentrating")) {
			series.setContentRating(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("firstaired")) {
			series.setFirstAirDate(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("genre")) {
			series.setGenre(Utilities.makeArray(new String(ch, start, length)));
		} else if(curTag.equalsIgnoreCase("imdb_id")) {
			series.setImdbId(new String(ch, start, length));
		//} else if(curTag.equalsIgnoreCase("language")) {	
		} else if(curTag.equalsIgnoreCase("network")) {
			series.setNetwork(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("overview")) {
			series.setDescription(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("rating")) {
			series.setStarRating(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("runtime")) {
			long runtimeMin = 0L;
			String str = new String(ch, start, length);
			try {
				runtimeMin = Long.parseLong(str);
			} catch(NumberFormatException nfe) {
				System.err.println("Failed to convert runtime to a long.  String: " + str);
			}
			//TheTvDb runtime is minutes.  We need it in milliseconds
			series.setRuntime(runtimeMin * 60000);
		} else if(curTag.equalsIgnoreCase("seriesid")) {
		//} else if(curTag.equalsIgnoreCase("status")) {	
		} else if(curTag.equalsIgnoreCase("banner")) {
			series.setBannerLocation(new String(ch, start, length));
		//} else if(curTag.equalsIgnoreCase("fanart")) {	
		//} else if(curTag.equalsIgnoreCase("lastupdated")) {	
		} else if(curTag.equalsIgnoreCase("zap2it_id")) {
			series.setZap2itId(new String(ch, start, length));
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		
		curTag = name;
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
	
		curTag = null;
	}

}
