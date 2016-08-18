package org.cryse.chaptersplitter;

import android.util.Log;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import org.apache.tika.metadata.Metadata;
import org.cryse.utils.UniversalEncodingDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LocalTextReader {
    public static final int DEFAULT_MAX_CHAPTERS_CACHE_SIZE = 10;
    static final Pattern chapterNumberPattern = Pattern.compile("(第[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部]?\\s+)|([第]?[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部]\\s+)");
    static final Pattern chapterNumberExcludePattern = Pattern.compile("(第[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部]?[完终])|([第]?[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部][完终])");
    static final Pattern symbolPattern = Pattern.compile("[,./`!@#$%^&*()-=_+;:'\"\\{\\}，。、！￥…（）《》<>？：“”；a-zA-z]+");
    static final Pattern symbol2Pattern = Pattern.compile("[,./`!@#$%^&*-=_+;:\\{\\}，。、！￥…<>？：；a-zA-z]+");
    static final Pattern bookNamePattern = Pattern.compile("《.+》");
    private int mListCacheSize = DEFAULT_MAX_CHAPTERS_CACHE_SIZE;
    private File mFile;
    private String mBookName = null;
    private InputStream mInputStream;
    private BufferedInputStream mBufferedInputStream;
    private BufferedReader mBufferedReader;
    private OnReadProgressListener mOnReadProgressListener;

    public LocalTextReader(String textFilePath) throws FileNotFoundException {
        this(new File(textFilePath));
    }

    public LocalTextReader(File textFile) throws FileNotFoundException {
        this.mFile = textFile;
        parseBookName();
    }

    private static boolean checkChapterTitle(List<TextChapter> chapterList, int lineCount, String line) {
        if (line != null) {
            if (line.length() > 75) return false; // 超过75字的一般就不是标题了
            /*if(!line.contains("章") &&
                    !line.contains("回") &&
                    !line.contains("节") &&
                    !line.contains("卷") &&
                    !line.contains("集") &&
                    !line.contains("幕") &&
                    !line.contains("计") &&
                    !line.contains("部")) { // 不包含这些关键字的话也一般不是章节标题
                return false;
            }*/
            String trimLine = line.trim().replace("\u3000", "");
            if (trimLine != null && trimLine.length() <= 75) {
                Matcher matchResult = chapterNumberPattern.matcher(trimLine);
                int symbolCount = countSymbolsCount(line);
                if (matchResult.find() && matchResult.start() < 5 && symbolCount < 3) {
                    Matcher excludeMatcher = chapterNumberExcludePattern.matcher(line);
                    if (excludeMatcher.find()) {
                        return false;
                    }
                    if (matchResult.find(0) && matchResult.start() > 0 && countSymbolsCount(line) > 1) {
                        return false;
                    }
                    return addToChapterList(chapterList, lineCount, line);
                } else if (trimLine.length() >= 3 && !line.startsWith(" ") && !line.startsWith("\u3000") && line.length() < 30 && countSymbolsCount(line) < 3) {
                    String removeSymbol = replaceSymbols(trimLine);
                    if (removeSymbol != null && removeSymbol.length() >= 2) {
                        return addToChapterList(chapterList, lineCount, line);
                    }
                }
            }
        }
        return false;
    }

    private static boolean addToChapterList(List<TextChapter> chapterList, int startLine, String currentChapter) {
        currentChapter = trim(currentChapter);
        int count = chapterList.size();
        if (count > 0) {
            int last = count - 1;
            TextChapter lastChapter = chapterList.get(last);
            String lastChapterName = lastChapter.getChapterName();
            if (!lastChapterName.equals(currentChapter) && !containsMutual(currentChapter, lastChapterName)) {
                if (startLine - 1 >= 0) {
                    lastChapter.setEndLineNumber(startLine - 1);
                    chapterList.add(new TextChapter(currentChapter, startLine, -1));
                    return true;
                }
            }

        } else {
            chapterList.add(new TextChapter(currentChapter, startLine, -1));
            return true;
        }
        return false;
    }

    private static String replaceSymbols(String input) {
        Matcher matcher = symbolPattern.matcher(input);

        return matcher.replaceAll("");
    }

    private static int countSymbolsCount(String input) {
        Matcher matcher = symbol2Pattern.matcher(input);
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    private static String trim(String input) {
        return input.trim().replace("\u3000", "");
    }

    public static boolean containsMutual(String one, String two) {
        String trimmedOne = replaceSymbols(trim(one));
        String trimmedTwo = replaceSymbols(trim(two));
        return trimmedOne.contains(trimmedTwo) || trimmedTwo.contains(trimmedOne);
    }

    public static boolean trimNoSymbolEqual(String one, String two) {
        if ((((float) Math.max(one.length(), two.length()) / ((float) Math.min(one.length(), two.length())))) > 3) {
            return false;
        }
        String trimmedOne = replaceSymbols(trim(one));
        String trimmedTwo = replaceSymbols(trim(two));
        return trimmedOne.equals(trimmedTwo);
    }

    private void parseBookName() {
        String fileName = this.mFile.getName();
        Matcher matcher = bookNamePattern.matcher(fileName);
        while (matcher.find()) {
            mBookName = matcher.group();
            mBookName = mBookName.substring(1, mBookName.length() - 1);
        }
        if (mBookName == null) {
            int lastIndexOfDot = fileName.lastIndexOf(".");
            if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length())
                mBookName = fileName.substring(0, lastIndexOfDot);
            else
                mBookName = fileName;
        }
    }

    public void open() throws IOException {
        this.mInputStream = new FileInputStream(mFile);
        UniversalEncodingDetector detector = new UniversalEncodingDetector();
        this.mBufferedInputStream = new BufferedInputStream(mInputStream);
        Charset charset = detector.detect(mBufferedInputStream, new Metadata());
        this.mBufferedInputStream.reset();
        InputStreamReader reader = new InputStreamReader(mBufferedInputStream, charset);
        this.mBufferedReader = new BufferedReader(reader);
    }

    public boolean isOpen() {
        try {
            if (mBufferedReader != null) {
                return mBufferedReader.ready();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBookName() {
        return mBookName;
    }

    public int readChapters(OnChapterReadCallback callback) throws IOException {
        int mChapterCount = 0;
        List<TextChapter> textChapters = new ArrayList<TextChapter>(256);
        String line;
        final int totalLineCount = countLines(mBufferedReader, true, false);
        int lineCount = 1;
        int percent = 0;
        line = mBufferedReader.readLine();
        StringBuilder chapterContentBuilder = new StringBuilder();
        while (line != null) {
            line = mBufferedReader.readLine(); // 一次读入一行数据
            if (line != null) {
                lineCount++;
                int newPercent = (int) Math.floor((((float) lineCount) / ((float) totalLineCount)) * 100);
                if (newPercent > percent) {
                    percent = newPercent;
                    Log.d("LINECOUNT", String.format("%d / %d, Percent: %d", lineCount, totalLineCount, percent));
                    if (mOnReadProgressListener != null) {
                        mOnReadProgressListener.onReadProgress(percent);
                    }
                }
                boolean isChapterTitle = checkChapterTitle(textChapters, lineCount, line);
                if (textChapters.size() > mListCacheSize) {
                    textChapters.remove(0);
                }
                int count = textChapters.size();
                if (isChapterTitle) {
                    mChapterCount++;
                    if (count > 1) {
                        TextChapter prevChapter = textChapters.get(count - 2);
                        prevChapter.setChapterSize(chapterContentBuilder.length());
                        String chapterContent = chapterContentBuilder.toString();
                        if (trimNoSymbolEqual(prevChapter.getChapterName(), chapterContent))
                            continue;
                        if (callback != null)
                            callback.onChapterRead(prevChapter, chapterContent);
                    }
                    chapterContentBuilder.delete(0, chapterContentBuilder.length());
                }
                // if(trim(line).length() > 0)
                chapterContentBuilder.append(line).append('\n');

            }
        }
        int count = textChapters.size();
        if (count > 0) {
            int last = count - 1;
            TextChapter lastChapter = textChapters.get(last);
            lastChapter.setEndLineNumber(lineCount);
            if (chapterContentBuilder.length() > 0) {
                lastChapter.setChapterSize(chapterContentBuilder.length());
                if (callback != null)
                    callback.onChapterRead(lastChapter, chapterContentBuilder.toString());
            }
        }
        return mChapterCount;
    }

    public void setListCacheSize(int listCacheSize) {
        this.mListCacheSize = listCacheSize;
    }

    public void setOnReadProgressListener(OnReadProgressListener onReadProgressListener) {
        this.mOnReadProgressListener = onReadProgressListener;
    }

    public int countLines(BufferedReader reader, boolean reset, boolean close) throws IOException {
        int lineCount = 0;
        try {
            byte[] c = new byte[1024];
            int count = 0;
            while (reader.readLine() != null) count++;
            reader.close();
            lineCount = count == 0 ? 1 : count;
        } finally {
            if (reset) {
                try {
                    // this gives error for larger images taken by camera
                    reader.reset();
                } catch (IOException e) {
                    reader.close();
                    open();
                }
            }
            if (close)
                reader.close();
        }
        return lineCount;
    }

    public interface OnChapterReadCallback {
        void onChapterRead(TextChapter chapter, String content);
    }

    public interface OnReadProgressListener {
        void onReadProgress(int percent);
    }
}