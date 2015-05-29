package org.cryse.novelreader.logic.impl;

import android.text.TextUtils;
import android.util.Log;

import org.cryse.chaptersplitter.LocalTextReader;
import org.cryse.chaptersplitter.TextChapter;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.HashUtils;
import org.cryse.novelreader.util.NovelTextFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
                    result[1] = novelModel.getSrc().startsWith(LOCAL_FILE_PREFIX + ":");
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
    public Observable<List<NovelChapterModel>> getChapterList(final NovelModel novel, final boolean forceUpdate) {
        return Observable.create((Subscriber<? super List<NovelChapterModel>> subscriber) -> {
            try {
                List<NovelChapterModel> chapterList;
                if (novelDataBase.isFavorite(novel.getId())) {
                    if (forceUpdate || novel.getLatestUpdateCount() != 0) {
                        chapterList = novelSource.getChapterListSync(novel.getId(), novel.getSrc());
                        novelDataBase.updateChapters(novel.getId(), chapterList);
                        chapterList = novelDataBase.loadChapters(novel.getId());
                    } else {
                        chapterList = novelDataBase.loadChapters(novel.getId());
                        if (chapterList.size() == 0) {
                            chapterList = novelSource.getChapterListSync(novel.getId(), novel.getSrc());
                            novelDataBase.updateChapters(novel.getId(), chapterList);
                            chapterList = novelDataBase.loadChapters(novel.getId());
                        }
                    }
                    subscriber.onNext(chapterList);
                    subscriber.onCompleted();
                } else {
                    chapterList = novelSource.getChapterListSync(novel.getId(), novel.getSrc());
                    subscriber.onNext(chapterList);
                    subscriber.onCompleted();
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> preloadChapterContents(NovelModel novel, List<NovelChapterModel> chapterModels) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            try {
                if (novelDataBase.isFavorite(novel.getId())) {
                    for (NovelChapterModel chapterModel : chapterModels) {
                        NovelChapterContentModel chapterContentModel = novelSource.getChapterContentSync(novel.getId(), chapterModel.getSecondId(), chapterModel.getSrc());
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
    public Observable<NovelChapterContentModel> getChapterContent(final NovelChapterModel novelChapter, boolean forceUpdate) {
        return Observable.create((Subscriber<? super NovelChapterContentModel> subscriber) -> {
            try {
                NovelChapterContentModel chapterContent;
                Boolean isFavorite = novelDataBase.isFavorite(novelChapter.getId());
                if (isFavorite && !forceUpdate) {
                    chapterContent = novelDataBase.loadChapterContent(novelChapter.getSecondId());
                    if (chapterContent == null) {
                        chapterContent = novelSource.getChapterContentSync(novelChapter.getId(), novelChapter.getSecondId(), novelChapter.getSrc());
                        chapterContent.setContent(novelTextFilter.filter(chapterContent.getContent()));
                        novelDataBase.updateChapterContent(chapterContent);
                    }
                    subscriber.onNext(chapterContent);
                    subscriber.onCompleted();
                } else {
                    chapterContent = novelSource.getChapterContentSync(novelChapter.getId(), novelChapter.getSecondId(), novelChapter.getSrc());
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
                List<String> novelIds = new ArrayList<String>(novelModels.size());
                Hashtable<String, NovelModel> hashtable = new Hashtable<String, NovelModel>();

                for (int i = 0; i < novelModels.size(); i++) {
                    NovelModel novelModel = novelModels.get(i);
                    if(novelModel.getSrc().startsWith(LOCAL_FILE_PREFIX + ":"))
                        continue;
                    String id = novelModel.getId();
                    novelIds.add(id);
                    hashtable.put(id, novelModel);
                }
                if(novelIds.size() == 0) {
                    subscriber.onNext(novelModels);
                    subscriber.onCompleted();
                }
                List<NovelSyncBookShelfModel> syncShelfItems = novelSource.getNovelUpdatesSync(novelIds.toArray(new String[novelIds.size()]));

                for (NovelSyncBookShelfModel syncBookShelfModel : syncShelfItems) {
                    String gid = syncBookShelfModel.getId();
                    NovelModel novelModel = hashtable.get(gid);
                    if (novelModel.getLatestUpdateCount() == 0 && novelModel.getLatestChapterTitle().compareTo(syncBookShelfModel.getLastChapterTitle()) != 0)
                        novelModel.setLatestUpdateCount(1);
                    novelModel.setLatestChapterTitle(syncBookShelfModel.getLastChapterTitle());
                }
                novelDataBase.updateFavoritesStatus(hashtable.values());

                subscriber.onNext(novelDataBase.loadAllFavorites());
                hashtable.clear();
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<Void> addBookMark(final NovelBookMarkModel model) {
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
    public Observable<Void> saveLastReadBookMark(final NovelBookMarkModel bookMarkModel) {
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
    public Observable<NovelBookMarkModel> getLastReadBookMark(final String novelId) {
        return Observable.create((Subscriber<? super NovelBookMarkModel> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.loadLastReadBookMark(novelId));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<NovelBookMarkModel>> getBookMarks(final String novelId) {
        return Observable.create((Subscriber<? super List<NovelBookMarkModel>> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.loadBookMarks(novelId));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<NovelBookMarkModel> checkLastReadBookMarkState(final String novelId) {
        return Observable.create((Subscriber<? super NovelBookMarkModel> subscriber) -> {
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
    public Observable<NovelChapterModel> changeChapterSrc(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel) {
        return Observable.create((Subscriber<? super NovelChapterModel> subscriber) -> {
            try {
                subscriber.onNext(novelDataBase.changeChapterSource(chapterModel, changeSrcModel));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }
}
