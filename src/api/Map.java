package api;

import java.util.ArrayList;
import java.util.List;

public class Map<K, V> {

	private List<K> keys;
	private List<V> values;
	
	public Map() {
		this.keys = new ArrayList<K>();
		this.values = new ArrayList<V>();
	}
	
	public void put (K key, V val) {
		if (this.keys.contains(key)) {
			int idx = this.keys.indexOf(key);
			this.values.remove(idx);
			this.values.add(idx, val);
		} else {
			this.keys.add(key);
			this.values.add(val);
		}
	}
	
	public V get (K key) {
		if (!this.keys.contains(key))
			return null;
		
		int idx = this.keys.indexOf(key);
		return this.values.get(idx);
	}

	public boolean containsKey(K key) {
		return this.keys.contains(key);
	}
}
