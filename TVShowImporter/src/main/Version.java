package main;

public class Version {
	public static String getFullNameVersion() {
	    //the version string is replaced by ant at build time
		return "TV File Processor (v@@VERSION@@)";
	}


	private Version() {}
}
