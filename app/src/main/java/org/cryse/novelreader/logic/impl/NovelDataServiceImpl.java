package org.cryse.novelreader.logic.impl;

import org.cryse.novelreader.data.DaoSession;
import org.cryse.novelreader.data.NovelBookMarkModelDao;
import org.cryse.novelreader.data.NovelChapterContentModelDao;
import org.cryse.novelreader.data.NovelChapterModelDao;
import org.cryse.novelreader.data.NovelModelDao;
import org.cryse.novelreader.logic.NovelDataService;
import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.NovelTextFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;

public class NovelDataServiceImpl implements NovelDataService {

    NovelSource novelSource;

    NovelModelDao novelModelDao;

    NovelChapterModelDao novelChapterModelDao;

    NovelChapterContentModelDao novelChapterContentModelDao;

    NovelBookMarkModelDao novelBookMarkModelDao;

    NovelTextFilter novelTextFilter;

    @Inject
    public NovelDataServiceImpl(
            NovelSource novelSource,
            NovelModelDao novelModelDao,
            NovelChapterModelDao novelChapterModelDao,
            NovelChapterContentModelDao novelChapterContentModelDao,
            NovelBookMarkModelDao novelBookMarkModelDao,
            NovelTextFilter novelTextFilter
    ) {
        this.novelSource = novelSource;
        this.novelModelDao = novelModelDao;
        this.novelChapterModelDao = novelChapterModelDao;
        this.novelChapterContentModelDao = novelChapterContentModelDao;
        this.novelBookMarkModelDao = novelBookMarkModelDao;
        this.novelTextFilter = novelTextFilter;
    }

    private Boolean isFavoriteSync(String id) {
        Boolean isExist;
        QueryBuilder<NovelModel> qb = novelModelDao.queryBuilder().where(NovelModelDao.Properties.Id.eq(id));
        isExist = qb.count() != 0;
        return isExist;
    }

    @Override
    public Observable<Boolean> isFavorite(final String id) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            try{
                subscriber.onNext(isFavoriteSync(id));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> getFavorited() {
        return Observable.create((Subscriber<? super List<NovelModel>> subscriber) -> {
            try{
                clearDaoSession();
                List<NovelModel> favoriteNovels = novelModelDao.loadAll();
                Collections.sort(favoriteNovels);
                subscriber.onNext(favoriteNovels);
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
                if (!isFavoriteSync(novel.getId())) {
                    novel.setSortWeight((new Date()).getTime());
                    novelModelDao.insert(novel);
                }
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
                List novelsList = Arrays.asList(novelIds);
                // 删除NovelModel
                novelModelDao.deleteByKeyInTx(novelIds);

                // 清除章节
                DeleteQuery deleteChaptersQuery = novelChapterModelDao.queryBuilder().where(NovelChapterModelDao.Properties.Id.in(novelsList)).buildDelete();
                deleteChaptersQuery.executeDeleteWithoutDetachingEntities();

                // 清除章节内容
                DeleteQuery deleteChapterContentsQuery = novelChapterContentModelDao.queryBuilder().where(NovelChapterContentModelDao.Properties.Id.in(novelsList)).buildDelete();
                deleteChapterContentsQuery.executeDeleteWithoutDetachingEntities();

                // 清除BookMark
                DeleteQuery deleteBookMarksQuery = novelBookMarkModelDao.queryBuilder().where(NovelBookMarkModelDao.Properties.Id.in(novelsList)).buildDelete();
                deleteBookMarksQuery.executeDeleteWithoutDetachingEntities();

                // 刷新Session
                clearDaoSession();

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
            try{
                List<NovelChapterModel> chapterList;
                if(isFavoriteSync(novel.getId())) {
                    //判断是否在书架之中
                    if(forceUpdate || novel.getLatestUpdateCount() != 0) {
                        chapterList = loadChaptersFromWeb(novel.getId(), novel.getSrc());
                        updateChapterListInDB(novel.getId(), chapterList);
                        chapterList = loadChaptersFromDB(novel.getId());
                    } else {
                        //无需更新章节
                        chapterList = loadChaptersFromDB(novel.getId());
                        if(chapterList.size() == 0) {
                            chapterList = loadChaptersFromWeb(novel.getId(), novel.getSrc());
                            updateChapterListInDB(novel.getId(), chapterList);
                            chapterList = loadChaptersFromDB(novel.getId());
                        }
                    }
                    subscriber.onNext(chapterList);
                    subscriber.onCompleted();
                } else {
                    chapterList = loadChaptersFromWeb(novel.getId(), novel.getSrc());
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
            try{
                if(isFavoriteSync(novel.getId())) {
                    for(NovelChapterModel chapterModel : chapterModels) {
                        NovelChapterContentModel chapterContentModel = loadChapterContentFromWeb(novel.getId(), chapterModel.getSecondId(), chapterModel.getSrc());
                        if(chapterContentModel != null) {
                            updateChapterContentInDB(chapterModel.getSecondId(), chapterModel.getSrc(), chapterContentModel);
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
            try{
                NovelChapterContentModel chapterContent;
                Boolean isFavorited = isFavoriteSync(novelChapter.getId());
                if(isFavorited && forceUpdate == false) {
                        //无需更新章节
                        chapterContent = loadChapterContentFromDB(novelChapter.getSecondId());
                        if(chapterContent == null){
                            chapterContent = loadChapterContentFromWeb(novelChapter.getId(), novelChapter.getSecondId(), novelChapter.getSrc());
                            chapterContent.setContent(novelTextFilter.filter(chapterContent.getContent()));
                            if(isFavorited) updateChapterContentInDB(novelChapter.getSecondId(), novelChapter.getSrc(), chapterContent);
                        }
                        subscriber.onNext(chapterContent);
                        subscriber.onCompleted();
                } else {
                    chapterContent = loadChapterContentFromWeb(novelChapter.getId(), novelChapter.getSecondId(), novelChapter.getSrc());
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
            try{
                List<NovelModel> novelModels = novelModelDao.loadAll();
                String[] novelIds = new String[novelModels.size()];
                Hashtable<String, NovelModel> hashtable = new Hashtable<String, NovelModel>();

                for(int i = 0; i < novelModels.size(); i++) {
                    String id =  novelModels.get(i).getId();
                    novelIds[i] = id;
                    hashtable.put(id, novelModels.get(i));
                }
                List<NovelSyncBookShelfModel> syncShelfItems = novelSource.getNovelUpdatesSync(novelIds);

                for (NovelSyncBookShelfModel syncBookShelfModel : syncShelfItems) {
                    String gid = syncBookShelfModel.getId();
                    NovelModel novelModel = hashtable.get(gid);
                    if (novelModel.getLatestUpdateCount() == 0 && novelModel.getLatestChapterTitle().compareTo(syncBookShelfModel.getLastChapterTitle()) != 0)
                        novelModel.setLatestUpdateCount(1);
                    novelModel.setLatestChapterTitle(syncBookShelfModel.getLastChapterTitle());
                }
                novelModelDao.insertOrReplaceInTx(hashtable.values());
                clearDaoSession();
                List<NovelModel> resultNovels = new ArrayList<NovelModel>(hashtable.values());
                Collections.sort(resultNovels);
                subscriber.onNext(resultNovels);
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
                novelBookMarkModelDao.insert(model);
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
                //删除已有的最后阅读记录，保存新的
                DeleteQuery deleteQuery = novelBookMarkModelDao.queryBuilder().where(
                        NovelBookMarkModelDao.Properties.Id.eq(bookMarkModel.getId()),
                        NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
                ).buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();
                novelBookMarkModelDao.insertOrReplace(bookMarkModel);

                // 更新书架上小说“最后阅读章节”的内容
                NovelModel novelModel = novelModelDao.load(bookMarkModel.getId());
                novelModel.setLastReadChapterTitle(bookMarkModel.getChapterTitle());
                novelModelDao.update(novelModel);

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
                // 获得Id相同且类型是LASTREAD的书签
                QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                        NovelBookMarkModelDao.Properties.Id.eq(novelId),
                        NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
                );

                subscriber.onNext((NovelBookMarkModel)queryBuilder.unique());
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
                // 获得Id相同且类型是LASTREAD的书签
                QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                        NovelBookMarkModelDao.Properties.Id.eq(novelId),
                        NovelBookMarkModelDao.Properties.BookMarkType.notEq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
                );

                subscriber.onNext((List<NovelBookMarkModel>)queryBuilder.list());
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
                Boolean isFavorite = isFavoriteSync(novelId);
                QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                        NovelBookMarkModelDao.Properties.Id.eq(novelId),
                        NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
                );
                Boolean hasLastRead = queryBuilder.count() == 1;
                if(isFavorite && hasLastRead)
                    subscriber.onNext((NovelBookMarkModel)queryBuilder.list().get(0));
                else
                    subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    public Observable<NovelDetailModel> getNovelDetail(String id, String src) {
        return novelSource.getNovelDetail(id,src);
    }

    @Override
    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String currentChapterSrc, String chapterTitle) {
        return novelSource.getOtherChapterSrc(novelId, currentChapterSrc, chapterTitle);
    }

    @Override
    public Observable<NovelChapterModel> changeChapterSrc(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel) {
        return Observable.create((Subscriber<? super NovelChapterModel> subscriber) -> {
            try {
                if(isFavoriteSync(chapterModel.getId())) {
                    DeleteQuery deleteChapterQuery = novelChapterModelDao.queryBuilder().where(
                            NovelChapterModelDao.Properties.Id.eq(chapterModel.getId()),
                            NovelChapterModelDao.Properties.Src.eq(chapterModel.getSrc())
                    ).buildDelete();
                    deleteChapterQuery.executeDeleteWithoutDetachingEntities();
                    DeleteQuery deleteChapterContentQuery = novelChapterContentModelDao.queryBuilder().where(
                            NovelChapterContentModelDao.Properties.Id.eq(chapterModel.getId()),
                            NovelChapterContentModelDao.Properties.Src.eq(chapterModel.getSrc())
                    ).buildDelete();
                    deleteChapterContentQuery.executeDeleteWithoutDetachingEntities();
                    clearDaoSession();
                    chapterModel.setSrc(changeSrcModel.getSrc());
                    chapterModel.setSecondId("");
                    novelChapterModelDao.insert(chapterModel);
                } else {
                    chapterModel.setSrc(changeSrcModel.getSrc());
                    chapterModel.setSecondId("");
                }
                subscriber.onNext(chapterModel);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    private List<NovelChapterModel> loadChaptersFromDB(String id) {
        //QueryBuilder<NovelChapterModel> queryBuilder = novelChapterModelDao.queryBuilder().where(NovelChapterModelDao.Properties.Id.eq(id)).orderAsc(NovelChapterModelDao.Properties.ChapterIndex);
        List<NovelChapterModel> chapters = novelChapterModelDao.deepQueryList(id);//queryBuilder.list();
        //Collections.sort(chapters);
        return chapters;
    }

    private List<NovelChapterModel> loadChaptersFromWeb(String id, String src) {
        return novelSource.getChapterListSync(id, src);
    }

    private void updateChapterListInDB(String id, List<NovelChapterModel> chapterModels) {
        DeleteQuery deleteQuery = novelChapterModelDao.queryBuilder().where(NovelChapterModelDao.Properties.Id.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
        novelChapterModelDao.insertInTx(chapterModels);
        NovelModel novelModel = novelModelDao.load(id);
        novelModel.setLatestUpdateCount(0);
        novelModel.setLatestChapterTitle(chapterModels.get(chapterModels.size() - 1).getTitle());
        novelModelDao.update(novelModel);
    }

    private NovelChapterContentModel loadChapterContentFromDB(String secondId) {
        QueryBuilder<NovelChapterContentModel> queryBuilder = novelChapterContentModelDao.queryBuilder().where(NovelChapterContentModelDao.Properties.SecondId.eq(secondId));
        if(queryBuilder.count() <= 0) {
            return null;
        } else {
            return queryBuilder.list().get(0);
        }
    }

    private NovelChapterContentModel loadChapterContentFromWeb(String id, String secondId, String src) {
        return novelSource.getChapterContentSync(id, secondId, src);
    }

    private void updateChapterContentInDB(String secondId, String src, NovelChapterContentModel chapterContentModel) {
        DeleteQuery deleteQuery = novelChapterContentModelDao.queryBuilder().whereOr(NovelChapterContentModelDao.Properties.SecondId.eq(secondId), NovelChapterContentModelDao.Properties.Src.eq(src)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
        novelChapterContentModelDao.insert(chapterContentModel);
    }

    private void clearDaoSession() {
        DaoSession daoSession = (DaoSession)novelModelDao.getSession();
        daoSession.clear();
    }
}
