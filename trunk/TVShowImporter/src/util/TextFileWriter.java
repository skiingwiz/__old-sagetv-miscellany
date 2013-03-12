package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A convenience class for writing to text files
 *
 */
public class TextFileWriter {
	private PrintWriter decoration;
	
	public TextFileWriter(String file) throws IOException {
		decoration = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
	}

	public TextFileWriter print(boolean b) {
		decoration.print(b);
		return this;
	}
	
	public TextFileWriter print(char c) {
		decoration.print(c);
		return this;
	}
	
	public TextFileWriter print(char[] s) {
		decoration.print(s);
		return this;
	}
	
	public TextFileWriter print(double d) {
		decoration.print(d);
		return this;
	}
	
	public TextFileWriter print(float f) {
		decoration.print(f);
		return this;
	}
	
	public TextFileWriter print(int i) {
		decoration.print(i);
		return this;
	}
	
	public TextFileWriter print(long l) {
		decoration.print(l);
		return this;
	}
	
	public TextFileWriter print(Object obj) {
		decoration.print(obj);
		return this;
	}
	
	public TextFileWriter print(String s) {
		decoration.print(s);
		return this;
	}
	
	public TextFileWriter println() {
		decoration.println();
		return this;
	}
	
	public TextFileWriter println(boolean x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(char x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(char[] x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(double x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(float x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(int x) {
		decoration.println(x);
		return this;
	}

	public TextFileWriter println(long x) {
		decoration.println(x);
		return this;
	}
	
	public TextFileWriter println(Object x) {
		decoration.println(x);
		return this;
	}

	public TextFileWriter println(String x) {
		decoration.println(x);
		return this;
	}

	public void close() {
		decoration.close();
	}
}
