package db.thetvdb.xml;

import java.util.List;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import data.Banner;
import data.filter.Filter;


public class BannerXmlHandler extends DefaultHandler {
	private String curTag;
	private Banner curBanner;
	List<Banner> banners;
	Filter<Banner> filter;
	
	public BannerXmlHandler(List<Banner> banners, Filter<Banner> filter) {
		this.banners = banners;
		this.filter = filter;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		if(curTag == null) {
			//do nothing
		} else if(curTag.equalsIgnoreCase("id")) {
			curBanner.setId(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("bannerpath")) {
			curBanner.setBannerPath(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("vignettepath")) {
			curBanner.setVignettePath(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("thumbnailpath")) {
			curBanner.setThumbnailPath(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("bannertype")) {
			curBanner.setBannerType(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("bannertype2")) {
			curBanner.setBannerType2(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("colors")) {
			curBanner.setColors(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("language")) {
			curBanner.setLanguage(new String(ch, start, length));
		} else if(curTag.equalsIgnoreCase("season")) {
			curBanner.setSeason(new String(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, name, name);
		
		if(name.equalsIgnoreCase("banner"))
			banners.add(curBanner);
			
		curTag = null;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes arg3) throws SAXException {
		super.startElement(uri, localName, name, arg3);
		curTag = name;
		
		if(curTag.equalsIgnoreCase("banner"))
			curBanner = new Banner();
	}

	
}
