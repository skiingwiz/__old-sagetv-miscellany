package db.thetvdb;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilitiesTest {

    @Test
    public void normaizeTest() {

        String[][] testVals = {
                {"Test Number ’one`", "Test Number 'one'"},
                {"Test Number –– two", "Test Number -- two"}
        };

        for(String[] test : testVals) {
            String result = Utilities.normalizeString(test[0]);

            assertNotNull(result);
            assertEquals(test[1], result);
        }
    }
}
