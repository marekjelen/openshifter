package eu.mjelen.warden.tool;

import java.util.HashMap;
import java.util.Map;

public class Maps<K,V> {

    private final Map<K,V> map;

    public static <A,B> Maps<A,B> get(Class<? extends A> k, Class<? extends B> v) {
        return new Maps<>();
    }

    public static Maps<Object, Object> get() {
        return get(Object.class, Object.class);
    }

    public static Maps<String, Object> dict() {
        return get(String.class, Object.class);
    }

    public Maps() {
        this.map = new HashMap<>();
    }

    public Maps<K,V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K,V> build() {
        return this.map;
    }

}
