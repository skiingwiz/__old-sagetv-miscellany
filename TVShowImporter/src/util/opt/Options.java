package util.opt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Options {
	private Map<String, String> map = new HashMap<String, String>();
	private List<String> pos = new ArrayList<String>();
	
	/**
	 * Note: Positional parameters are 1 indexed <b> NOT 0 indexed</b>
	 * @param pos
	 * @param value
	 */
	public void addPositional(int pos, String value) {
		this.pos.add(pos - 1, value);
	}
	
	public void add(String name, String value) {
		map.put(name, value);
	}

	public void add(String name, boolean b) {
		map.put(name, Boolean.toString(b));
	}


	public void add(String name, Object value) {
		map.put(name, value.toString());
	}

	public Object get(String name) {
		return map.get(name);
	}
	
	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(map.get(name));
	}
	
	public String getString(String name) {
		return map.get(name);
	}
	
	public int getInt(String name) {
		return Integer.parseInt(map.get(name));
	}
	
	public long getLong(String name) {
		return Long.parseLong(map.get(name));
	}
	
	public float getFloat(String name) {
		return Float.parseFloat(map.get(name));
	}
	
	public double getDouble(String name) {
		return Double.parseDouble(map.get(name));
	}
	
	public BigDecimal getBigDecimal(String name) {
		return new BigDecimal(map.get(name));
	}

	public String getPositional(int i) {
		return pos.get(i - 1);
	}
	
	public List<String> getPositionals() {
		return Collections.unmodifiableList(pos);
	}
	
	public int countPositionals() {
		return pos.size();
	}
}
