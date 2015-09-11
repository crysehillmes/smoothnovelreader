package org.cryse.novelreader.lib.novelsource.easou;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.lib.novelsource.easou.converter.ToChapter;
import org.cryse.novelreader.lib.novelsource.easou.converter.ToChapterContent;
import org.cryse.novelreader.lib.novelsource.easou.converter.ToNovelDetail;
import org.cryse.novelreader.lib.novelsource.easou.converter.ToNovelFromSearch;
import org.cryse.novelreader.lib.novelsource.easou.converter.ToNovelUpdate;
import org.cryse.novelreader.lib.novelsource.easou.model.ChapterContentItem;
import org.cryse.novelreader.lib.novelsource.easou.model.ChapterItem;
import org.cryse.novelreader.lib.novelsource.easou.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.easou.utils.CustomGsonConverter;
import org.cryse.novelreader.lib.novelsource.easou.utils.EasouNovelId;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.model.UpdateRequestInfo;
import org.cryse.novelreader.source.NovelSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Subscriber;

public class EasouNovelSource implements NovelSource {
    public static final int SOURCE_EASOU = Consts.SOURCE_EASOU;
    public static final String SOURCE_NAME_EASOU = Consts.SOURCE_NAME_EASOU;
    private static final String LOG_TAG = EasouNovelSource.class.getSimpleName();
    private static final String CONST_APP_VERSION = "appversion";
    private static final String CONST_CH = "blp1298_10269_001";
    private static final String CONST_CID = "eef_easou_book";
    private static final String CONST_OS = "android";
    private static final String CONST_VERSION = "002";

    /*private OkHttpClient mOkHttpClient;*/
    private EasouNovelService mNovelSource;

    public EasouNovelSource(OkHttpClient okHttpClient) {
        /*this.mOkHttpClient = new OkHttpClient();
        this.mOkHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        this.mOkHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        this.mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);*/
        this.mNovelSource = new RestAdapter.Builder()
                .setRequestInterceptor(request -> {
                    request.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                    request.addHeader("Accept", "application/json,text/html");
                })
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(EasouNovelService.EASOU_NOVEL_URL)
                .setConverter(new CustomGsonConverter(new Gson()))
                .build()
                .create(EasouNovelService.class);
    }

    public static NovelSource build(OkHttpClient okHttpClient) {
        return new EasouNovelSource(okHttpClient);
    }

    @Override
    public int getNovelType() {
        return SOURCE_EASOU;
    }

    @Override
    public String getNovelSourceName() {
        return SOURCE_NAME_EASOU;
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
        long gid = EasouNovelId.fromNovelIdToGid(id);
        long nid = EasouNovelId.fromNovelIdToNid(id);

        return mNovelSource.getChapters(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                10000,
                gid,
                nid
        ).map(new ToChapter(id));
    }

    @Override
    public Observable<ChapterContentModel> getChapterContent(String id, String chapterId, String chapterTitle, String src) {
        long gid = EasouNovelId.fromNovelIdToGid(id);
        long nid = EasouNovelId.fromNovelIdToNid(id);
        String dataString = String.format("[\"%s\"]", chapterId);
        int sort = Integer.valueOf(chapterId);
        return mNovelSource.getChapterContent(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                gid,
                nid,
                sort,
                chapterTitle
        )
                .map(new ToChapterContent(id, chapterId));
    }

    @Override
    public Observable<List<NovelModel>> search(String queryString, int page) {
        return mNovelSource.search(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                0,
                0,
                20,
                page,
                queryString).map(new ToNovelFromSearch());
    }

    @Override
    public ChapterContentModel getChapterContentSync(String id, String chapterId, String chapterTitle, String src) {
        long gid = EasouNovelId.fromNovelIdToGid(id);
        long nid = EasouNovelId.fromNovelIdToNid(id);
        int sort = Integer.valueOf(chapterId);
        String dataString = String.format("[\"%s\"]", chapterId);
        ChapterContentItem item = mNovelSource.getChapterContentSync(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                gid,
                nid,
                sort,
                chapterTitle
        );
        return new ToChapterContent(id, chapterId).call(item);
    }

    @Override
    public List<ChapterModel> getChapterListSync(String id, String src) {
        long gid = EasouNovelId.fromNovelIdToGid(id);
        long nid = EasouNovelId.fromNovelIdToNid(id);
        ChapterItem[] chapters = mNovelSource.getChaptersSync(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                10000,
                gid,
                nid
        );
        return new ToChapter(id).call(chapters);
    }

    @Override
    public List<NovelSyncBookShelfModel> getNovelUpdatesSync(UpdateRequestInfo... requestInfos) {
        return getNovelUpdatesSync(Arrays.asList(requestInfos));
    }

    @Override
    public List<NovelSyncBookShelfModel> getNovelUpdatesSync(List<UpdateRequestInfo> requestInfos) {
        List<NovelSyncBookShelfModel> result = new ArrayList<>(requestInfos.size());
        ToNovelUpdate converter = new ToNovelUpdate();
        for (UpdateRequestInfo info : requestInfos) {
            long gid = EasouNovelId.fromNovelIdToGid(info.getNovelId());
            long nid = EasouNovelId.fromNovelIdToNid(info.getNovelId());

            NovelUpdateItem dataset = mNovelSource.getNovelUpdateSync(
                    CONST_APP_VERSION,
                    CONST_CH,
                    CONST_CID,
                    CONST_OS,
                    CONST_VERSION,
                    gid
            );
            result.add(converter.call(dataset));
        }
        return result;
    }

    @Override
    public Observable<NovelDetailModel> getNovelDetail(String id, String src) {
        long gid = EasouNovelId.fromNovelIdToGid(id);
        long nid = EasouNovelId.fromNovelIdToNid(id);
        return mNovelSource.getNovelDetail(
                CONST_APP_VERSION,
                CONST_CH,
                CONST_CID,
                CONST_OS,
                CONST_VERSION,
                gid
        ).map(new ToNovelDetail());
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

    @Override
    public String getCopyRightStatement(Context context) {
        return context.getString(R.string.copyright_statement_easou);
    }
}
