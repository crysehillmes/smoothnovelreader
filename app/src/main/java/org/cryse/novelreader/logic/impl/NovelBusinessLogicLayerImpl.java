package org.cryse.novelreader.logic.impl;

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
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

public class NovelBusinessLogicLayerImpl implements NovelBusinessLogicLayer {
    public static final String LOCAL_FILE_PREFIX = DataContract.LOCAL_FILE_PREFIX;
    NovelSource novelSource;

    NovelDatabaseAccessLayer novelDataBase;

    NovelTextFilter novelTextFilter;

    @Inject
    public NovelBusinessLogicLayerImpl(
            NovelSource novelSource,
            NovelDatabaseAccessLayer novelDataBase,
            NovelTextFilter novelTextFilter
    ) {
        this.novelSource = novelSource;
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
        return novelSource.getCategories(cid, subcate, page, status, isByTag);
    }

    @Override
    public Observable<List<NovelModel>> getRanks(String cid, int page) {
        return novelSource.getRanks(cid, page);
    }

    @Override
    public Observable<List<ChapterModel>> getChapterList(final NovelModel novel, final boolean forceUpdate, final boolean hideRedundantTitle) {
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
    public Observable<ChapterContentModel> getChapterContent(final ChapterModel novelChapter, boolean forceUpdate) {
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
        return novelSource.search(queryString, page);
    }

    @Override
    public Observable<List<NovelModel>> getNovelUpdate() {
        return Observable.create((Subscriber<? super List<NovelModel>> subscriber) -> {
            try {
                List<NovelModel> novelModels = novelDataBase.loadAllFavorites();
                List<UpdateRequestInfo> novelIds = new ArrayList<UpdateRequestInfo>(novelModels.size());
                Hashtable<String, NovelModel> hashtable = new Hashtable<String, NovelModel>();

                for (int i = 0; i < novelModels.size(); i++) {
                    NovelModel novelModel = novelModels.get(i);
                    if(novelModel.getSource().startsWith(LOCAL_FILE_PREFIX + ":"))
                        continue;
                    String id = novelModel.getNovelId();
                    novelIds.add(
                            new UpdateRequestInfo(
                                    id,
                                    TextUtils.isEmpty(novelModel.getLatestChapterId()) ? id + "_1" : novelModel.getLatestChapterId()
                            )
                    );
                    hashtable.put(id, novelModel);
                }
                if(novelIds.size() == 0) {
                    Collections.sort(novelModels, new NovelSortKeyComparator());
                    subscriber.onNext(novelModels);
                    subscriber.onCompleted();
                }
                UpdateRequestInfo[] updateRequests = new UpdateRequestInfo[novelIds.size()];
                for (int i = 0; i < novelIds.size(); i++) {
                    UpdateRequestInfo requestInfo = novelIds.get(i);
                    updateRequests[i] = requestInfo;
                }
                List<NovelSyncBookShelfModel> syncShelfItems = novelSource.getNovelUpdatesSync(updateRequests);

                for (NovelSyncBookShelfModel syncBookShelfModel : syncShelfItems) {
                    String gid = syncBookShelfModel.getId();
                    NovelModel novelModel = hashtable.get(gid);
                    if (
                            novelModel.getLatestUpdateChapterCount() == 0 &&
                                    novelModel.getLatestChapterTitle() != null &&
                                    novelModel.getLatestChapterTitle().compareTo(syncBookShelfModel.getLastChapterTitle()) != 0
                            )
                        novelModel.setLatestUpdateChapterCount(1);
                    novelModel.setLatestChapterId(syncBookShelfModel.getLastChapterId());
                    novelModel.setLatestChapterTitle(syncBookShelfModel.getLastChapterTitle());
                }
                novelDataBase.updateFavoritesStatus(hashtable.values());
                List<NovelModel> novelModel = novelDataBase.loadAllFavorites();
                Collections.sort(novelModel, new NovelSortKeyComparator());
                subscriber.onNext(novelModel);
                hashtable.clear();
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
    public Observable<NovelDetailModel> getNovelDetail(String id, String src) {
        return novelSource.getNovelDetail(id, src);
    }

    @Override
    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String currentChapterSrc, String chapterTitle) {
        return novelSource.getOtherChapterSrc(novelId, currentChapterSrc, chapterTitle);
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
