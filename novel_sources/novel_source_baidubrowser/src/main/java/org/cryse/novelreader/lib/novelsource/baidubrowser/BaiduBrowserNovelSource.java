package org.cryse.novelreader.lib.novelsource.baidubrowser;

import org.cryse.novelreader.lib.novelsource.baidubrowser.model.ChapterContentItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.ChapterItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.DetailItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.baidubrowser.model.SearchNovelItem;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface BaiduBrowserNovelSource {
    String BAIDU_BROWSER_NOVEL_URL = "http://uil.cbs.baidu.com/novel/";
    @FormUrlEncoded
    @POST("update")
    NovelUpdateItem[] getNovelUpdateSync(
            @Field("list") String dataString
    );

    @GET("catalog")
    Observable<ChapterItem[]> getChapters(
            @Query("id") String novelId
    );

    @GET("catalog")
    ChapterItem[] getChaptersSync(
            @Query("id") String novelId
    );

    @FormUrlEncoded
    @POST("text")
    Observable<ChapterContentItem[]> getChapterContent(
            @Field("list") String dataString
    );


    @FormUrlEncoded
    @POST("text")
    ChapterContentItem[] getChapterContentSync(
            @Field("list") String dataString
    );

    @GET("GET")
    Observable<SearchNovelItem[]> search(
            @Query("kw") String keyword,
            @Query("pn") int page
    );

    @FormUrlEncoded
    @GET("detail")
    Observable<DetailItem> getNovelDetail(
            @Query("id") String novelId,
            @Query("catalog") int catalog,
            @Query("relate") int relate
    );
}
