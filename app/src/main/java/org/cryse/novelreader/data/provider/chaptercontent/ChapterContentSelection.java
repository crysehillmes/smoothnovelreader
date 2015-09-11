package org.cryse.novelreader.data.provider.chaptercontent;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.novelreader.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code chapter_content} table.
 */
public class ChapterContentSelection extends AbstractSelection<ChapterContentSelection> {
    @Override
    protected Uri baseUri() {
        return ChapterContentColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ChapterContentCursor} object, which is positioned before the first entry, or null.
     */
    public ChapterContentCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ChapterContentCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public ChapterContentCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ChapterContentCursor} object, which is positioned before the first entry, or null.
     */
    public ChapterContentCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ChapterContentCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public ChapterContentCursor query(Context context) {
        return query(context, null);
    }


    public ChapterContentSelection id(long... value) {
        addEquals("chapter_content." + ChapterContentColumns._ID, toObjectArray(value));
        return this;
    }

    public ChapterContentSelection idNot(long... value) {
        addNotEquals("chapter_content." + ChapterContentColumns._ID, toObjectArray(value));
        return this;
    }

    public ChapterContentSelection orderById(boolean desc) {
        orderBy("chapter_content." + ChapterContentColumns._ID, desc);
        return this;
    }

    public ChapterContentSelection orderById() {
        return orderById(false);
    }

    public ChapterContentSelection novelId(String... value) {
        addEquals(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection novelIdNot(String... value) {
        addNotEquals(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection novelIdLike(String... value) {
        addLike(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection novelIdContains(String... value) {
        addContains(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection novelIdStartsWith(String... value) {
        addStartsWith(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection novelIdEndsWith(String... value) {
        addEndsWith(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }

    public ChapterContentSelection orderByNovelId(boolean desc) {
        orderBy(ChapterContentColumns.NOVEL_ID, desc);
        return this;
    }

    public ChapterContentSelection orderByNovelId() {
        orderBy(ChapterContentColumns.NOVEL_ID, false);
        return this;
    }

    public ChapterContentSelection chapterId(String... value) {
        addEquals(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection chapterIdNot(String... value) {
        addNotEquals(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection chapterIdLike(String... value) {
        addLike(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection chapterIdContains(String... value) {
        addContains(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection chapterIdStartsWith(String... value) {
        addStartsWith(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection chapterIdEndsWith(String... value) {
        addEndsWith(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }

    public ChapterContentSelection orderByChapterId(boolean desc) {
        orderBy(ChapterContentColumns.CHAPTER_ID, desc);
        return this;
    }

    public ChapterContentSelection orderByChapterId() {
        orderBy(ChapterContentColumns.CHAPTER_ID, false);
        return this;
    }

    public ChapterContentSelection source(String... value) {
        addEquals(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection sourceNot(String... value) {
        addNotEquals(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection sourceLike(String... value) {
        addLike(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection sourceContains(String... value) {
        addContains(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection sourceStartsWith(String... value) {
        addStartsWith(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection sourceEndsWith(String... value) {
        addEndsWith(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentSelection orderBySource(boolean desc) {
        orderBy(ChapterContentColumns.SOURCE, desc);
        return this;
    }

    public ChapterContentSelection orderBySource() {
        orderBy(ChapterContentColumns.SOURCE, false);
        return this;
    }

    public ChapterContentSelection content(String... value) {
        addEquals(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection contentNot(String... value) {
        addNotEquals(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection contentLike(String... value) {
        addLike(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection contentContains(String... value) {
        addContains(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection contentStartsWith(String... value) {
        addStartsWith(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection contentEndsWith(String... value) {
        addEndsWith(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentSelection orderByContent(boolean desc) {
        orderBy(ChapterContentColumns.CONTENT, desc);
        return this;
    }

    public ChapterContentSelection orderByContent() {
        orderBy(ChapterContentColumns.CONTENT, false);
        return this;
    }
}
