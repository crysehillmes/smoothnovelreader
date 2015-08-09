package org.cryse.novelreader.source.baidu.entity;

import org.cryse.novelreader.source.baidu.entity.categorylist.CategoryDataset;
import org.cryse.novelreader.source.baidu.entity.changesrc.ChangeSrcItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterContentItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListDataset;
import org.cryse.novelreader.source.baidu.entity.search.SearchDataset;
import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfItem;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface BaiduNovelSource {
    String BAIDU_SEARCHBOX_URL = "/searchbox";

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<SyncShelfItem[]> getNovelUpdate(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    SyncShelfItem[] getNovelUpdateSync(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<CategoryDataset> getCategoryList(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<ChapterListDataset> getChapterList(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    ChapterListDataset getChapterListSync(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<ChapterContentItem> getChapterContent(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    ChapterContentItem getChapterContentSync(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<SearchDataset> search(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<String> getNovelDetail(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    Observable<ChangeSrcItem[]> getOtherSource(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );
}
