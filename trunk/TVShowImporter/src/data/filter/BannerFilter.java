package data.filter;

import data.Banner;

public class BannerFilter implements Filter<Banner> {
	private String lang;
	
	public BannerFilter(String lang) {
		this.lang = lang;
	}
	@Override
	public boolean test(Banner item) {
		return item.getLanguage().equalsIgnoreCase(lang);
	}

}
