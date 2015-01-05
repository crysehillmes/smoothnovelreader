package org.cryse.novelreader.logic;

import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;

import java.util.List;

import rx.Observable;

/**
 * The interface Novel data service.
 */
public interface NovelDataService {

    /**
     * Is favorite.
     *
     * @param id the id
     * @return the boolean
     */
    public Observable<Boolean> isFavorite(String id);

    /**
     * Gets favorited.
     *
     * @return the favorited
     */
    public Observable<List<NovelModel>> getFavorited();

    /**
     * Add to favorite.
     *
     * @param novels the novels
     */
    public Observable<Void> addToFavorite(NovelModel novels);

    /**
     * Remove from favorite.
     *
     * @param novelId the novel id
     */
    public Observable<Void> removeFromFavorite(String... novelId);

    /**
     * Gets recommends.
     *
     * @param cid     the cid
     * @param subcate the subcate
     * @param page    the page
     * @return the recommends
     */
    public Observable<List<NovelModel>> getCategories(String cid, String subcate, int page, int status, boolean isByTag);

    /**
     * Gets ranks.
     *
     * @param cid  the cid
     * @param page the page
     * @return the ranks
     */
    public Observable<List<NovelModel>> getRanks(String cid, int page);

    /**
     * Gets chapter list.
     *
     * @param novel the novel
     * @return the chapter list
     */
    public Observable<List<NovelChapterModel>> getChapterList(NovelModel novel, final boolean forceUpdate);

    /**
     * Gets chapter list.
     *
     * @param novel the novel
     * @param chapterModels the novel chapter List
     * @return the Observable, would call onNext for many times
     */
    public Observable<Boolean> preloadChapterContents(NovelModel novel, List<NovelChapterModel> chapterModels);

    /**
     * Gets chapter content.
     *
     * @param novel       the novel
     * @return the chapter content
     */
    public Observable<NovelChapterContentModel> getChapterContent(NovelChapterModel novel, boolean forceUpdate);

    /**
     * Search observable.
     *
     * @param queryString the query string
     * @param page        the page
     * @return the observable
     */
    public Observable<List<NovelModel>> search(String queryString, int page);


    public Observable<List<NovelModel>> getNovelUpdate();

    public Observable<Void> addBookMark(NovelBookMarkModel model);

    public Observable<Void> saveLastReadBookMark(NovelBookMarkModel bookMarkModel);

    public Observable<NovelBookMarkModel> getLastReadBookMark(String novelId);

    public Observable<List<NovelBookMarkModel>> getBookMarks(String novelId);

    public Observable<NovelBookMarkModel> checkLastReadBookMarkState(String novelId);

    public Observable<NovelDetailModel> getNovelDetail(String id, String src);

    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String currentChapterSrc, String chapterTitle);

    public Observable<NovelChapterModel> changeChapterSrc(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel);
}
