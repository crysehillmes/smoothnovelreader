package org.cryse.novelreader.data.provider.novel;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.novelreader.data.provider.NovelReaderContentProvider;

/**
 * A human being which is part of a team.
 */
public class NovelColumns implements BaseColumns {
    public static final String TABLE_NAME = "novel";
    public static final Uri CONTENT_URI = Uri.parse(NovelReaderContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String NOVEL_ID = "novel_id";

    public static final String TITLE = "title";

    public static final String AUTHOR = "author";

    public static final String TYPE = "type";

    public static final String SOURCE = "source";

    public static final String COVER_IMAGE = "cover_image";

    public static final String CHAPTER_COUNT = "chapter_count";

    public static final String LAST_READ_CHAPTER_TITLE = "last_read_chapter_title";

    public static final String LATEST_CHAPTER_TITLE = "latest_chapter_title";

    public static final String LATEST_CHAPTER_ID = "latest_chapter_id";

    public static final String LATEST_UPDATE_CHAPTER_COUNT = "latest_update_chapter_count";

    public static final String SORT_KEY = "sort_key";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NOVEL_ID,
            TITLE,
            AUTHOR,
            TYPE,
            SOURCE,
            COVER_IMAGE,
            CHAPTER_COUNT,
            LAST_READ_CHAPTER_TITLE,
            LATEST_CHAPTER_TITLE,
            LATEST_CHAPTER_ID,
            LATEST_UPDATE_CHAPTER_COUNT,
            SORT_KEY
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NOVEL_ID) || c.contains("." + NOVEL_ID)) return true;
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(AUTHOR) || c.contains("." + AUTHOR)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(SOURCE) || c.contains("." + SOURCE)) return true;
            if (c.equals(COVER_IMAGE) || c.contains("." + COVER_IMAGE)) return true;
            if (c.equals(CHAPTER_COUNT) || c.contains("." + CHAPTER_COUNT)) return true;
            if (c.equals(LAST_READ_CHAPTER_TITLE) || c.contains("." + LAST_READ_CHAPTER_TITLE)) return true;
            if (c.equals(LATEST_CHAPTER_TITLE) || c.contains("." + LATEST_CHAPTER_TITLE)) return true;
            if (c.equals(LATEST_CHAPTER_ID) || c.contains("." + LATEST_CHAPTER_ID)) return true;
            if (c.equals(LATEST_UPDATE_CHAPTER_COUNT) || c.contains("." + LATEST_UPDATE_CHAPTER_COUNT)) return true;
            if (c.equals(SORT_KEY) || c.contains("." + SORT_KEY)) return true;
        }
        return false;
    }

}
