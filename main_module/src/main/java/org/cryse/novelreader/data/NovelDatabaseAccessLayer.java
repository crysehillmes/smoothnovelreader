package org.cryse.novelreader.data;

import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;

import java.util.Collection;
import java.util.List;

public interface NovelDatabaseAccessLayer {
    // Favorite novel operation
    boolean isFavorite(String id);
    NovelModel loadFavorite(String id);
    void addToFavorite(NovelModel novel);
    void updateFavoritesStatus(Collection<NovelModel> novels);
    void removeFavorite(String... ids);
    List<NovelModel> loadAllFavorites();

    // Chapter list operation
    List<NovelChapterModel> loadChapters(String novelId);
    void insertChapter(String novelId, NovelChapterModel chapter);
    void insertChapters(String novelId, List<NovelChapterModel> chapters);
    void updateChapters(String novelId, List<NovelChapterModel> chapters);

    // Chapter content operation
    NovelChapterContentModel loadChapterContent(String chapterId);
    void removeChapterContent(String chapterId);
    void updateChapterContent(NovelChapterContentModel chapterContent);

    // Bookmark operation
    void addBookMark(NovelBookMarkModel bookMark);
    void insertOrUpdateLastReadBookMark(NovelBookMarkModel bookMark);
    NovelBookMarkModel loadLastReadBookMark(String novelId);
    List<NovelBookMarkModel> loadBookMarks(String novelId);
    NovelBookMarkModel checkLastReadBookMarkState(String novelId);

    // Change chapter source
    public NovelChapterModel changeChapterSource(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel);
}
