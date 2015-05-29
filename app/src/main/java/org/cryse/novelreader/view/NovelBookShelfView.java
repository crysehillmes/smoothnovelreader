package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelModel;

import java.util.List;

public interface NovelBookShelfView extends ContentView {
    void showBooksOnShelf(List<NovelModel> books);
    void showAddLocalBookProgressDialog(boolean show);
}
