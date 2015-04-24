package com.galaxy.util.http;

import java.io.File;

public interface ISdCacheStrategy {
    public File getFile(String key);

    public void putFile(String key, byte[] srcData);

    public void putFile(String key, File srcFile);
}
