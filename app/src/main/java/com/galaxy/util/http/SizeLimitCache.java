package com.galaxy.util.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.galaxy.ishare.AppConst;
import com.galaxy.util.utils.FileUtil;

public class SizeLimitCache implements ISdCacheStrategy {
    private Comparator<File> mFileComparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;
            } else if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }

    };

    private String mCachePath = AppConst.ROOT_PATH + "/SizeLimitCache";
    private int mMaxSize = 30 << 20; // 30M

    @Override
    public File getFile(String key) {
        File cache = new File(mCachePath);
        if (!cache.exists()) {
            FileUtil.makeDir(mCachePath);
            return null;
        } else {
            return FileUtil.findFile(mCachePath, key);
        }
    }

    @Override
    public void putFile(String key, File src) {
        File cache = new File(mCachePath);
        if (!cache.exists()) {
            FileUtil.makeDir(mCachePath);
        }
        if (!FileUtil.copyFile(src.getAbsolutePath(), mCachePath + "/" + src.getName())) {
            return;
        }

        long totalSapce = cache.getTotalSpace();
        List<File> sortList = null;
        if (totalSapce > mMaxSize) {
            File[] files = cache.listFiles();
            sortList = new ArrayList<File>();
            for (File f : files) {
                sortList.add(f);
            }
            Collections.sort(sortList, mFileComparator);
            while (totalSapce > mMaxSize) {
                sortList.get(sortList.size() - 1).delete();
                sortList.remove(sortList.size() - 1);
                totalSapce = cache.getTotalSpace();
            }
        }
    }

    @Override
    public void putFile(String key, byte[] srcData) {
    }
}
