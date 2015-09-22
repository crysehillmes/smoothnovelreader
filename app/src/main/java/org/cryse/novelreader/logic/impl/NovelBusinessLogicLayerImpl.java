package org.cryse.novelreader.logic.impl;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.model.UpdateRequestInfo;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.ChapterTitleUtils;
import org.cryse.novelreader.util.NovelTextFilter;
import org.cryse.novelreader.util.comparator.NovelSortKeyComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.FuncN;

public class NovelBusinessLogicLayerImpl implements NovelBusinessLogicLayer {
    public static final String LOCAL_FILE_PREFIX = DataContract.LOCAL_FILE_PREFIX;
    NovelSourceManager mNovelSourceManager;

    NovelDatabaseAccessLayer novelDataBase;

    NovelTextFilter novelTextFilter;

    @Inject
    public NovelBusinessLogicLayerImpl(
            NovelSourceManager novelSourceManager,
            NovelDatabaseAccessLayer novelDataBase,
            NovelTextFilter novelTextFilter
    ) {
        this.mNovelSourceManager = novelSourceManager;
        this.novelDataBase = novelDataBase;
        this.novelTextFilter = novelTextFilter;
    }

    @Override
    public Observable<Boolean> isFavorite(final String id) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            try{
                subscriber.onNext(novelDataBase.isFavorite(id));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Boolean[]> isFavoriteLocal(String id) {
        return Observable.create((Subscriber<? super Boolean[]> subscriber) -> {
            try{
                Boolean[] result = new Boolean[2];
                NovelModel novelModel = novelDataBase.loadFavorite(id);
                if(novelModel == null) {
                    result[0] = false;
                    result[1] = false;
                } else {
                    result[0] = true;
                    result[1] = novelModel.getSource().startsWith(LOCAL_FILE_PREFIX + ":");
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> getFavorites() {
        return Observable.create((Subscriber<? super List<NovelModel>> subscriber) -> {
            try{
                subscriber.onNext(novelDataBase.loadAllFavorites());
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Void> addToFavorite(final NovelModel novel) {
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            try {
                novelDataBase.addToFavorite(novel);
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Void> removeFromFavorite(final String... novelIds) {
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            try {
                novelDataBase.removeFavorite(novelIds);
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> getCategories(String cid, String subcate, int page, int status, boolean isByTag) {
        return mNovelSourceManager.getDefault().getCategories(cid, subcate, page, status, isByTag);
    }

    @Override
    public Observable<List<NovelModel>> getRanks(String cid, int page) {
        return mNovelSourceManager.getDefault().getRanks(cid, page);
    }

    @Override
    public Observable<List<ChapterModel>> getChapterList(final NovelModel novel, final boolean forceUpdate, final boolean hideRedundantTitle) {
        NovelSource novelSource = mNovelSourceManager.getNovelSource(novel.getType());
        return Observable.create((Subscriber<? super List<ChapterModel>> subscriber) -> {
            try {
                List<ChapterModel> chapterList;
                if (novelDataBase.isFavorite(novel.getNovelId())) {
                    if (forceUpdate || novel.getLatestUpdateChapterCount() != 0) {
                        chapterList = novelSource.getChapterListSync(novel.getNovelId(), novel.getSource());
                        novelDataBase.updateChapters(novel.getNovelId(), chapterList);
                        chapterList = novelDataBase.loadChapters(novel.getNovelId());
                    } else {
                        chapterList = novelDataBase.loadChapters(novel.getNovelId());
                        if (chapterList.size() == 0) {
                            chapterList = novelSource.getChapterListSync(novel.getNovelId(), novel.getSource());
                            novelDataBase.updateChapters(novel.getNovelId(), chapterList);
                            chapterList = novelDataBase.loadChapters(novel.getNovelId());
                        }
                    }
                    ChapterTitleUtils.shrinkTitles(chapterList);
                    subscriber.onNext(chapterList);
                    subscriber.onCompleted();
                } else {
                    chapterList = novelSource.getChapterListSync(novel.getNovelId(), novel.getSource());
                    ChapterTitleUtils.shrinkTitles(chapterList);
                    subscriber.onNext(chapterList);
                    subscriber.onCompleted();
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> preloadChapterContents(NovelModel novel, List<ChapterModel> chapterModels) {
        NovelSource novelSource = mNovelSourceManager.getNovelSource(novel.getType());
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            try {
                if (novelDataBase.isFavorite(novel.getNovelId())) {
                    for (ChapterModel chapterModel : chapterModels) {
                        ChapterContentModel chapterContentModel = novelSource.getChapterContentSync(novel.getNovelId(), chapterModel.getChapterId(), chapterModel.getTitle(), chapterModel.getSource());
                        if (chapterContentModel != null) {
                            novelDataBase.updateChapterContent(chapterContentModel);
                            subscriber.onNext(true);
                        } else {
                            subscriber.onNext(false);
                        }
                    }
                    subscriber.onCompleted();
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<ChapterContentModel> getChapterContent(NovelModel novelModel, final ChapterModel novelChapter, boolean forceUpdate) {
        NovelSource novelSource = mNovelSourceManager.getNovelSource(novelModel.getType());
        return Observable.create((Subscriber<? super ChapterContentModel> subscriber) -> {
            try {
                ChapterContentModel chapterContent;
                Boolean isFavorite = novelDataBase.isFavorite(novelChapter.getNovelId());
                if (isFavorite && !forceUpdate) {
                    chapterContent = novelDataBase.loadChapterContent(novelChapter.getChapterId());
                    if (chapterContent == null) {
                        chapterContent = novelSource.getChapterContentSync(novelChapter.getNovelId(), novelChapter.getChapterId(), novelChapter.getTitle(), novelChapter.getSource());
                        chapterContent.setContent(novelTextFilter.filter(chapterContent.getContent()));
                        novelDataBase.updateChapterContent(chapterContent);
                    }
                    subscriber.onNext(chapterContent);
                    subscriber.onCompleted();
                } else {
                    chapterContent = novelSource.getChapterContentSync(novelChapter.getNovelId(), novelChapter.getChapterId(), novelChapter.getTitle(), novelChapter.getSource());
                    chapterContent.setContent(novelTextFilter.filter(chapterContent.getContent()));
                    // if(isFavorited) updateChapterContentInDB(novelChapter.getSecondId(), novelChapter.getSrc(), chapterContent);
                    subscriber.onNext(chapterContent);
                    subscriber.onCompleted();
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> search(String queryString, int page) {
        Observable<List<NovelModel>> result = Observable.empty();
        List<Observable<List<NovelModel>>> observables = new ArrayList<>();
        for (int i = 0; i < mNovelSourceManager.getNovelSourceCount(); i++) {
            NovelSource novelSource = mNovelSourceManager.getNovelSourceAt(i);
            Observable<List<NovelModel>> searchObservable = novelSource.search(queryString, page);
            observables.add(searchObservable);
        }
        return Observable.zip(observables, new FuncN<List<NovelModel>>() {
            @Override
            public List<NovelModel> call(Object... args) {
                List<NovelModel> result = new ArrayList<NovelModel>();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof List) {
                        result.addAll((List<? extends NovelModel>) args[i]);
                    }
                }
                return result;
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> getNovelUpdate() {
        return Observable.create((Subscriber<? super List<NovelModel>> subscriber) -> {
            try {
                List<NovelModel> novelModels = novelDataBase.loadAllFavorites();
                List<UpdateRequestInfo> novelIds = new ArrayList<UpdateRequestInfo>(novelModels.size());

                for (int i = 0; i < novelModels.size(); i++) {
                    NovelModel novelModel = novelModels.get(i);
                    if (novelModel.getSource().startsWith(LOCAL_FILE_PREFIX + ":"))
                        continue;
                    String id = novelModel.getNovelId();
                    novelIds.add(
                            new UpdateRequestInfo(
                                    novelModel.getType(),
                                    id,
                                    TextUtils.isEmpty(novelModel.getLatestChapterId()) ? id + "_1" : novelModel.getLatestChapterId()
                            )
                    );
                }
                if (novelIds.size() == 0) {
                    Collections.sort(novelModels, new NovelSortKeyComparator());
                    subscriber.onNext(novelModels);
                    subscriber.onCompleted();
                }
                ArrayMap<Integer, List<UpdateRequestInfo>> updateRequestsMap = new ArrayMap<Integer, List<UpdateRequestInfo>>(mNovelSourceManager.getNovelSourceCount());

                UpdateRequestInfo[] updateRequests = new UpdateRequestInfo[novelIds.size()];
                for (int i = 0; i < novelIds.size(); i++) {
                    UpdateRequestInfo requestInfo = novelIds.get(i);
                    updateRequests[i] = requestInfo;
                    if (!updateRequestsMap.containsKey(requestInfo.getNovelType())) {
                        updateRequestsMap.put(requestInfo.getNovelType(), new ArrayList<UpdateRequestInfo>());
                    }
                    List<UpdateRequestInfo> requests = updateRequestsMap.get(requestInfo.getNovelType());
                    requests.add(requestInfo);
                }


                List<NovelSyncBookShelfModel> syncShelfItems = new ArrayList<NovelSyncBookShelfModel>();
                for (int i = 0; i < updateRequestsMap.size(); i++) {
                    NovelSource novelSource = mNovelSourceManager.getNovelSource(updateRequestsMap.keyAt(i));
                    List<NovelSyncBookShelfModel> items = novelSource.getNovelUpdatesSync(updateRequestsMap.valueAt(i));
                    syncShelfItems.addAll(items);
                }

                List<NovelModel> updatedNovels = new ArrayList<NovelModel>(syncShelfItems.size());
                for (NovelSyncBookShelfModel syncBookShelfModel : syncShelfItems) {
                    String novelId = syncBookShelfModel.getId();
                    NovelModel novelModel = novelDataBase.loadFavorite(novelId);
                    if (
                            novelModel != null &&
                            novelModel.getLatestUpdateChapterCount() == 0 &&
                                    novelModel.getLatestChapterTitle() != null &&
                                    novelModel.getLatestChapterTitle().compareTo(syncBookShelfModel.getLastChapterTitle()) != 0
                            ) {
                        novelModel.setLatestUpdateChapterCount(1);
                        novelModel.setLatestChapterId(syncBookShelfModel.getLastChapterId());
                        novelModel.setLatestChapterTitle(syncBookShelfModel.getLastChapterTitle());
                        updatedNovels.add(novelModel);
                    }
                }
                novelDataBase.updateFavoritesStatus(updatedNovels);
                List<NovelModel> novelModel = novelDataBase.loadAllFavorites();
                Collections.sort(novelModel, new NovelSortKeyComparator());
                subscriber.onNext(novelModel);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Void> addBookMark(final BookmarkModel model) {
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            try {
                novelDataBase.addBookMark(model);
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Void> saveLastReadBookMark(final BookmarkModel bookMarkModel) {
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            try {
                novelDataBase.insertOrUpdateLastReadBookMark(bookMarkModel);
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<BookmarkModel> getLastReadBookMark(final String novelId) {
        return Observable.create((Subscriber<? super BookmarkModel> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.loadLastReadBookMark(novelId));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<BookmarkModel>> getBookMarks(final String novelId) {
        return Observable.create((Subscriber<? super List<BookmarkModel>> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.loadBookMarks(novelId));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<BookmarkModel> checkLastReadBookMarkState(final String novelId) {
        return Observable.create((Subscriber<? super BookmarkModel> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.checkLastReadBookMarkState(novelId));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<NovelDetailModel> getNovelDetail(NovelModel novel, String src) {
        NovelSource novelSource = mNovelSourceManager.getNovelSource(novel.getType());
        return novelSource.getNovelDetail(novel.getNovelId(), src);
    }

    @Override
    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(NovelModel novel, String currentChapterSrc, String chapterTitle) {
        NovelSource novelSource = mNovelSourceManager.getNovelSource(novel.getType());
        return novelSource.getOtherChapterSrc(novel.getNovelId(), currentChapterSrc, chapterTitle);
    }

    @Override
    public Observable<ChapterModel> changeChapterSrc(ChapterModel chapterModel, NovelChangeSrcModel changeSrcModel) {
        return Observable.create((Subscriber<? super ChapterModel> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.changeChapterSource(chapterModel, changeSrcModel));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }
}
