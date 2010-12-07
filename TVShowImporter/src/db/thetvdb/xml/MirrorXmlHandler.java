package db.thetvdb.xml;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import db.thetvdb.Mirror;

public class MirrorXmlHandler extends DefaultHandler {
	private List<Mirror> mirrors;
	
	private int id;
	private int typemask;
	private String path;
	
	private static enum TagType { ID, TYPE_MASK, PATH };
	private TagType curTag;
	
	public MirrorXmlHandler(List<Mirror> list) {
		this.mirrors = list;
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		
		if(name.equalsIgnoreCase("mirror")) {
			mirrors.add(new Mirror(id, typemask, path));
		} else if(name.equalsIgnoreCase("id")) {
			curTag = null;
		} else if(name.equalsIgnoreCase("typemask")) {
			curTag = null;
		} else if(name.equalsIgnoreCase("mirrorpath")) {
			curTag = null;
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		
		if(name.equalsIgnoreCase("mirror")) {
			id = 0;
			typemask = 0;
			path = null;
		} else if(name.equalsIgnoreCase("id")) {
			curTag = TagType.ID;
		} else if(name.equalsIgnoreCase("typemask")) {
			curTag = TagType.TYPE_MASK;
		} else if(name.equalsIgnoreCase("mirrorpath")) {
			curTag = TagType.PATH;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		
		if(curTag == TagType.ID) {
			id = Integer.parseInt(new String(ch, start, length));
		} else if(curTag == TagType.TYPE_MASK) {
			typemask = Integer.parseInt(new String(ch, start, length));
		} else if(curTag == TagType.PATH) {
			path = new String(ch, start, length);
		}
	}
}
