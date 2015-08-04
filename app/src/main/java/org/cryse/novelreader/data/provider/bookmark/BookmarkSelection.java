package org.cryse.novelreader.data.provider.bookmark;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.novelreader.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code bookmark} table.
 */
public class BookmarkSelection extends AbstractSelection<BookmarkSelection> {
    @Override
    protected Uri baseUri() {
        return BookmarkColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BookmarkCursor} object, which is positioned before the first entry, or null.
     */
    public BookmarkCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BookmarkCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public BookmarkCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BookmarkCursor} object, which is positioned before the first entry, or null.
     */
    public BookmarkCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BookmarkCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public BookmarkCursor query(Context context) {
        return query(context, null);
    }


    public BookmarkSelection id(long... value) {
        addEquals("bookmark." + BookmarkColumns._ID, toObjectArray(value));
        return this;
    }

    public BookmarkSelection idNot(long... value) {
        addNotEquals("bookmark." + BookmarkColumns._ID, toObjectArray(value));
        return this;
    }

    public BookmarkSelection orderById(boolean desc) {
        orderBy("bookmark." + BookmarkColumns._ID, desc);
        return this;
    }

    public BookmarkSelection orderById() {
        return orderById(false);
    }

    public BookmarkSelection novelId(String... value) {
        addEquals(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection novelIdNot(String... value) {
        addNotEquals(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection novelIdLike(String... value) {
        addLike(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection novelIdContains(String... value) {
        addContains(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection novelIdStartsWith(String... value) {
        addStartsWith(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection novelIdEndsWith(String... value) {
        addEndsWith(BookmarkColumns.NOVEL_ID, value);
        return this;
    }

    public BookmarkSelection orderByNovelId(boolean desc) {
        orderBy(BookmarkColumns.NOVEL_ID, desc);
        return this;
    }

    public BookmarkSelection orderByNovelId() {
        orderBy(BookmarkColumns.NOVEL_ID, false);
        return this;
    }

    public BookmarkSelection chapterId(String... value) {
        addEquals(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection chapterIdNot(String... value) {
        addNotEquals(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection chapterIdLike(String... value) {
        addLike(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection chapterIdContains(String... value) {
        addContains(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection chapterIdStartsWith(String... value) {
        addStartsWith(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection chapterIdEndsWith(String... value) {
        addEndsWith(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }

    public BookmarkSelection orderByChapterId(boolean desc) {
        orderBy(BookmarkColumns.CHAPTER_ID, desc);
        return this;
    }

    public BookmarkSelection orderByChapterId() {
        orderBy(BookmarkColumns.CHAPTER_ID, false);
        return this;
    }

    public BookmarkSelection novelTitle(String... value) {
        addEquals(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection novelTitleNot(String... value) {
        addNotEquals(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection novelTitleLike(String... value) {
        addLike(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection novelTitleContains(String... value) {
        addContains(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection novelTitleStartsWith(String... value) {
        addStartsWith(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection novelTitleEndsWith(String... value) {
        addEndsWith(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkSelection orderByNovelTitle(boolean desc) {
        orderBy(BookmarkColumns.NOVEL_TITLE, desc);
        return this;
    }

    public BookmarkSelection orderByNovelTitle() {
        orderBy(BookmarkColumns.NOVEL_TITLE, false);
        return this;
    }

    public BookmarkSelection chapterTitle(String... value) {
        addEquals(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection chapterTitleNot(String... value) {
        addNotEquals(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection chapterTitleLike(String... value) {
        addLike(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection chapterTitleContains(String... value) {
        addContains(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection chapterTitleStartsWith(String... value) {
        addStartsWith(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection chapterTitleEndsWith(String... value) {
        addEndsWith(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkSelection orderByChapterTitle(boolean desc) {
        orderBy(BookmarkColumns.CHAPTER_TITLE, desc);
        return this;
    }

    public BookmarkSelection orderByChapterTitle() {
        orderBy(BookmarkColumns.CHAPTER_TITLE, false);
        return this;
    }

    public BookmarkSelection chapterOffset(int... value) {
        addEquals(BookmarkColumns.CHAPTER_OFFSET, toObjectArray(value));
        return this;
    }

    public BookmarkSelection chapterOffsetNot(int... value) {
        addNotEquals(BookmarkColumns.CHAPTER_OFFSET, toObjectArray(value));
        return this;
    }

    public BookmarkSelection chapterOffsetGt(int value) {
        addGreaterThan(BookmarkColumns.CHAPTER_OFFSET, value);
        return this;
    }

    public BookmarkSelection chapterOffsetGtEq(int value) {
        addGreaterThanOrEquals(BookmarkColumns.CHAPTER_OFFSET, value);
        return this;
    }

    public BookmarkSelection chapterOffsetLt(int value) {
        addLessThan(BookmarkColumns.CHAPTER_OFFSET, value);
        return this;
    }

    public BookmarkSelection chapterOffsetLtEq(int value) {
        addLessThanOrEquals(BookmarkColumns.CHAPTER_OFFSET, value);
        return this;
    }

    public BookmarkSelection orderByChapterOffset(boolean desc) {
        orderBy(BookmarkColumns.CHAPTER_OFFSET, desc);
        return this;
    }

    public BookmarkSelection orderByChapterOffset() {
        orderBy(BookmarkColumns.CHAPTER_OFFSET, false);
        return this;
    }

    public BookmarkSelection markType(int... value) {
        addEquals(BookmarkColumns.MARK_TYPE, toObjectArray(value));
        return this;
    }

    public BookmarkSelection markTypeNot(int... value) {
        addNotEquals(BookmarkColumns.MARK_TYPE, toObjectArray(value));
        return this;
    }

    public BookmarkSelection markTypeGt(int value) {
        addGreaterThan(BookmarkColumns.MARK_TYPE, value);
        return this;
    }

    public BookmarkSelection markTypeGtEq(int value) {
        addGreaterThanOrEquals(BookmarkColumns.MARK_TYPE, value);
        return this;
    }

    public BookmarkSelection markTypeLt(int value) {
        addLessThan(BookmarkColumns.MARK_TYPE, value);
        return this;
    }

    public BookmarkSelection markTypeLtEq(int value) {
        addLessThanOrEquals(BookmarkColumns.MARK_TYPE, value);
        return this;
    }

    public BookmarkSelection orderByMarkType(boolean desc) {
        orderBy(BookmarkColumns.MARK_TYPE, desc);
        return this;
    }

    public BookmarkSelection orderByMarkType() {
        orderBy(BookmarkColumns.MARK_TYPE, false);
        return this;
    }

    public BookmarkSelection createTime(long... value) {
        addEquals(BookmarkColumns.CREATE_TIME, toObjectArray(value));
        return this;
    }

    public BookmarkSelection createTimeNot(long... value) {
        addNotEquals(BookmarkColumns.CREATE_TIME, toObjectArray(value));
        return this;
    }

    public BookmarkSelection createTimeGt(long value) {
        addGreaterThan(BookmarkColumns.CREATE_TIME, value);
        return this;
    }

    public BookmarkSelection createTimeGtEq(long value) {
        addGreaterThanOrEquals(BookmarkColumns.CREATE_TIME, value);
        return this;
    }

    public BookmarkSelection createTimeLt(long value) {
        addLessThan(BookmarkColumns.CREATE_TIME, value);
        return this;
    }

    public BookmarkSelection createTimeLtEq(long value) {
        addLessThanOrEquals(BookmarkColumns.CREATE_TIME, value);
        return this;
    }

    public BookmarkSelection orderByCreateTime(boolean desc) {
        orderBy(BookmarkColumns.CREATE_TIME, desc);
        return this;
    }

    public BookmarkSelection orderByCreateTime() {
        orderBy(BookmarkColumns.CREATE_TIME, false);
        return this;
    }
}
