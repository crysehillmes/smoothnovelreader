package org.cryse.novelreader.data.provider.chaptercontent;

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
public class ChapterContentColumns implements BaseColumns {
    public static final String TABLE_NAME = "chapter_content";
    public static final Uri CONTENT_URI = Uri.parse(NovelReaderContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String NOVEL_ID = "novel_id";

    public static final String CHAPTER_ID = "chapter_id";

    public static final String SOURCE = "source";

    public static final String CONTENT = "content";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NOVEL_ID,
            CHAPTER_ID,
            SOURCE,
            CONTENT
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NOVEL_ID) || c.contains("." + NOVEL_ID)) return true;
            if (c.equals(CHAPTER_ID) || c.contains("." + CHAPTER_ID)) return true;
            if (c.equals(SOURCE) || c.contains("." + SOURCE)) return true;
            if (c.equals(CONTENT) || c.contains("." + CONTENT)) return true;
        }
        return false;
    }

}
