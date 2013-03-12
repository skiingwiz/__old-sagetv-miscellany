package util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * A <code>BufferedReader</code> which is <code>Iterable</code>.  Because
 * this iteration is over a stream, iteration can only happen once.  Thus,
 * the <code>iterator</code> method may only be called once.
 * 
 * This class is primarily intended to allow <code>BufferedReader</code>s to 
 * be used in for-each style loops.
 */
public class IterableBufferedReader extends BufferedReader implements Iterable<String> {
	private _Iterator iter;
	
	public IterableBufferedReader(Reader arg0, int arg1) {
		super(arg0, arg1);
	}

	public IterableBufferedReader(Reader arg0) {
		super(arg0);
	}

	/**
	 * Get an <code>Iterator</code> over this stream.  
	 * 
	 * NB: This method may only be called <b>ONCE<b>.  Subsequent
	 * calls will throw a <code>RuntimeException</code>  This is due to
	 * the nature of the stream over which it is iterating.
	 */
	@Override
	public Iterator<String> iterator() {
		if(iter == null) {
			iter = new _Iterator();
			return iter;
		} else {
			throw new RuntimeException();
		}
		
	}

	private class _Iterator implements Iterator<String> {
		private String nextLine;
		
		public _Iterator() {
			try {
				nextLine = readLine();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		@Override
		public boolean hasNext() {
			return nextLine != null;
		}

		@Override
		public String next() {
			String retVal = nextLine;
			try {
				nextLine = readLine();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			return retVal;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
