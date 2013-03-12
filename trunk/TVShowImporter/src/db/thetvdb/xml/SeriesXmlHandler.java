package db.thetvdb.xml;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import data.Series;


public class SeriesXmlHandler extends DefaultHandler {
	private List<Series> seriesList;
	private Series curSeries;
	private String curTag;

	public SeriesXmlHandler(List<Series> list) {
		this.seriesList = list;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);

		if(curTag == null) {
			//do nothing
		} else if(curTag.equalsIgnoreCase("seriesid")) {
			curSeries.setId(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("language")) {
			curSeries.setLanguage(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("seriesname")) {
		    String name = new String(ch, start, length);
			curSeries.setName(name);
			curSeries.setOriginalName(name);
		} else if(curTag.equalsIgnoreCase("banner")) {
			curSeries.setBannerLocation(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("overview")) {
			curSeries.setDescription(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("firstaired")) {
			curSeries.setFirstAirDate(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("imdb_id")) {
			curSeries.setImdbId(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("zap2it_id")) {
			curSeries.setZap2itId(new String(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		curTag = null;

		if(name.equalsIgnoreCase("series")) {
			seriesList.add(curSeries);
	    }
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		curTag = name;

		if(name.equalsIgnoreCase("series")) {
			curSeries = new Series();
	    }
	}
}
