package org.cryse.novelreader.presenter.common;

/**
 * Created by cryse on 14-7-12.
 */
public interface BasePresenter<T> {
    void bindView(T view);
    void unbindView();
    void destroy();
}
