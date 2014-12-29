package org.cryse.novelreader.util;

public interface RunTimeStore {
    public void put(String key, Object obj);
    public Object get(String key);
    public void remove(String key);
    public void clear();
    public boolean containsKey(String key);
    public int size();
}
