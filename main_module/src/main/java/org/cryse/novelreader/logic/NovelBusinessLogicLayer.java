package org.cryse.novelreader.logic;

import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;

import java.util.List;

import rx.Observable;

/**
 * The interface Novel data service.
 */
public interface NovelBusinessLogicLayer {

    /**
     * Is favorite.
     *
     * @param id the id
     * @return the boolean
     */
    Observable<Boolean> isFavorite(String id);

    Observable<Boolean[]> isFavoriteLocal(String id);

    /**
     * Get favorites.
     *
     * @return the favorited
     */
    Observable<List<NovelModel>> getFavorites();

    /**
     * Add to favorite.
     *
     * @param novels the novels
     */
    Observable<Void> addToFavorite(NovelModel novels);

    /**
     * Remove from favorite.
     *
     * @param novelId the novel id
     */
    Observable<Void> removeFromFavorite(String... novelId);

    /**
     * Gets recommends.
     *
     * @param cid     the cid
     * @param subcate the subcate
     * @param page    the page
     * @return the recommends
     */
    Observable<List<NovelModel>> getCategories(String cid, String subcate, int page, int status, boolean isByTag);

    /**
     * Gets ranks.
     *
     * @param cid  the cid
     * @param page the page
     * @return the ranks
     */
    Observable<List<NovelModel>> getRanks(String cid, int page);

    /**
     * Gets chapter list.
     *
     * @param novel the novel
     * @return the chapter list
     */
    Observable<List<ChapterModel>> getChapterList(NovelModel novel, final boolean forceUpdate, final boolean hideRedundantTitle);

    /**
     * Gets chapter list.
     *
     * @param novel the novel
     * @param chapterModels the novel chapter List
     * @return the Observable, would call onNext for many times
     */
    Observable<Boolean> preloadChapterContents(NovelModel novel, List<ChapterModel> chapterModels);

    /**
     * Gets chapter content.
     *
     * @param novel       the novel
     * @return the chapter content
     */
    Observable<ChapterContentModel> getChapterContent(ChapterModel novel, boolean forceUpdate);

    /**
     * Search observable.
     *
     * @param queryString the query string
     * @param page        the page
     * @return the observable
     */
    Observable<List<NovelModel>> search(String queryString, int page);


    Observable<List<NovelModel>> getNovelUpdate();

    Observable<Void> addBookMark(BookmarkModel model);

    Observable<Void> saveLastReadBookMark(BookmarkModel bookMarkModel);

    Observable<BookmarkModel> getLastReadBookMark(String novelId);

    Observable<List<BookmarkModel>> getBookMarks(String novelId);

    Observable<BookmarkModel> checkLastReadBookMarkState(String novelId);

    Observable<NovelDetailModel> getNovelDetail(String id, String src);

    Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String currentChapterSrc, String chapterTitle);

    Observable<ChapterModel> changeChapterSrc(ChapterModel chapterModel, NovelChangeSrcModel changeSrcModel);
}
