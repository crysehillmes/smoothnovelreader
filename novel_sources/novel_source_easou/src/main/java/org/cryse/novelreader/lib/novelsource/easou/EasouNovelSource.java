package org.cryse.novelreader.lib.novelsource.easou;

import org.cryse.novelreader.lib.novelsource.easou.model.ChapterContentItem;
import org.cryse.novelreader.lib.novelsource.easou.model.ChapterItem;
import org.cryse.novelreader.lib.novelsource.easou.model.DetailItem;
import org.cryse.novelreader.lib.novelsource.easou.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.easou.model.SearchNovelItem;

import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface EasouNovelSource {
    String EASOU_NOVEL_URL = "http://api.easou.com/api/bookapp";

    @FormUrlEncoded
    @POST("/cover.m")
    NovelUpdateItem[] getNovelUpdateSync(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("gid") long gid
    );

    @GET("/chapter_list.m")
    Observable<ChapterItem[]> getChapters(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("size") int size,
            @Query("gid") long gid,
            @Query("nid") long nid
    );

    @GET("/chapter_list.m")
    ChapterItem[] getChaptersSync(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("size") int size,
            @Query("gid") long gid,
            @Query("nid") long nid
    );

    @FormUrlEncoded
    @POST("/chapter.m")
    Observable<ChapterContentItem[]> getChapterContent(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("gid") long gid,
            @Query("nid") long nid,
            @Query("sort") int sort,
            @Query("chapter_name") String chapterName
    );


    @FormUrlEncoded
    @POST("/chapter.m")
    ChapterContentItem[] getChapterContentSync(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("gid") long gid,
            @Query("nid") long nid,
            @Query("sort") int sort,
            @Query("chapter_name") String chapterName
    );

    @GET("/search.m")
    Observable<SearchNovelItem[]> search(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("sort_type") int sort_type,
            @Query("type") int type,
            @Query("count") int count,
            @Query("page_id") int page_id,
            @Query("word") String word
    );

    @GET("/cover.m")
    Observable<DetailItem> getNovelDetail(
            @Query("appversion") String appversion,
            @Query("ch") String ch,
            @Query("cid") String cid,
            @Query("os") String os,
            @Query("version") String version,
            @Query("gid") long gid
    );
}
