package main;
import org.junit.Test;

import data.Episode;
import db.thetvdb.TheTvDbDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Tests {

	//This is a vanilla test that should work out of the box
	@Test
	public void testBones5x1() throws Exception {
		TheTvDbDatabase db = new TheTvDbDatabase("en");
		db.initialize();

		Episode e = db.lookup("Bones", 5, 1);

		assertEquals("Episode title does not match", "Harbingers in the Fountain", e.getEpisodeTitle());
		assertEquals("Season number does not match", 5, e.getSeasonNum());
		assertEquals("Episode number does not match", 1, e.getEpisodeNum());

		System.out.println(e);
	}

	//This is a test that requires a lookup override in the series id file
	@Test
	public void testCastle5x1() throws Exception {
		TheTvDbDatabase db = new TheTvDbDatabase("en");
		db.initialize();

		Episode e = db.lookup("Castle", 5, 1);
		assertNotNull("Episode not linked to Series", e.getSeries());
		assertEquals("Fanart name doesn't match", "Castle (2009)", e.getSeries().getOriginalName());
	    assertEquals("Episode title does not match", "After The Storm", e.getEpisodeTitle());
	    assertEquals("Season number does not match", 5, e.getSeasonNum());
	    assertEquals("Episode number does not match", 1, e.getEpisodeNum());

		System.out.println(e);
	}
}
