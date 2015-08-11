package org.cryse.novelreader.source;

import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.model.UpdateRequestInfo;

import java.util.List;

import rx.Observable;

public interface NovelSource {
    Observable<List<NovelModel>> getCategories(String query, String subQuery, int page, int status, boolean isByTag);

    Observable<List<NovelModel>> getRanks(String cid, int page);

    Observable<List<ChapterModel>> getChapterList(String id, String src);

    Observable<ChapterContentModel> getChapterContent(String id, String secondId, String src);

    Observable<List<NovelModel>> search(String queryString, int page);

    ChapterContentModel getChapterContentSync(String id, String secondId, String src);

    List<ChapterModel> getChapterListSync(String id, String src);

    List<NovelSyncBookShelfModel> getNovelUpdatesSync(UpdateRequestInfo... requestInfos);

    Observable<NovelDetailModel> getNovelDetail(String id, String src);

    Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String chapterSrc, String title);
}
