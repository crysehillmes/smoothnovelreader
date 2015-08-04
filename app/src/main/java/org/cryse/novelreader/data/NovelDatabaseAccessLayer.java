package org.cryse.novelreader.data;

import org.cryse.novelreader.model.Bookmark;
import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.Chapter;
import org.cryse.novelreader.model.ChapterContent;
import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
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
    List<ChapterModel> loadChapters(String novelId);
    void insertChapter(String novelId, ChapterModel chapter);
    void insertChapters(String novelId, List<ChapterModel> chapters);
    void updateChapters(String novelId, List<ChapterModel> chapters);

    // Chapter content operation
    ChapterContentModel loadChapterContent(String chapterId);
    void removeChapterContent(String chapterId);
    void updateChapterContent(ChapterContentModel chapterContent);

    // Bookmark operation
    void addBookMark(BookmarkModel bookMark);
    void insertOrUpdateLastReadBookMark(BookmarkModel bookMark);
    BookmarkModel loadLastReadBookMark(String novelId);
    List<BookmarkModel> loadBookMarks(String novelId);
    BookmarkModel checkLastReadBookMarkState(String novelId);

    // Change chapter source
    ChapterModel changeChapterSource(ChapterModel chapterModel, NovelChangeSrcModel changeSrcModel);
}
