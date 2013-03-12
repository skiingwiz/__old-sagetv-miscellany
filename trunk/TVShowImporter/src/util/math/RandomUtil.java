package util.math;

import java.util.Random;

public class RandomUtil {
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	/**
	 * Get a random integer in the range [lowerBound, upperBound).
	 * @param lowerBound The lower bound (inclusive) of the range constraining the return value 
	 * @param upperBound The upper bound (exclusive) of the range constraining the return value
	 * @return A random integer in the range given
	 * 
	 * @throws IllegalArgumentException if <code>upperBound - lowerBound &lt;= 0</code>
	 */
	public static int randomInt(int lowerBound, int upperBound) {
		return RANDOM.nextInt(upperBound - lowerBound) + lowerBound;
	}
}
