package db.thetvdb;

import java.util.ArrayList;
import java.util.List;

public final class Utilities {

	public static String[] makeArray(String str) {
		if(str == null)
			return null;


		String[] arr = str.split("\\|");

		List<String> l = new ArrayList<String>(arr.length);
		for(String s: arr) {
			if(s != null && s.length() > 0)
				l.add(s);
		}

		return l.toArray(new String[l.size()]);
	}

	private static final String[][] REPLACEMENTS = {
		{"’", "'"},
		{"`", "'"},
		{"–", "-"}
		};
	public static String normalizeString(String string)  {
		String retVal = string;
		for(String[] group : REPLACEMENTS) {
			retVal = retVal.replaceAll(group[0], group[1]);
		}

		return retVal;
	}

	private Utilities() {}
}
