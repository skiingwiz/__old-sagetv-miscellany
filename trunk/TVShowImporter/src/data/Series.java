package data;

public class Series {
	private String id;
	private String language;
	private String name;
	private String originalName;
	private String bannerLocation;
	private String description;
	private String firstAirDate;
	private String imdbId;
	private String zap2itId;
	private long runtime;
	private String starRating;
	private String network;
	private String[] genre;
	private String contentRating;
	private String airTime;
	private String airDay;
	private String[] actors;

	public Series() {}

	public Series(String bannerLocation, String description,
			String firstAirDate, String id, String imdbId, String language,
			String name, String zap2itId) {
		super();
		this.bannerLocation = bannerLocation;
		this.description = description;
		this.firstAirDate = firstAirDate;
		this.id = id;
		this.imdbId = imdbId;
		this.language = language;
		this.name = name;
		this.zap2itId = zap2itId;
	}

	public String getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}

	public String getName() {
		return name;
	}

	public String getOriginalName() {
	    return originalName;
	}

	public String getBannerLocation() {
		return bannerLocation;
	}

	public String getDescription() {
		return description;
	}

	public String getFirstAirDate() {
		return firstAirDate;
	}

	public String getImdbId() {
		return imdbId;
	}

	public String getZap2itId() {
		return zap2itId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOriginalName(String originalName) {
	    this.originalName = originalName;
	}

	public void setBannerLocation(String bannerLocation) {
		this.bannerLocation = bannerLocation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFirstAirDate(String firstAirDate) {
		this.firstAirDate = firstAirDate;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public void setZap2itId(String zap2itId) {
		this.zap2itId = zap2itId;
	}

	public void setActors(String[] actors) {
		this.actors = actors;
	}

	public void setAirDay(String day) {
		this.airDay = day;
	}

	public void setAirTime(String time) {
		this.airTime = time;
	}

	public void setContentRating(String rating) {
		this.contentRating = rating;
	}

	public void setGenre(String[] genre) {
		this.genre = genre;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public void setStarRating(String rating) {
		this.starRating = rating;
	}

	/**
	 * Set the runtime, in milliseconds
	 * @param runtime the runtime, in milliseconds
	 */
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}

	public long getRuntime() {
		return runtime;
	}

	public String getStarRating() {
		return starRating;
	}

	public String getNetwork() {
		return network;
	}

	public String[] getGenre() {
		return genre;
	}

	public String getContentRating() {
		return contentRating;
	}

	public String getAirTime() {
		return airTime;
	}

	public String getAirDay() {
		return airDay;
	}

	public String[] getActors() {
		return actors;
	}
}
