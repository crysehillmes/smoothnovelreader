package org.cryse.novelreader.data.provider.bookmark;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.novelreader.data.provider.NovelReaderContentProvider;
import org.cryse.novelreader.data.provider.bookmark.BookmarkColumns;
import org.cryse.novelreader.data.provider.chapter.ChapterColumns;
import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentColumns;
import org.cryse.novelreader.data.provider.novel.NovelColumns;

/**
 * A human being which is part of a team.
 */
public class BookmarkColumns implements BaseColumns {
    public static final String TABLE_NAME = "bookmark";
    public static final Uri CONTENT_URI = Uri.parse(NovelReaderContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String NOVEL_ID = "novel_id";

    public static final String CHAPTER_ID = "chapter_id";

    public static final String NOVEL_TITLE = "novel_title";

    public static final String CHAPTER_TITLE = "chapter_title";

    public static final String CHAPTER_OFFSET = "chapter_offset";

    public static final String MARK_TYPE = "mark_type";

    public static final String CREATE_TIME = "create_time";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NOVEL_ID,
            CHAPTER_ID,
            NOVEL_TITLE,
            CHAPTER_TITLE,
            CHAPTER_OFFSET,
            MARK_TYPE,
            CREATE_TIME
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NOVEL_ID) || c.contains("." + NOVEL_ID)) return true;
            if (c.equals(CHAPTER_ID) || c.contains("." + CHAPTER_ID)) return true;
            if (c.equals(NOVEL_TITLE) || c.contains("." + NOVEL_TITLE)) return true;
            if (c.equals(CHAPTER_TITLE) || c.contains("." + CHAPTER_TITLE)) return true;
            if (c.equals(CHAPTER_OFFSET) || c.contains("." + CHAPTER_OFFSET)) return true;
            if (c.equals(MARK_TYPE) || c.contains("." + MARK_TYPE)) return true;
            if (c.equals(CREATE_TIME) || c.contains("." + CREATE_TIME)) return true;
        }
        return false;
    }

}
