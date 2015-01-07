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
    public boolean isFavorite(String id);
    public void addToFavorite(NovelModel novel);
    public void updateFavoritesStatus(Collection<NovelModel> novels);
    public void removeFavorite(String... ids);
    public List<NovelModel> loadAllFavorites();

    // Chapter list operation
    public List<NovelChapterModel> loadChapters(String novelId);
    public void insertChapters(String novelId, List<NovelChapterModel> chapters);
    public void removeChaptersByChapterId(String... chapterIds);
    public void removeChaptersByNovelId(String... novelIds);
    public void updateChapters(String novelId, List<NovelChapterModel> chapters);

    // Chapter content operation
    public NovelChapterContentModel loadChapterContent(String chapterId);
    public void removeChapterContent(String chapterId);
    public void updateChapterContent(NovelChapterContentModel chapterContent);

    // Bookmark operation
    public void addBookMark(NovelBookMarkModel bookMark);
    public void insertOrUpdateLastReadBookMark(NovelBookMarkModel bookMark);
    public NovelBookMarkModel loadLastReadBookMark(String novelId);
    public List<NovelBookMarkModel> loadBookMarks(String novelId);
    public NovelBookMarkModel checkLastReadBookMarkState(String novelId);

    // Change chapter source
    public NovelChapterModel changeChapterSource(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel);
}
