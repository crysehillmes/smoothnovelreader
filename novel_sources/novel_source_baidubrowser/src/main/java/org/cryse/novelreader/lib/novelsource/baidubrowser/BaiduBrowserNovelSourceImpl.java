package org.cryse.novelreader.lib.novelsource.baidubrowser;

import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.lib.novelsource.baidubrowser.converter.ToChapter;
import org.cryse.novelreader.lib.novelsource.baidubrowser.converter.ToChapterContent;
import org.cryse.novelreader.lib.novelsource.baidubrowser.converter.ToNovelDetail;
import org.cryse.novelreader.lib.novelsource.baidubrowser.converter.ToNovelFromSearch;
import org.cryse.novelreader.lib.novelsource.baidubrowser.converter.ToNovelUpdate;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.ChapterContentItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.ChapterItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.utils.CustomGsonConverter;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.model.UpdateRequestInfo;
import org.cryse.novelreader.source.NovelSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Subscriber;

public class BaiduBrowserNovelSourceImpl implements NovelSource {
    private static final String LOG_TAG = BaiduBrowserNovelSourceImpl.class.getSimpleName();
    private OkHttpClient mOkHttpClient;
    private BaiduBrowserNovelSource mNovelSource;

    public BaiduBrowserNovelSourceImpl() {
        this.mOkHttpClient = new OkHttpClient();
        this.mOkHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        this.mOkHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        this.mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        this.mNovelSource = new RestAdapter.Builder()
                .setRequestInterceptor(request -> {
                    request.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                    request.addHeader("Accept", "application/json,text/html");
                })
                .setEndpoint("http://m.baidu.com")
                .setClient(new OkClient(mOkHttpClient)).setConverter(new CustomGsonConverter(new Gson())).build().create(BaiduBrowserNovelSource.class);

    }

    @Override
    public Observable<List<NovelModel>> getCategories(String query, String subQuery, int page, int status, boolean isByTag) {
        /*String dataString = String.format("{\"" + (isByTag ? "tag" : "cid") +"\":\"%s\",\"" + (isByTag ? "subtag" : "subcate") + "\":\"%s\",\"pagenum\":%d,\"status\":%d,\"dataType\":\"json\"}",query,subQuery, page, status);
        return mNovelSource.getCategoryList(
                SEARCHBOX_ACTION,
                SEARCHBOX_CATELIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelModel());*/
        return Observable.create(new Observable.OnSubscribe<List<NovelModel>>() {
            @Override
            public void call(Subscriber<? super List<NovelModel>> subscriber) {
                subscriber.onNext(Collections.<NovelModel>emptyList());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<NovelModel>> getRanks(String cid, int page) {
        /*String dataString = String.format("{\"cid\":\"%s\",\"pageNum\":%d,\"subType\":0,\"dataType\":\"json\"}",cid, page);
        return mNovelSource.getCategoryList(
                SEARCHBOX_ACTION,
                SEARCHBOX_RANKLIST,
                SEARCHBOX_SERVICE,
                SEARCHBOX_OSNAME,
                SEARCHBOX_OSBRANCH,
                SEARCHBOX_PKGNAME,
                dataString
        ).map(new ToNovelModel());*/
        return Observable.create(new Observable.OnSubscribe<List<NovelModel>>() {
            @Override
            public void call(Subscriber<? super List<NovelModel>> subscriber) {
                subscriber.onNext(Collections.<NovelModel>emptyList());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ChapterModel>> getChapterList(String id, String src) {
        return mNovelSource.getChapters(id)
                .map(new ToChapter(id));
    }

    @Override
    public Observable<ChapterContentModel> getChapterContent(String id, String chapterId, String src) {
        String dataString = String.format("[\"%s\"]", chapterId);
        return mNovelSource.getChapterContent(dataString)
            .map(new ToChapterContent(id, chapterId));
    }

    @Override
    public Observable<List<NovelModel>> search(String queryString, int page) {
        return mNovelSource.search(queryString,page).map(new ToNovelFromSearch());
    }

    @Override
    public ChapterContentModel getChapterContentSync(String id, String chapterId, String src) {
        String dataString = String.format("[\"%s\"]", chapterId);
        ChapterContentItem[] item = mNovelSource.getChapterContentSync(dataString);
        return new ToChapterContent(id, chapterId).call(item);
    }

    @Override
    public List<ChapterModel> getChapterListSync(String id, String src) {
        ChapterItem[] chapters = mNovelSource.getChaptersSync(id);
        return new ToChapter(id).call(chapters);
    }

    @Override
    public List<NovelSyncBookShelfModel> getNovelUpdatesSync(UpdateRequestInfo... requestInfos) {
        String dataString = null;
        try {
            dataString = buildSyncShelfDataString(requestInfos);
            NovelUpdateItem[] dataset = mNovelSource.getNovelUpdateSync(
                    dataString
            );
            return new ToNovelUpdate().call(dataset);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return Collections.<NovelSyncBookShelfModel>emptyList();
    }

    @Override
    public Observable<NovelDetailModel> getNovelDetail(String id, String src) {
        return mNovelSource.getNovelDetail(id, 1, 0)
                .map(new ToNovelDetail());
    }

    @Override
    public Observable<List<NovelChangeSrcModel>> getOtherChapterSrc(String novelId, String chapterSrc, String title) {
        /*String dataString = String.format("{\"gid\":\"%s\",\"ctsrc\":\"%s\",\"title\":\"%s\"}", novelId, chapterSrc, title);
        return mNovelSource.getOtherSource(
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
        });*/
        return null;
    }

    private String buildSyncShelfDataString(UpdateRequestInfo... requestInfos) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(UpdateRequestInfo info : requestInfos) {
            JSONObject item = new JSONObject();
            item.put("id", info.getNovelId());
            item.put("ch", info.getChapterId());
            jsonArray.put(item);
        }
        return jsonArray.toString();
    }
}
