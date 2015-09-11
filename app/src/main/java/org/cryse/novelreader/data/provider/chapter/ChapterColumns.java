package org.cryse.novelreader.data.provider.chapter;

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
public class ChapterColumns implements BaseColumns {
    public static final String TABLE_NAME = "chapter";
    public static final Uri CONTENT_URI = Uri.parse(NovelReaderContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String NOVEL_ID = "novel_id";

    public static final String CHAPTER_ID = "chapter_id";

    public static final String SOURCE = "source";

    public static final String TITLE = "title";

    public static final String CHAPTER_INDEX = "chapter_index";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NOVEL_ID,
            CHAPTER_ID,
            SOURCE,
            TITLE,
            CHAPTER_INDEX
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NOVEL_ID) || c.contains("." + NOVEL_ID)) return true;
            if (c.equals(CHAPTER_ID) || c.contains("." + CHAPTER_ID)) return true;
            if (c.equals(SOURCE) || c.contains("." + SOURCE)) return true;
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(CHAPTER_INDEX) || c.contains("." + CHAPTER_INDEX)) return true;
        }
        return false;
    }

}
