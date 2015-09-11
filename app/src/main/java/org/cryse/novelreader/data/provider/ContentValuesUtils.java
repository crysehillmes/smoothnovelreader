package org.cryse.novelreader.data.provider;

import org.cryse.novelreader.data.provider.bookmark.BookmarkContentValues;
import org.cryse.novelreader.data.provider.chapter.ChapterContentValues;
import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentContentValues;
import org.cryse.novelreader.data.provider.novel.NovelContentValues;
import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelModel;

public class ContentValuesUtils {
    public static NovelContentValues toNovelContentValues(NovelModel novel) {
        NovelContentValues values = new NovelContentValues();
        values.putNovelId(novel.getNovelId());
        values.putTitle(novel.getTitle());
        values.putAuthor(novel.getAuthor());
        values.putType(novel.getType());
        values.putSource(novel.getSource());
        values.putCoverImage(novel.getCoverImage());
        values.putChapterCount(novel.getChapterCount());
        values.putLastReadChapterTitle(novel.getLastReadChapterTitle());
        values.putLatestChapterTitle(novel.getLatestChapterTitle());
        values.putLatestUpdateChapterCount(novel.getLatestUpdateChapterCount());
        values.putSortKey(novel.getSortKey());
        return values;
    }

    public static BookmarkContentValues toBookmarkContentValues(BookmarkModel bookmark) {
        BookmarkContentValues values = new BookmarkContentValues();
        values.putNovelId(bookmark.getNovelId());
        values.putChapterId(bookmark.getChapterId());
        values.putNovelTitle(bookmark.getNovelTitle());
        values.putChapterTitle(bookmark.getChapterTitle());
        values.putChapterOffset(bookmark.getChapterOffset());
        values.putMarkType(bookmark.getMarkType());
        values.putCreateTime(bookmark.getCreateTime());
        return values;
    }

    public static ChapterContentValues toChapterValues(ChapterModel chapter) {
        ChapterContentValues values = new ChapterContentValues();
        values.putNovelId(chapter.getNovelId());
        values.putChapterId(chapter.getChapterId());
        values.putTitle(chapter.getTitle());
        values.putSource(chapter.getSource());
        values.putChapterIndex(chapter.getChapterIndex());
        return values;
    }

    public static ChapterContentContentValues toChapterContentValues(ChapterContentModel chapter) {
        ChapterContentContentValues values = new ChapterContentContentValues();
        values.putNovelId(chapter.getNovelId());
        values.putChapterId(chapter.getChapterId());
        values.putSource(chapter.getSource());
        values.putContent(chapter.getContent());
        return values;
    }
}
