package org.cryse.novelreader.data;

        import android.content.ContentProviderClient;
        import android.content.ContentResolver;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;

        import org.cryse.novelreader.data.provider.ContentValuesUtils;
        import org.cryse.novelreader.data.provider.NovelReaderContentProvider;
        import org.cryse.novelreader.data.provider.bookmark.BookmarkContentValues;
        import org.cryse.novelreader.data.provider.bookmark.BookmarkCursor;
        import org.cryse.novelreader.data.provider.bookmark.BookmarkSelection;
        import org.cryse.novelreader.data.provider.chapter.ChapterColumns;
        import org.cryse.novelreader.data.provider.chapter.ChapterContentValues;
        import org.cryse.novelreader.data.provider.chapter.ChapterSelection;
        import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentContentValues;
        import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentCursor;
        import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentSelection;
        import org.cryse.novelreader.data.provider.novel.NovelContentValues;
        import org.cryse.novelreader.data.provider.novel.NovelCursor;
        import org.cryse.novelreader.data.provider.novel.NovelSelection;
        import org.cryse.novelreader.model.Bookmark;
        import org.cryse.novelreader.model.BookmarkModel;
        import org.cryse.novelreader.model.Chapter;
        import org.cryse.novelreader.model.ChapterContent;
        import org.cryse.novelreader.model.Novel;
        import org.cryse.novelreader.model.NovelChangeSrcModel;
        import org.cryse.novelreader.model.ChapterContentModel;
        import org.cryse.novelreader.model.ChapterModel;
        import org.cryse.novelreader.model.NovelModel;
        import org.cryse.novelreader.qualifier.ApplicationContext;
        import org.cryse.novelreader.util.comparator.NovelSortKeyComparator;

        import java.util.ArrayList;
        import java.util.Collection;
        import java.util.Collections;
        import java.util.List;

        import javax.inject.Inject;

public class NovelDatabaseAccessLayerImpl implements NovelDatabaseAccessLayer {
    @ApplicationContext
    Context mContext;
    ContentResolver mContentResolver;

    @Inject
    public NovelDatabaseAccessLayerImpl(@ApplicationContext Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
    }

    @Override
    public boolean isFavorite(String id) {
        NovelSelection novelSelection = new NovelSelection();
        NovelCursor cursor = novelSelection.novelId(id).query(mContentResolver);
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    @Override
    public NovelModel loadFavorite(String id) {
        NovelSelection novelSelection = new NovelSelection();
        novelSelection.novelId(id);
        NovelCursor cursor = novelSelection.query(mContentResolver);
        Novel novel = null;
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            novel = new Novel(cursor);
            cursor.close();
        }
        return novel;
    }

    @Override
    public void addToFavorite(NovelModel novel) {
        NovelContentValues values = ContentValuesUtils.toNovelContentValues(novel);
        values.insert(mContentResolver);
    }

    @Override
    public void updateFavoritesStatus(Collection<NovelModel> novels) {
        for(NovelModel model : novels) {
            NovelContentValues values = ContentValuesUtils.toNovelContentValues(model);
            values.insert(mContentResolver);
        }
    }

    @Override
    public void removeFavorite(String... novelIds) {
        // Clear BookMark
        BookmarkSelection bookmarkSelection = new BookmarkSelection();
        bookmarkSelection.novelId(novelIds).delete(mContentResolver);

        // Clear ChapterContents
        ChapterContentSelection contentSelection = new ChapterContentSelection();
        contentSelection.novelId(novelIds).delete(mContentResolver);

        // Clear Chapters
        ChapterSelection chapterSelection = new ChapterSelection();
        chapterSelection.novelId(novelIds).delete(mContentResolver);

        // Remove NovelModel
        NovelSelection novelSelection = new NovelSelection();
        novelSelection.novelId(novelIds).delete(mContentResolver);
    }

    @Override
    public List<NovelModel> loadAllFavorites() {
        NovelSelection novelSelection = new NovelSelection();
        NovelCursor cursor = novelSelection.query(mContentResolver);
        List<NovelModel> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result.add(new Novel(cursor));
        }
        cursor.close();
        Collections.sort(result, new NovelSortKeyComparator());
        return result;
    }

    @Override
    public List<ChapterModel> loadChapters(String novelId) {
        return ChapterModelDao.deepQueryList(novelId);
    }

    @Override
    public void insertChapter(String novelId, ChapterModel chapter) {
        ChapterContentValues values = ContentValuesUtils.toChapterValues(chapter);
        values.insert(mContentResolver);
        Novel novel = (Novel)loadFavorite(novelId);
        novel.setLatestUpdateChapterCount(0);
        novel.setLatestChapterTitle(chapter.getTitle());
        NovelContentValues novelContentValues = ContentValuesUtils.toNovelContentValues(novel);
        novelContentValues.insert(mContentResolver);
    }

    @Override
    public void insertChapters(String novelId, List<ChapterModel> chapters) {
        ContentValues[] values = new ContentValues[chapters.size()];
        for(int i = 0; i < chapters.size(); i++) {
            values[i] = ContentValuesUtils.toChapterValues(chapters.get(i)).values();
        }
        mContentResolver.bulkInsert(ChapterColumns.CONTENT_URI, values);

        Novel novel = (Novel)loadFavorite(novelId);
        novel.setLatestUpdateChapterCount(0);
        novel.setLatestChapterTitle(chapters.get(chapters.size() - 1).getTitle());
        NovelContentValues novelContentValues = ContentValuesUtils.toNovelContentValues(novel);
        novelContentValues.insert(mContentResolver);
    }

    @Override
    public void updateChapters(String novelId, List<ChapterModel> chapters) {
        ChapterSelection chapterSelection = new ChapterSelection();
        chapterSelection.novelId(novelId).delete(mContentResolver);
        insertChapters(novelId, chapters);
    }

    @Override
    public ChapterContentModel loadChapterContent(String chapterId) {
        ChapterContentSelection contentSelection = new ChapterContentSelection();
        contentSelection.chapterId(chapterId);
        ChapterContentCursor cursor = contentSelection.query(mContentResolver);
        ChapterContent chapterContent = null;
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            chapterContent = new ChapterContent(cursor);
        }
        return chapterContent;
    }

    @Override
    public void removeChapterContent(String chapterId) {
        ChapterContentSelection contentSelection = new ChapterContentSelection();
        contentSelection.chapterId(chapterId);
        contentSelection.delete(mContentResolver);
    }

    @Override
    public void updateChapterContent(ChapterContentModel chapterContent) {
        /*ChapterContentSelection contentSelection = new ChapterContentSelection();
        contentSelection.chapterId(chapterContent.getChapterId());
        contentSelection.delete(mContentResolver);*/
        ChapterContentContentValues values = ContentValuesUtils.toChapterContentValues(chapterContent);
        values.insert(mContentResolver);
    }

    @Override
    public void addBookMark(BookmarkModel bookMark) {
        BookmarkContentValues contentValues = ContentValuesUtils.toBookmarkContentValues(bookMark);
        contentValues.insert(mContentResolver);
    }

    @Override
    public void insertOrUpdateLastReadBookMark(BookmarkModel bookmark) {
        // Replace existing last read bookmark and by the new one.
        BookmarkSelection bookmarkSelection = new BookmarkSelection();
        bookmarkSelection.novelId(bookmark.getNovelId()).and().markType(BookmarkModel.BOOKMARK_TYPE_LASTREAD);
        bookmarkSelection.delete(mContentResolver);
        BookmarkContentValues contentValues = ContentValuesUtils.toBookmarkContentValues(bookmark);
        contentValues.insert(mContentResolver);

        // Update NovelModel lastReadChapterTitle field.
        Novel novel = (Novel) loadFavorite(bookmark.getNovelId());
        novel.setLastReadChapterTitle(bookmark.getChapterTitle());
        NovelContentValues novelContentValues = ContentValuesUtils.toNovelContentValues(novel);
        novelContentValues.insert(mContentResolver);
    }

    @Override
    public BookmarkModel loadLastReadBookMark(String novelId) {
        // get last read bookmark according to novelId.
        BookmarkSelection bookmarkSelection = new BookmarkSelection();
        bookmarkSelection.novelId(novelId).and().markType(BookmarkModel.BOOKMARK_TYPE_LASTREAD);
        BookmarkCursor cursor = bookmarkSelection.query(mContentResolver);
        Bookmark bookmark = null;
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            bookmark = new Bookmark(cursor);
            cursor.close();
        }
        return bookmark;
    }

    @Override
    public List<BookmarkModel> loadBookMarks(String novelId) {
        // get all bookmarks according to novelId.
        BookmarkSelection bookmarkSelection = new BookmarkSelection();
        bookmarkSelection.novelId(novelId).and().markType(BookmarkModel.BOOKMARK_TYPE_LASTREAD);
        BookmarkCursor cursor = bookmarkSelection.query(mContentResolver);
        List<BookmarkModel> bookmarks = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bookmarks.add(new Bookmark(cursor));
        }
        cursor.close();
        return bookmarks;
    }

    @Override
    public BookmarkModel checkLastReadBookMarkState(String novelId) {
        Boolean isFavorite = isFavorite(novelId);
        BookmarkSelection selection = new BookmarkSelection();
        selection.novelId(novelId).and().markType(BookmarkModel.BOOKMARK_TYPE_LASTREAD);
        BookmarkCursor cursor = selection.query(mContentResolver);
        Boolean hasLastRead = cursor.getCount() > 0;
        Bookmark bookmark = null;
        if(isFavorite && hasLastRead) {
            cursor.moveToFirst();
            bookmark = new Bookmark(cursor);
        }
        return bookmark;
    }

    @Override
    public ChapterModel changeChapterSource(ChapterModel chapterModel, NovelChangeSrcModel changeSrcModel) {
        if(isFavorite(chapterModel.getNovelId())) {
            ChapterSelection chapterSelection = new ChapterSelection();
            chapterSelection.novelId(chapterModel.getNovelId()).and().source(chapterModel.getSource());
            chapterSelection.delete(mContentResolver);
            ChapterContentSelection contentSelection = new ChapterContentSelection();
            contentSelection.novelId(chapterModel.getNovelId()).and().source(chapterModel.getSource());
            contentSelection.delete(mContentResolver);

            ChapterContentValues values = ContentValuesUtils.toChapterValues(chapterModel);
            values.putSource(changeSrcModel.getSrc());
            values.insert(mContentResolver);
        }
        Chapter chapter = new Chapter(chapterModel);
        chapter.setSource(changeSrcModel.getSrc());
        return chapter;
    }
}
