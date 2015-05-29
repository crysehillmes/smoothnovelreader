package org.cryse.novelreader.view;

import org.cryse.novelreader.util.SnackbarSupport;

public interface ContentView extends SnackbarSupport {
    public void setLoading(Boolean value);
    public Boolean isLoading();
}
