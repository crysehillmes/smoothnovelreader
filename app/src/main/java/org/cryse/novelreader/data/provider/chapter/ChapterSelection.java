package org.cryse.novelreader.data.provider.chapter;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.novelreader.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code chapter} table.
 */
public class ChapterSelection extends AbstractSelection<ChapterSelection> {
    @Override
    protected Uri baseUri() {
        return ChapterColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ChapterCursor} object, which is positioned before the first entry, or null.
     */
    public ChapterCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ChapterCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public ChapterCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ChapterCursor} object, which is positioned before the first entry, or null.
     */
    public ChapterCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ChapterCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public ChapterCursor query(Context context) {
        return query(context, null);
    }


    public ChapterSelection id(long... value) {
        addEquals("chapter." + ChapterColumns._ID, toObjectArray(value));
        return this;
    }

    public ChapterSelection idNot(long... value) {
        addNotEquals("chapter." + ChapterColumns._ID, toObjectArray(value));
        return this;
    }

    public ChapterSelection orderById(boolean desc) {
        orderBy("chapter." + ChapterColumns._ID, desc);
        return this;
    }

    public ChapterSelection orderById() {
        return orderById(false);
    }

    public ChapterSelection novelId(String... value) {
        addEquals(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection novelIdNot(String... value) {
        addNotEquals(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection novelIdLike(String... value) {
        addLike(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection novelIdContains(String... value) {
        addContains(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection novelIdStartsWith(String... value) {
        addStartsWith(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection novelIdEndsWith(String... value) {
        addEndsWith(ChapterColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterSelection orderByNovelId(boolean desc) {
        orderBy(ChapterColumns.NOVEL_ID, desc);
        return this;
    }

    public ChapterSelection orderByNovelId() {
        orderBy(ChapterColumns.NOVEL_ID, false);
        return this;
    }

    public ChapterSelection chapterId(String... value) {
        addEquals(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection chapterIdNot(String... value) {
        addNotEquals(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection chapterIdLike(String... value) {
        addLike(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection chapterIdContains(String... value) {
        addContains(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection chapterIdStartsWith(String... value) {
        addStartsWith(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection chapterIdEndsWith(String... value) {
        addEndsWith(ChapterColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterSelection orderByChapterId(boolean desc) {
        orderBy(ChapterColumns.CHAPTER_ID, desc);
        return this;
    }

    public ChapterSelection orderByChapterId() {
        orderBy(ChapterColumns.CHAPTER_ID, false);
        return this;
    }

    public ChapterSelection source(String... value) {
        addEquals(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection sourceNot(String... value) {
        addNotEquals(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection sourceLike(String... value) {
        addLike(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection sourceContains(String... value) {
        addContains(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection sourceStartsWith(String... value) {
        addStartsWith(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection sourceEndsWith(String... value) {
        addEndsWith(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterSelection orderBySource(boolean desc) {
        orderBy(ChapterColumns.SOURCE, desc);
        return this;
    }

    public ChapterSelection orderBySource() {
        orderBy(ChapterColumns.SOURCE, false);
        return this;
    }

    public ChapterSelection title(String... value) {
        addEquals(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection titleNot(String... value) {
        addNotEquals(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection titleLike(String... value) {
        addLike(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection titleContains(String... value) {
        addContains(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection titleStartsWith(String... value) {
        addStartsWith(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection titleEndsWith(String... value) {
        addEndsWith(ChapterColumns.TITLE, value);
        return this;
    }

    public ChapterSelection orderByTitle(boolean desc) {
        orderBy(ChapterColumns.TITLE, desc);
        return this;
    }

    public ChapterSelection orderByTitle() {
        orderBy(ChapterColumns.TITLE, false);
        return this;
    }

    public ChapterSelection chapterIndex(int... value) {
        addEquals(ChapterColumns.CHAPTER_INDEX, toObjectArray(value));
        return this;
    }

    public ChapterSelection chapterIndexNot(int... value) {
        addNotEquals(ChapterColumns.CHAPTER_INDEX, toObjectArray(value));
        return this;
    }

    public ChapterSelection chapterIndexGt(int value) {
        addGreaterThan(ChapterColumns.CHAPTER_INDEX, value);
        return this;
    }

    public ChapterSelection chapterIndexGtEq(int value) {
        addGreaterThanOrEquals(ChapterColumns.CHAPTER_INDEX, value);
        return this;
    }

    public ChapterSelection chapterIndexLt(int value) {
        addLessThan(ChapterColumns.CHAPTER_INDEX, value);
        return this;
    }

    public ChapterSelection chapterIndexLtEq(int value) {
        addLessThanOrEquals(ChapterColumns.CHAPTER_INDEX, value);
        return this;
    }

    public ChapterSelection orderByChapterIndex(boolean desc) {
        orderBy(ChapterColumns.CHAPTER_INDEX, desc);
        return this;
    }

    public ChapterSelection orderByChapterIndex() {
        orderBy(ChapterColumns.CHAPTER_INDEX, false);
        return this;
    }
}
