package db.thetvdb;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import data.Banner;
import data.Episode;
import data.Series;
import data.filter.BannerFilter;
import db.DatabaseInitializationException;
import db.DatabaseProcessingException;
import db.thetvdb.xml.BannerXmlHandler;
import db.thetvdb.xml.BaseSeriesXmlHandler;
import db.thetvdb.xml.EpisodeXmlHandler;
import db.thetvdb.xml.MirrorXmlHandler;
import db.thetvdb.xml.SeriesXmlHandler;
import main.GlobalConfig;
import util.io.IterableBufferedReader;


public class TheTvDbDatabase {
    private static final String BASE_URL = "http://thetvdb.com";
	private static final String CACHE_DIR = "cache/";
	private static final String API_KEY = "3D94331EFB6E696C";
	private static final String MIRROR_FILE_URL = "http://www.thetvdb.com/api/" + API_KEY + "/mirrors.xml";
	private static final String LOCAL_MIRROR_FILE = "mirrors.xml";
	private static final String SERIES_ID_FILE = "series_ids.dat";

	private List<Mirror> mirrors;
	private Properties seriesIds;
	private String lang;

	public TheTvDbDatabase(String lang) {
		this.lang = lang;
	}

	public static void clearCaches() {
		File cacheDir = new File(CACHE_DIR);
		if(cacheDir.exists()) {
			for(File f : cacheDir.listFiles()) {
				//This fails if a directory is not empty, but there should be no directories in here
				f.delete();
			}
		}
	}

	public void initialize() throws DatabaseInitializationException {
		File cacheDir = new File(CACHE_DIR);
		if(!cacheDir.exists()) {
			if(!cacheDir.mkdirs()) {
				throw new DatabaseInitializationException("Faile to create cache directory: " + CACHE_DIR);
			}
		}

		File mirrorList = new File(CACHE_DIR + LOCAL_MIRROR_FILE);

		if(!mirrorList.exists() ||
	       (System.currentTimeMillis() - mirrorList.lastModified() >
	       GlobalConfig.getOptions().getLong(GlobalConfig.MIRROR_FILE_LIFE))) {
			try {
				getMirrorList();
			} catch (DatabaseInitializationException die) {
				if(mirrorList.exists()) {
					System.err.println("Could not update mirror list, proceeding with old list.");
				} else {
					throw die;
				}
			}
		}

		try {
			parseMirrors();
		} catch (Exception e) {
			throw new DatabaseInitializationException("Failed to parse mirrors file.", e);
		}
	}

	public List<Banner> getBannerInfo(String series) throws DatabaseProcessingException {
		String seriesId = getSeriesId(series);

		StringBuffer urlPath = new StringBuffer();
		urlPath.append(BASE_URL).append("/api/");
		urlPath.append(API_KEY).append("/series/").append(seriesId).append("/banners.xml");

		URL bannerUrl;
		try {
			bannerUrl = new URL(urlPath.toString());
		} catch (MalformedURLException e) {
			throw new DatabaseProcessingException("Could not retrieve series information.  Bad Url.  URL:" + urlPath.toString(), e);
		}

		List<Banner> banners = new ArrayList<Banner>();
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(
					bannerUrl.openStream(), new BannerXmlHandler(banners, new BannerFilter(lang)));
		} catch (Exception e) {
			throw new DatabaseProcessingException("Could not retrieve episode information.  Error:" + e.getMessage(), e);
		}

		return banners;
	}

	public void downloadBanner(Banner banner, String dir) throws DatabaseProcessingException {
		StringBuffer urlPath = new StringBuffer();
		urlPath.append(BASE_URL).append("/banners/");
		urlPath.append(banner.getBannerPath());

		URL bannerUrl;
		try {
			bannerUrl = new URL(urlPath.toString());
		} catch (MalformedURLException e) {
			throw new DatabaseProcessingException("Could not retrieve series information.  Bad Url.  URL:" + urlPath.toString(), e);
		}

		File dirF = new File(dir);
		if(!dirF.exists() && !dirF.mkdirs()) {
			System.err.println("Unable to make directory for storing fanart.  Directory: " + dir);
		}

		StringBuffer filename = new StringBuffer(dir);
		if(!dir.endsWith(File.separator)) {
			filename.append(File.separator);
		}

		filename.append(banner.getBannerName());
		File fileD = new File(filename.toString());
		if(fileD.exists()) {
			if(GlobalConfig.getOptions().getBoolean(GlobalConfig.VERBOSE)) {
				System.out.println("Skipping fanart file because it already exists.  File: " + fileD.getPath());
			}
		} else {
			String imageFormat = GlobalConfig.getOptions().getString(GlobalConfig.IMAGE_FORMAT);

			try {
				BufferedImage im = ImageIO.read(bannerUrl);
				ImageIO.write(im, imageFormat, new File(filename.toString()));
			} catch (IOException ioe) {
				throw new DatabaseProcessingException("Failed to download banner.  Error: " + ioe.getMessage(), ioe);
			}
		}
	}

	public Episode lookup(String series, int season, int episode) throws DatabaseProcessingException {
		String seriesId = getSeriesId(series);
		Episode e = null;
		if(seriesId == null) {
			System.err.println("Lookup failed because series id couldn't be found.  Series Name: " + series);
		} else {
			Series s = lookup(seriesId);
			s.setName(series);
			e = getEpisodeInfo(seriesId, season, episode);
			e.setSeries(s);
		}

		return e;
	}

	public Series lookup(String seriesId) throws DatabaseProcessingException {
		File seriesIdFile = new File(CACHE_DIR + seriesId + ".xml");

		if(!seriesIdFile.exists()) {
			StringBuffer urlPath = new StringBuffer();
			urlPath.append(BASE_URL).append("/api/");
			urlPath.append(API_KEY).append("/series/").append(seriesId).append("/");
			urlPath.append(lang).append(".xml");

			URL seriesUrl;
			try {
				seriesUrl = new URL(urlPath.toString());
			} catch (MalformedURLException e) {
				throw new DatabaseProcessingException("Could not retrieve series information.  Bad Url.  URL:" + urlPath.toString(), e);
			}

			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(seriesIdFile)));
				IterableBufferedReader in = new IterableBufferedReader(new InputStreamReader(seriesUrl.openStream()));
				for(String s : in)
					out.println(s);

				in.close();
				out.close();
			} catch (IOException ioe) {
				throw new RuntimeException("Failed to store series information.  Error: " + ioe.getMessage(), ioe);
			}
		}

		Series series = new Series();
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(seriesIdFile, new BaseSeriesXmlHandler(series));
		} catch (Exception e) {
			throw new DatabaseProcessingException("Could not retrieve series information.  Error:" + e.getMessage(), e);
		}

		return series;
	}

	private Episode getEpisodeInfo(String seriesId, int seasonNum, int episodeNum) throws DatabaseProcessingException {
		StringBuffer urlPath = new StringBuffer();
		urlPath.append(BASE_URL).append("/api/");
		urlPath.append(API_KEY).append("/series/").append(seriesId);
		urlPath.append("/default/").append(seasonNum).append("/").append(episodeNum);
		urlPath.append("/").append(lang).append(".xml");

		System.out.println(urlPath);
		URL episodeUrl;
		try {
			episodeUrl = new URL(urlPath.toString());
		} catch (MalformedURLException e) {
			throw new DatabaseProcessingException("Could not retrieve episode information.  Bad Url.  URL:" + urlPath.toString(), e);
		}

		Episode episode = new Episode();
        SAXParser parser;
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new DatabaseProcessingException("Failed to create SAX Parser", e);
        } catch (SAXException e) {
            throw new DatabaseProcessingException("Failed to create SAX Parser", e);
        }

        try {
            parser.parse(episodeUrl.openStream(), new EpisodeXmlHandler(episode));
        } catch (Exception e) {
            System.out.println("Couldn't parse XML as text, trying GZIP");
            //create a new Episode, just in case it was messed up above
            episode = new Episode();
            try {
                parser.parse(new GZIPInputStream(episodeUrl.openStream()), new EpisodeXmlHandler(episode));
            } catch (SAXException e1) {
                throw new DatabaseProcessingException("Could not retrieve episode information.  Error:" + e.getMessage(), e);
            } catch (IOException e1) {
                throw new DatabaseProcessingException("Could not retrieve episode information.  Error:" + e.getMessage(), e);
            }
        }

		return episode;
	}

	private String getSeriesId(String seriesName) {
		boolean writeUpdates = true;
		File seriesIdFile = new File(CACHE_DIR + SERIES_ID_FILE);

		if(seriesIds == null) {
			seriesIds = new Properties();
			if(seriesIdFile.exists()) {
				if(GlobalConfig.getOptions().getBoolean(GlobalConfig.VERBOSE)){
					System.out.println("Loading Series ID cache from " + seriesIdFile.getAbsolutePath());
				}

				try {
					seriesIds.load(new BufferedReader(new FileReader(seriesIdFile)));
				} catch (IOException e) {
					writeUpdates = false;
					System.err.println("Could not load Series ID file.  Updates will not be written to disk. File: " + seriesIdFile.getAbsolutePath());
				}
			} else {
				System.out.println("Series ID cache file not found at: " + seriesIdFile.getAbsolutePath());
			}
		}

		String id = seriesIds.getProperty(seriesName);
		if(id == null) {
			id = lookupSeriesId(seriesName);
			if(id != null) {
				seriesIds.put(seriesName, id);

				if(writeUpdates) {
					try {
						seriesIds.store(new BufferedWriter(new FileWriter(seriesIdFile)), "");
					} catch (IOException e) {
						System.out.println("Could not save series id file.  Updates will not be written to disk. File: " + seriesIdFile.getAbsolutePath());
					}
				}
			}
		}

		return id;
	}

	private String lookupSeriesId(String seriesName) {
		URL seriesUrl;
		String seriesNameEncode;
		try {
			seriesNameEncode = URLEncoder.encode(seriesName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			throw new RuntimeException("Could not URL encode string: " + seriesName);
		}
		String urlPath = BASE_URL + "/api/GetSeries.php?seriesname=" + seriesNameEncode + "&language=" + lang;
		try {
			seriesUrl = new URL(urlPath);
		} catch (MalformedURLException e1) {
			System.out.println("Could not retrieve series id.  Bad URL: " + urlPath);
			return null;
		}
		List<Series> seriesList = new ArrayList<Series>();
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(seriesUrl.openStream(), new SeriesXmlHandler(seriesList));
		} catch (Exception e) {
			System.out.println("Failed to retrieve series id for series: " + seriesName);
			return null;
		}

		switch(seriesList.size()) {
		case 0:
			System.out.println("No series returned for search: " + seriesName);
			return null;
			//break;
		default:
			System.out.println("More than one series returned for search.  Using first result.  Search: " + seriesName);
			//fall through
		case 1:
			return seriesList.get(0).getId();
		}
	}

	private void getMirrorList() throws DatabaseInitializationException {
		URL mirrorUrl;
		try {
			mirrorUrl = new URL(MIRROR_FILE_URL);
		} catch (MalformedURLException mue) {
			throw new DatabaseInitializationException("Bad URL for mirrors file.  This is most likely a configuration issue", mue);
		}

		File tmpFile = new File(CACHE_DIR + "_" + LOCAL_MIRROR_FILE);
		if(tmpFile.exists()) {
			if(!tmpFile.delete()) {
				System.out.println("Could not delete temporary file.  Attempting to continue anyway.  File: " + tmpFile.getAbsolutePath());
			}
		}

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)));
			IterableBufferedReader in = new IterableBufferedReader(new InputStreamReader(mirrorUrl.openStream()));
			for(String s : in)
				out.println(s);

			in.close();
			out.close();
		} catch (IOException ioe) {
			throw new DatabaseInitializationException("Failed to retrieve mirror list.", ioe);
		}

		File outFile = new File(CACHE_DIR + LOCAL_MIRROR_FILE);
		if(outFile.exists()) {
			if(!outFile.delete()) {
				System.out.println("Could not delete temporary file.  Attempting to continue anyway.  File: " + outFile.getAbsolutePath());
			}
		}

		if(!tmpFile.renameTo(outFile)) {
			throw new DatabaseInitializationException(
					"Could not rename " + tmpFile.getAbsolutePath() + " to " + outFile.getAbsolutePath());
		}
	}

	private void parseMirrors() throws SAXException, IOException, ParserConfigurationException {
		mirrors = new ArrayList<Mirror>();
		SAXParserFactory.newInstance().newSAXParser().parse(new File(CACHE_DIR + LOCAL_MIRROR_FILE), new MirrorXmlHandler(mirrors));
	}
}
