package org.cryse.novelreader.data.provider.novel;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.novelreader.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code novel} table.
 */
public class NovelSelection extends AbstractSelection<NovelSelection> {
    @Override
    protected Uri baseUri() {
        return NovelColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code NovelCursor} object, which is positioned before the first entry, or null.
     */
    public NovelCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new NovelCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public NovelCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code NovelCursor} object, which is positioned before the first entry, or null.
     */
    public NovelCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new NovelCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public NovelCursor query(Context context) {
        return query(context, null);
    }


    public NovelSelection id(long... value) {
        addEquals("novel." + NovelColumns._ID, toObjectArray(value));
        return this;
    }

    public NovelSelection idNot(long... value) {
        addNotEquals("novel." + NovelColumns._ID, toObjectArray(value));
        return this;
    }

    public NovelSelection orderById(boolean desc) {
        orderBy("novel." + NovelColumns._ID, desc);
        return this;
    }

    public NovelSelection orderById() {
        return orderById(false);
    }

    public NovelSelection novelId(String... value) {
        addEquals(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection novelIdNot(String... value) {
        addNotEquals(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection novelIdLike(String... value) {
        addLike(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection novelIdContains(String... value) {
        addContains(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection novelIdStartsWith(String... value) {
        addStartsWith(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection novelIdEndsWith(String... value) {
        addEndsWith(NovelColumns.NOVEL_ID, value);
        return this;
    }

    public NovelSelection orderByNovelId(boolean desc) {
        orderBy(NovelColumns.NOVEL_ID, desc);
        return this;
    }

    public NovelSelection orderByNovelId() {
        orderBy(NovelColumns.NOVEL_ID, false);
        return this;
    }

    public NovelSelection title(String... value) {
        addEquals(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection titleNot(String... value) {
        addNotEquals(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection titleLike(String... value) {
        addLike(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection titleContains(String... value) {
        addContains(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection titleStartsWith(String... value) {
        addStartsWith(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection titleEndsWith(String... value) {
        addEndsWith(NovelColumns.TITLE, value);
        return this;
    }

    public NovelSelection orderByTitle(boolean desc) {
        orderBy(NovelColumns.TITLE, desc);
        return this;
    }

    public NovelSelection orderByTitle() {
        orderBy(NovelColumns.TITLE, false);
        return this;
    }

    public NovelSelection author(String... value) {
        addEquals(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection authorNot(String... value) {
        addNotEquals(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection authorLike(String... value) {
        addLike(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection authorContains(String... value) {
        addContains(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection authorStartsWith(String... value) {
        addStartsWith(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection authorEndsWith(String... value) {
        addEndsWith(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelSelection orderByAuthor(boolean desc) {
        orderBy(NovelColumns.AUTHOR, desc);
        return this;
    }

    public NovelSelection orderByAuthor() {
        orderBy(NovelColumns.AUTHOR, false);
        return this;
    }

    public NovelSelection type(int... value) {
        addEquals(NovelColumns.TYPE, toObjectArray(value));
        return this;
    }

    public NovelSelection typeNot(int... value) {
        addNotEquals(NovelColumns.TYPE, toObjectArray(value));
        return this;
    }

    public NovelSelection typeGt(int value) {
        addGreaterThan(NovelColumns.TYPE, value);
        return this;
    }

    public NovelSelection typeGtEq(int value) {
        addGreaterThanOrEquals(NovelColumns.TYPE, value);
        return this;
    }

    public NovelSelection typeLt(int value) {
        addLessThan(NovelColumns.TYPE, value);
        return this;
    }

    public NovelSelection typeLtEq(int value) {
        addLessThanOrEquals(NovelColumns.TYPE, value);
        return this;
    }

    public NovelSelection orderByType(boolean desc) {
        orderBy(NovelColumns.TYPE, desc);
        return this;
    }

    public NovelSelection orderByType() {
        orderBy(NovelColumns.TYPE, false);
        return this;
    }

    public NovelSelection source(String... value) {
        addEquals(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection sourceNot(String... value) {
        addNotEquals(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection sourceLike(String... value) {
        addLike(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection sourceContains(String... value) {
        addContains(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection sourceStartsWith(String... value) {
        addStartsWith(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection sourceEndsWith(String... value) {
        addEndsWith(NovelColumns.SOURCE, value);
        return this;
    }

    public NovelSelection orderBySource(boolean desc) {
        orderBy(NovelColumns.SOURCE, desc);
        return this;
    }

    public NovelSelection orderBySource() {
        orderBy(NovelColumns.SOURCE, false);
        return this;
    }

    public NovelSelection coverImage(String... value) {
        addEquals(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection coverImageNot(String... value) {
        addNotEquals(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection coverImageLike(String... value) {
        addLike(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection coverImageContains(String... value) {
        addContains(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection coverImageStartsWith(String... value) {
        addStartsWith(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection coverImageEndsWith(String... value) {
        addEndsWith(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelSelection orderByCoverImage(boolean desc) {
        orderBy(NovelColumns.COVER_IMAGE, desc);
        return this;
    }

    public NovelSelection orderByCoverImage() {
        orderBy(NovelColumns.COVER_IMAGE, false);
        return this;
    }

    public NovelSelection chapterCount(Integer... value) {
        addEquals(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection chapterCountNot(Integer... value) {
        addNotEquals(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection chapterCountGt(int value) {
        addGreaterThan(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection chapterCountGtEq(int value) {
        addGreaterThanOrEquals(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection chapterCountLt(int value) {
        addLessThan(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection chapterCountLtEq(int value) {
        addLessThanOrEquals(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection orderByChapterCount(boolean desc) {
        orderBy(NovelColumns.CHAPTER_COUNT, desc);
        return this;
    }

    public NovelSelection orderByChapterCount() {
        orderBy(NovelColumns.CHAPTER_COUNT, false);
        return this;
    }

    public NovelSelection lastReadChapterTitle(String... value) {
        addEquals(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection lastReadChapterTitleNot(String... value) {
        addNotEquals(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection lastReadChapterTitleLike(String... value) {
        addLike(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection lastReadChapterTitleContains(String... value) {
        addContains(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection lastReadChapterTitleStartsWith(String... value) {
        addStartsWith(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection lastReadChapterTitleEndsWith(String... value) {
        addEndsWith(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection orderByLastReadChapterTitle(boolean desc) {
        orderBy(NovelColumns.LAST_READ_CHAPTER_TITLE, desc);
        return this;
    }

    public NovelSelection orderByLastReadChapterTitle() {
        orderBy(NovelColumns.LAST_READ_CHAPTER_TITLE, false);
        return this;
    }

    public NovelSelection latestChapterTitle(String... value) {
        addEquals(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection latestChapterTitleNot(String... value) {
        addNotEquals(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection latestChapterTitleLike(String... value) {
        addLike(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection latestChapterTitleContains(String... value) {
        addContains(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection latestChapterTitleStartsWith(String... value) {
        addStartsWith(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection latestChapterTitleEndsWith(String... value) {
        addEndsWith(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelSelection orderByLatestChapterTitle(boolean desc) {
        orderBy(NovelColumns.LATEST_CHAPTER_TITLE, desc);
        return this;
    }

    public NovelSelection orderByLatestChapterTitle() {
        orderBy(NovelColumns.LATEST_CHAPTER_TITLE, false);
        return this;
    }

    public NovelSelection latestUpdateChapterCount(Integer... value) {
        addEquals(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection latestUpdateChapterCountNot(Integer... value) {
        addNotEquals(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection latestUpdateChapterCountGt(int value) {
        addGreaterThan(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection latestUpdateChapterCountGtEq(int value) {
        addGreaterThanOrEquals(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection latestUpdateChapterCountLt(int value) {
        addLessThan(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection latestUpdateChapterCountLtEq(int value) {
        addLessThanOrEquals(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }

    public NovelSelection orderByLatestUpdateChapterCount(boolean desc) {
        orderBy(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, desc);
        return this;
    }

    public NovelSelection orderByLatestUpdateChapterCount() {
        orderBy(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, false);
        return this;
    }

    public NovelSelection sortKey(Long... value) {
        addEquals(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection sortKeyNot(Long... value) {
        addNotEquals(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection sortKeyGt(long value) {
        addGreaterThan(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection sortKeyGtEq(long value) {
        addGreaterThanOrEquals(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection sortKeyLt(long value) {
        addLessThan(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection sortKeyLtEq(long value) {
        addLessThanOrEquals(NovelColumns.SORT_KEY, value);
        return this;
    }

    public NovelSelection orderBySortKey(boolean desc) {
        orderBy(NovelColumns.SORT_KEY, desc);
        return this;
    }

    public NovelSelection orderBySortKey() {
        orderBy(NovelColumns.SORT_KEY, false);
        return this;
    }
}
