package org.cryse.novelreader.source.baidu;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.source.baidu.converter.ToNovelDetailModel;
import org.cryse.novelreader.source.baidu.converter.ToNovelModel;
import org.cryse.novelreader.source.baidu.parser.gson.CustomGsonConverter;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.source.baidu.converter.ToNovelChapterContent;
import org.cryse.novelreader.source.baidu.converter.ToNovelChapterModel;
import org.cryse.novelreader.source.baidu.converter.ToNovelModelFromSearch;
import org.cryse.novelreader.source.baidu.converter.ToNovelSyncBookShelfModel;
import org.cryse.novelreader.source.baidu.entity.BaiduNovelSource;
import org.cryse.novelreader.source.baidu.entity.changesrc.ChangeSrcItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterContentItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListDataset;
import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfDataset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;

public class BaiduNovelSourceImpl implements NovelSource {
    BaiduNovelSource novelSource = new RestAdapter.Builder()
            .setRequestInterceptor(request -> {
                request.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                request.addHeader("Accept", "application/json,text/html");
            })
            .setEndpoint("http://m.baidu.com")
            .setClient(new OkClient(new OkHttpClient())).setConverter(new CustomGsonConverter(new Gson())).build().create(BaiduNovelSource.class);

    private static final String SEARCHBOX_ACTION = "novel";
    private static final String SEARCHBOX_CATELIST = "catelist";
    private static final String SEARCHBOX_RANKLIST = "ranklist";
    private static final String SEARCHBOX_CHAPTER_CONTENT = "content";
    private static final String SEARCHBOX_CHAPTERLIST = "chapter";
    private static final String SEARCHBOX_SYNCSHELF = "syncshelf";
    private static final String SEARCHBOX_SEARCH = "search";
    private static final String SEARCHBOX_DETAIL = "detail";
    private static final String SEARCHBOX_CHANGESRC = "changesrc";
    private static final String SEARCHBOX_OSBRANCH = "a0";
    private static final String SEARCHBOX_PKGNAME = "com.baidu.searchbox";
    private static final String SEARCHBOX_SERVICE = "bdbox";
    private static final String SEARCHBOX_OSNAME = "baiduboxapp";

    @Override
    public Observable<List<NovelModel>> getCategories(String query, String subQuery, int page, int status, boolean isByTag) {
        String dataString = String.format("{\"" + (isByTag ? "tag" : "cid") +"\":\"%s\",\"" + (isByTag ? "subtag" : "subcate") + "\":\"%s\",\"pagenum\":%d,\"status\":%d,\"dataType\":\"json\"}",query,subQuery, page, status);
        return novelSource.getCategoryList(
                SEARCHBOX_ACTION,
                SEARCHBOX_CATELIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelModel());
    }

    @Override
    public Observable<List<NovelModel>> getRanks(String cid, int page) {
        String dataString = String.format("{\"cid\":\"%s\",\"pageNum\":%d,\"subType\":0,\"dataType\":\"json\"}",cid, page);
        return novelSource.getCategoryList(
                SEARCHBOX_ACTION,
                SEARCHBOX_RANKLIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelModel());
    }

    @Override
    public Observable<List<NovelChapterModel>> getChapterList(String id, String src) {
        String dataString = String.format("{\"gid\":%s,\"cpsrc\":\"%s\"}", id, src);
        return novelSource.getChapterList(
                SEARCHBOX_ACTION,
                SEARCHBOX_CHAPTERLIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelChapterModel(id));
    }

    @Override
    public Observable<NovelChapterContentModel> getChapterContent(String id, String secondId, String src) {
        String dataString = String.format("{\"gid\":%s,\"dir\":\"0\",\"cid\":\"%s\",\"ctsrc\":\"%s\"}", id, secondId, src);
        return novelSource.getChapterContent(
                SEARCHBOX_ACTION,
                SEARCHBOX_CHAPTER_CONTENT,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString).map(new ToNovelChapterContent(id, secondId));
    }

    @Override
    public Observable<List<NovelModel>> search(String queryString, int page) {
        String dataString = String.format("{\"word\":\"%s\",\"pageNum\":%d,\"dataType\":\"json\"}", queryString, page);
        return novelSource.search(
                SEARCHBOX_ACTION,
                SEARCHBOX_SEARCH,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString).map(new ToNovelModelFromSearch());
    }

    @Override
    public NovelChapterContentModel getChapterContentSync(String id, String secondId, String src) {
        String dataString = String.format("{\"gid\":%s,\"dir\":\"0\",\"cid\":\"%s\",\"ctsrc\":\"%s\"}", id, secondId, src);
        ChapterContentItem item = novelSource.getChapterContentSync(
                SEARCHBOX_ACTION,
                SEARCHBOX_CHAPTER_CONTENT,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString);
        return new ToNovelChapterContent(id, secondId).call(item);
    }

    @Override
    public List<NovelChapterModel> getChapterListSync(String id, String src) {
        String dataString = String.format("{\"gid\":%s,\"cpsrc\":\"%s\"}", id, src);
        ChapterListDataset dataset = novelSource.getChapterListSync(
                SEARCHBOX_ACTION,
                SEARCHBOX_CHAPTERLIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        );
        return new ToNovelChapterModel(id).call(dataset);
    }

    @Override
    public List<NovelSyncBookShelfModel> getNovelUpdatesSync(String... novelIds) {
        String dataString = buildSyncShelfDataString(novelIds);
        SyncShelfDataset dataset = novelSource.getNovelUpdateSync(
                SEARCHBOX_ACTION,
                SEARCHBOX_SYNCSHELF,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        );
        return new ToNovelSyncBookShelfModel().call(dataset);
    }

    @Override
    public Observable<NovelDetailModel> getNovelDetail(String id, String src) {
        String dataString = String.format("{\"gid\":\"%s\",\"cpsrc\":\"%s\",\"fromaction\":\"catelist\"}", id, src);
        return novelSource.getNovelDetail(
                SEARCHBOX_ACTION,
                SEARCHBOX_DETAIL,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelDetailModel());
    }

    @Override
    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String chapterSrc, String title) {
        String dataString = String.format("{\"gid\":\"%s\",\"ctsrc\":\"%s\",\"title\":\"%s\"}", novelId, chapterSrc, title);
        return novelSource.getOtherSource(
                SEARCHBOX_ACTION,
                SEARCHBOX_CHANGESRC,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map((s) -> {
            if(s == null || s.length <= 0) return Collections.<NovelChangeSrcModel>emptyList();
            List<NovelChangeSrcModel> result = new ArrayList<NovelChangeSrcModel>(s.length);
            for(ChangeSrcItem item : s) {
                result.add(new NovelChangeSrcModel(item.getGid(), item.getTitle(),item.getBookname(),item.getCtsrc()));
            }
            return result;
        });
    }

    private String buildSyncShelfDataString(String... novelIds) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"gids\":\"");
        int count = novelIds.length;
        for (int i = 0; i < count; i++) {
            builder.append(novelIds[i]);
            if (i != count - 1)
                builder.append("_");
        }
        builder.append("\"}");
        return builder.toString();
    }
}
