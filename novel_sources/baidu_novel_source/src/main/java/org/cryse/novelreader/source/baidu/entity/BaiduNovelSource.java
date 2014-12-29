package org.cryse.novelreader.source.baidu.entity;

import org.cryse.novelreader.source.baidu.entity.categorylist.CategoryDataset;
import org.cryse.novelreader.source.baidu.entity.changesrc.ChangeSrcItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterContentItem;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListDataset;
import org.cryse.novelreader.source.baidu.entity.search.SearchDataset;
import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfDataset;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface BaiduNovelSource {
    static final String BAIDU_SEARCHBOX_URL = "/searchbox";

    @FormUrlEncoded
    @POST(BAIDU_SEARCHBOX_URL)
    public Observable<SyncShelfDataset> getNovelUpdate(
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
    public SyncShelfDataset getNovelUpdateSync(
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
    public Observable<CategoryDataset> getCategoryList(
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
    public Observable<ChapterListDataset> getChapterList(
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
    public ChapterListDataset getChapterListSync(
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
    public Observable<ChapterContentItem> getChapterContent(
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
    public ChapterContentItem getChapterContentSync(
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
    public Observable<SearchDataset> search(
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
    public Observable<String> getNovelDetail(
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
    public Observable<ChangeSrcItem[]> getOtherSource(
            @Query("action") String action,
            @Query("type") String type,
            @Query("service") String service,
            @Query("osname") String osname,
            @Query("osbranch") String osbranch,
            @Query("pkgname") String pkgname,
            @Field("data") String dataString
    );
}
