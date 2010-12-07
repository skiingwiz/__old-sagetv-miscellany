package db.thetvdb;


/**
 * Represents a single mirror for use with TheTvDb.com
 *
 */
public class Mirror {
	public static final int XML_MIRROR_MASK = 1;
	public static final int BANNER_MIRROR_MASK = 2;
	public static final int ZIP_MIRROR_MASK = 4;
	
	private int id;
	private int typeMask;
	private String path;
	
	public Mirror(int id, int typeMask, String path) {
		this.id = id;
		this.path = path;
		this.typeMask = typeMask;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isXmlMirror() {
		return (typeMask & XML_MIRROR_MASK) > 0;
	}
	
	public boolean isBannerMirror() {
		return (typeMask & BANNER_MIRROR_MASK) > 0;
	}
	
	public boolean isZipMirror() {
		return (typeMask & ZIP_MIRROR_MASK) > 0;
	}
	
	@Override
	public String toString() {
		return path;
	}
}
