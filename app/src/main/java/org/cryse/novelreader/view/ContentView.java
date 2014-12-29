package org.cryse.novelreader.view;

import org.cryse.novelreader.util.ToastSupport;

public interface ContentView extends ToastSupport {
    public void setLoading(Boolean value);
    public Boolean isLoading();
}
