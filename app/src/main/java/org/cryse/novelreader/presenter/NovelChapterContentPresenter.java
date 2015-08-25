package org.cryse.novelreader.presenter;

import android.text.TextPaint;

import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelChapterContentView;

import java.util.List;

public interface NovelChapterContentPresenter extends BaseFragmentPresenter<NovelChapterContentView> {
    void loadChapter(ChapterModel novelChapterModel, boolean forceUpdate);
    void loadNextChapter(ChapterModel novelChapterModel);
    void loadPrevChapter(ChapterModel novelChapterModel, boolean jumpToLast);
    void splitChapterAndDisplay(String title, String content);
    void addBookMark(BookmarkModel bookMarkModel);
    void saveLastReadBookMark(BookmarkModel bookMarkModel);
    List<ChapterModel> getChaptersState();
    void removeChaptersState();
    void saveChaptersState(List<ChapterModel> chapters);

    TextSplitParam getSplitParams();

    void setSplitParams(TextSplitParam splitParams);

    void getOtherSrc(ChapterModel novelChapterModel);
    void changeSrc(ChapterModel novelChapterModel, NovelChangeSrcModel changeSrcModel);

    class TextSplitParam {
        private int displayWidth;
        private int displayHeight;
        private float lineSpacingMultiplier;
        private float lineSpacingExtra;
        private TextPaint textPaint;

        public TextSplitParam() {
        }

        public TextSplitParam(int displayWidth, int displayHeight, float lineSpacingMultiplier, float lineSpacingExtra, TextPaint textPaint) {
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            this.lineSpacingMultiplier = lineSpacingMultiplier;
            this.lineSpacingExtra = lineSpacingExtra;
            this.textPaint = textPaint;
        }

        public int getDisplayWidth() {
            return displayWidth;
        }

        public void setDisplayWidth(int displayWidth) {
            this.displayWidth = displayWidth;
        }

        public int getDisplayHeight() {
            return displayHeight;
        }

        public void setDisplayHeight(int displayHeight) {
            this.displayHeight = displayHeight;
        }

        public float getLineSpacingMultiplier() {
            return lineSpacingMultiplier;
        }

        public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
        }

        public float getLineSpacingExtra() {
            return lineSpacingExtra;
        }

        public void setLineSpacingExtra(float lineSpacingExtra) {
            this.lineSpacingExtra = lineSpacingExtra;
        }

        public TextPaint getTextPaint() {
            return textPaint;
        }

        public void setTextPaint(TextPaint textPaint) {
            this.textPaint = textPaint;
        }
    }
}
