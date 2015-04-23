package com.galaxy.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;


public class GzipDecoder {
	
	private static final int BUFFER_SIZE = 4096;
	
    public static byte[] gzipDecoder(byte[] src) {
        if (src==null || src.length<=0){return null;}

        ByteArrayInputStream is = new ByteArrayInputStream(src);
        GZIPInputStream gis = null;
        byte[] desbyte = null;

        try {
            gis = new GZIPInputStream(is);
            ByteArrayBuffer buffer = new ByteArrayBuffer(0);
            int byteRead = 0;
            byte[] tempBytes = new byte[BUFFER_SIZE];

            while ((byteRead = gis.read(tempBytes)) != -1) {
                buffer.append(tempBytes, 0, byteRead);
            }
            desbyte = buffer.toByteArray();

        } catch (IOException e) {
            Log.e("Gzip", "Gzip exception: " + e.toString());
            return null;
        } finally {
            try {
                is.close();
                if (gis != null) {
                    gis.close();
                }
            } catch (IOException e) {
                Log.e("Gzip", e.toString());
                return null;
            }
        }

        return desbyte;
    }
}
