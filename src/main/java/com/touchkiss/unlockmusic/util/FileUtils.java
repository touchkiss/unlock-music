package com.touchkiss.unlockmusic.util;

import java.io.*;

/**
 * Created on 2020/03/17 15:47
 *
 * @author Touchkiss
 */
public class FileUtils {
    public static void writeBytesToFile(byte[] data, String destFilePath) throws IOException {
        OutputStream out = new FileOutputStream(destFilePath);
        InputStream is = new ByteArrayInputStream(data);
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }
}
