package org.cryse.novelreader.data;

import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class NovelDatabaseAccessLayerImpl implements NovelDatabaseAccessLayer {
    NovelModelDao novelModelDao;

    NovelChapterModelDao novelChapterModelDao;

    NovelChapterContentModelDao novelChapterContentModelDao;

    NovelBookMarkModelDao novelBookMarkModelDao;

    @Inject
    public NovelDatabaseAccessLayerImpl(
            NovelModelDao novelModelDao,
            NovelChapterModelDao novelChapterModelDao,
            NovelChapterContentModelDao novelChapterContentModelDao,
            NovelBookMarkModelDao novelBookMarkModelDao
    ) {
        this.novelModelDao = novelModelDao;
        this.novelChapterModelDao = novelChapterModelDao;
        this.novelChapterContentModelDao = novelChapterContentModelDao;
        this.novelBookMarkModelDao = novelBookMarkModelDao;
    }

    @Override
    public boolean isFavorite(String id) {
        Boolean isExist;
        QueryBuilder<NovelModel> qb = novelModelDao.queryBuilder().where(NovelModelDao.Properties.Id.eq(id));
        isExist = qb.count() != 0;
        return isExist;
    }

    @Override
    public NovelModel loadFavorite(String id) {
        QueryBuilder<NovelModel> qb = novelModelDao.queryBuilder().where(NovelModelDao.Properties.Id.eq(id));
        return qb.count() != 0 ? qb.unique() : null;
    }

    @Override
    public void addToFavorite(NovelModel novel) {
        if (!isFavorite(novel.getId())) {
            novel.setSortWeight((new Date()).getTime());
            novelModelDao.insert(novel);
        }
    }

    @Override
    public void updateFavoritesStatus(Collection<NovelModel> novels) {
        novelModelDao.insertOrReplaceInTx(novels);
        clearDaoSession();
    }

    @Override
    public void removeFavorite(String... novelIds) {
        List novelsList = Arrays.asList(novelIds);

        // Clear BookMark
        DeleteQuery deleteBookMarksQuery = novelBookMarkModelDao.queryBuilder().where(NovelBookMarkModelDao.Properties.NovelId.in(novelsList)).buildDelete();
        deleteBookMarksQuery.executeDeleteWithoutDetachingEntities();

        // Clear ChapterContents
        DeleteQuery deleteChapterContentsQuery = novelChapterContentModelDao.queryBuilder().where(NovelChapterContentModelDao.Properties.Id.in(novelsList)).buildDelete();
        deleteChapterContentsQuery.executeDeleteWithoutDetachingEntities();

        // Clear Chapters
        DeleteQuery deleteChaptersQuery = novelChapterModelDao.queryBuilder().where(NovelChapterModelDao.Properties.Id.in(novelsList)).buildDelete();
        deleteChaptersQuery.executeDeleteWithoutDetachingEntities();

        // Remove NovelModel
        novelModelDao.deleteByKeyInTx(novelIds);

        // Clear DaoSession
        clearDaoSession();
    }

    @Override
    public List<NovelModel> loadAllFavorites() {
        clearDaoSession();
        List<NovelModel> favoriteNovels = novelModelDao.loadAll();
        Collections.sort(favoriteNovels);
        return favoriteNovels;
    }

    @Override
    public List<NovelChapterModel> loadChapters(String novelId) {
        return novelChapterModelDao.deepQueryList(novelId);
    }

    @Override
    public void insertChapter(String novelId, NovelChapterModel chapter) {
        novelChapterModelDao.insertOrReplace(chapter);
        NovelModel novelModel = novelModelDao.load(novelId);
        novelModel.setLatestUpdateCount(0);
        novelModel.setLatestChapterTitle(chapter.getTitle());
        novelModelDao.update(novelModel);
    }

    @Override
    public void insertChapters(String novelId, List<NovelChapterModel> chapters) {
        novelChapterModelDao.insertOrReplaceInTx(chapters);
        NovelModel novelModel = novelModelDao.load(novelId);
        novelModel.setLatestUpdateCount(0);
        novelModel.setLatestChapterTitle(chapters.get(chapters.size() - 1).getTitle());
        novelModelDao.update(novelModel);
    }

    @Override
    public void updateChapters(String novelId, List<NovelChapterModel> chapters) {
        DeleteQuery deleteQuery = novelChapterModelDao.queryBuilder().where(NovelChapterModelDao.Properties.Id.eq(novelId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
        insertChapters(novelId, chapters);
    }

    @Override
    public NovelChapterContentModel loadChapterContent(String chapterId) {
        QueryBuilder<NovelChapterContentModel> queryBuilder = novelChapterContentModelDao.queryBuilder().where(NovelChapterContentModelDao.Properties.SecondId.eq(chapterId));
        if(queryBuilder.count() <= 0) {
            return null;
        } else {
            return queryBuilder.list().get(0);
        }
    }

    @Override
    public void removeChapterContent(String chapterId) {
        DeleteQuery deleteQuery = novelChapterContentModelDao.queryBuilder().where(NovelChapterModelDao.Properties.SecondId.eq(chapterId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
    }

    @Override
    public void updateChapterContent(NovelChapterContentModel chapterContent) {
        DeleteQuery deleteQuery = novelChapterContentModelDao.queryBuilder().whereOr(
                NovelChapterContentModelDao.Properties.SecondId.eq(chapterContent.getSecondId()),
                NovelChapterContentModelDao.Properties.Src.eq(chapterContent.getSrc())
        ).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
        novelChapterContentModelDao.insert(chapterContent);
    }

    @Override
    public void addBookMark(NovelBookMarkModel bookMark) {
        novelBookMarkModelDao.insert(bookMark);
    }

    @Override
    public void insertOrUpdateLastReadBookMark(NovelBookMarkModel bookMark) {
        // Replace existing last read bookmark and by the new one.
        DeleteQuery deleteQuery = novelBookMarkModelDao.queryBuilder().where(
                NovelBookMarkModelDao.Properties.NovelId.eq(bookMark.getNovelId()),
                NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
        ).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        novelBookMarkModelDao.insertOrReplace(bookMark);

        // Update NovelModel lastReadChapterTitle field.
        NovelModel novelModel = novelModelDao.load(bookMark.getNovelId());
        novelModel.setLastReadChapterTitle(bookMark.getChapterTitle());
        novelModelDao.update(novelModel);
    }

    @Override
    public NovelBookMarkModel loadLastReadBookMark(String novelId) {
        // get last read bookmark according to novelId.
        QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                NovelBookMarkModelDao.Properties.NovelId.eq(novelId),
                NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
        );
        return (NovelBookMarkModel)queryBuilder.unique();
    }

    @Override
    public List<NovelBookMarkModel> loadBookMarks(String novelId) {
        // get all bookmarks according to novelId.
        QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                NovelBookMarkModelDao.Properties.NovelId.eq(novelId),
                NovelBookMarkModelDao.Properties.BookMarkType.notEq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
        );
        return (List<NovelBookMarkModel>)queryBuilder.list();
    }

    @Override
    public NovelBookMarkModel checkLastReadBookMarkState(String novelId) {
        Boolean isFavorite = isFavorite(novelId);
        QueryBuilder queryBuilder = novelBookMarkModelDao.queryBuilder().where(
                NovelBookMarkModelDao.Properties.NovelId.eq(novelId),
                NovelBookMarkModelDao.Properties.BookMarkType.eq(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD)
        );
        Boolean hasLastRead = queryBuilder.count() == 1;
        if(isFavorite && hasLastRead)
            return (NovelBookMarkModel)queryBuilder.list().get(0);
        else
            return null;
    }

    @Override
    public NovelChapterModel changeChapterSource(NovelChapterModel chapterModel, NovelChangeSrcModel changeSrcModel) {
        if(isFavorite(chapterModel.getId())) {
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
        return chapterModel;
    }

    private void clearDaoSession() {
        DaoSession daoSession = (DaoSession)novelModelDao.getSession();
        daoSession.clear();
    }
}
