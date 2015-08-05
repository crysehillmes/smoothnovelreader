package org.cryse.novelreader.source;

import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;

import java.util.List;

import rx.Observable;

public interface NovelSource {
    public Observable<List<NovelModel>> getCategories(String query, String subQuery, int page, int status, boolean isByTag);

    public Observable<List<NovelModel>> getRanks(String cid, int page);

    public Observable<List<ChapterModel>> getChapterList(String id, String src);

    public Observable<ChapterContentModel> getChapterContent(String id, String secondId, String src);

    public Observable<List<NovelModel>> search(String queryString, int page);

    public ChapterContentModel getChapterContentSync(String id, String secondId, String src);

    public List<ChapterModel> getChapterListSync(String id, String src);

    public List<NovelSyncBookShelfModel> getNovelUpdatesSync(String ... novelIds);

    public Observable<NovelDetailModel> getNovelDetail(String id, String src);

    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String chapterSrc, String title);
}
