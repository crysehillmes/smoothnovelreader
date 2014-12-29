package org.cryse.novelreader.util;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import org.cryse.novelreader.ui.adapter.ContentAdapter;

import java.util.List;

/**
 * Created by cryse on 11/6/14.
 */
public class RecyclerViewUtils {
    public static <T> void addOneByOne(
            Handler mHandler,
            ContentAdapter<T> adapter,
            long delay,
            List<T> data
    ) {
        for (int i = 0; i < data.size(); i++) {
            final int finalI = i;
            mHandler.postDelayed(() -> {
                adapter.add(data.get(finalI));
            }, i * delay);
        }
    }
}
