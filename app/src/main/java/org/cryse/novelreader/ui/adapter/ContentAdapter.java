package org.cryse.novelreader.ui.adapter;

import java.util.List;

public interface ContentAdapter<T> {
    public void addAll(List<T> data);
    public void addAll(int position, List<T> data);
    public void add(T data);
    public void add(int position, T data);
    public void remove(int position);
}