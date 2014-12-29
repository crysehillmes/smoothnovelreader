package org.cryse.novelreader.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import org.cryse.novelreader.model.NovelBookMarkModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table NOVEL_BOOK_MARK_MODEL.
*/
public class NovelBookMarkModelDao extends AbstractDao<NovelBookMarkModel, Void> {

    public static final String TABLENAME = "NOVEL_BOOK_MARK_MODEL";

    /**
     * Properties of entity NovelBookMarkModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", false, "ID");
        public final static Property ChapterTitle = new Property(1, String.class, "chapterTitle", false, "CHAPTER_TITLE");
        public final static Property NovelTitle = new Property(2, String.class, "novelTitle", false, "NOVEL_TITLE");
        public final static Property ChapterIndex = new Property(3, Integer.class, "chapterIndex", false, "CHAPTER_INDEX");
        public final static Property ChapterOffset = new Property(4, Integer.class, "chapterOffset", false, "CHAPTER_OFFSET");
        public final static Property BookMarkType = new Property(5, Integer.class, "bookMarkType", false, "BOOK_MARK_TYPE");
        public final static Property CreateTime = new Property(6, java.util.Date.class, "createTime", false, "CREATE_TIME");
    };


    public NovelBookMarkModelDao(DaoConfig config) {
        super(config);
    }
    
    public NovelBookMarkModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'NOVEL_BOOK_MARK_MODEL' (" + //
                "'ID' TEXT," + // 0: id
                "'CHAPTER_TITLE' TEXT," + // 1: chapterTitle
                "'NOVEL_TITLE' TEXT," + // 2: novelTitle
                "'CHAPTER_INDEX' INTEGER," + // 3: chapterIndex
                "'CHAPTER_OFFSET' INTEGER," + // 4: chapterOffset
                "'BOOK_MARK_TYPE' INTEGER," + // 5: bookMarkType
                "'CREATE_TIME' INTEGER);"); // 6: createTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NOVEL_BOOK_MARK_MODEL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NovelBookMarkModel entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String chapterTitle = entity.getChapterTitle();
        if (chapterTitle != null) {
            stmt.bindString(2, chapterTitle);
        }
 
        String novelTitle = entity.getNovelTitle();
        if (novelTitle != null) {
            stmt.bindString(3, novelTitle);
        }
 
        Integer chapterIndex = entity.getChapterIndex();
        if (chapterIndex != null) {
            stmt.bindLong(4, chapterIndex);
        }
 
        Integer chapterOffset = entity.getChapterOffset();
        if (chapterOffset != null) {
            stmt.bindLong(5, chapterOffset);
        }
 
        Integer bookMarkType = entity.getBookMarkType();
        if (bookMarkType != null) {
            stmt.bindLong(6, bookMarkType);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(7, createTime.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public NovelBookMarkModel readEntity(Cursor cursor, int offset) {
        NovelBookMarkModel entity = new NovelBookMarkModel( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // chapterTitle
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // novelTitle
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // chapterIndex
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // chapterOffset
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // bookMarkType
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)) // createTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NovelBookMarkModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setChapterTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNovelTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChapterIndex(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setChapterOffset(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setBookMarkType(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setCreateTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(NovelBookMarkModel entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(NovelBookMarkModel entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
