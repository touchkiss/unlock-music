package com.touchkiss.unlockmusic.qmc;

import com.touchkiss.unlockmusic.qmc.detect.QmcMaskDetectMflac;
import com.touchkiss.unlockmusic.qmc.detect.QmcMaskDetectMgg;
import com.touchkiss.unlockmusic.qmc.handler.QmcHandler;
import com.touchkiss.unlockmusic.util.AudioMimeType;
import com.touchkiss.unlockmusic.util.CommonUtils;
import com.touchkiss.unlockmusic.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2020/03/16 15:14
 *
 * @author Touchkiss
 */
public class QmcDecrypt {
    private final static Map<String, QmcHandler> handlerMap = new HashMap() {{
        put("mgg", new QmcHandler(new QmcMaskDetectMgg(), "ogg", true));
        put("mflac", new QmcHandler(new QmcMaskDetectMflac(), "flac", true));
    }};

    public static void main(String[] args) {
        File file = new File("D:\\music\\VipSongsDownload\\黄晓明 - 别哭，我最爱的人.mgg");
        QmcDecrypt qmcDecrypt = new QmcDecrypt();
        String fileName = file.getName();
        qmcDecrypt.decrypt(file, fileName, fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    void decrypt(File encryptedFile, String rawFilename, String rawExt) {
        if (!handlerMap.containsKey(rawExt)) {
            System.out.println("===================");
            System.out.println("File type is incorrect!");
            System.out.println("===================");
        }
        QmcHandler handler = handlerMap.get(rawExt);
        try {
            byte[] audioData, keyData;
            QmcMask seed;
            FileInputStream fileInputStream = new FileInputStream(encryptedFile);
            int fileLength = (int) encryptedFile.length();
            byte[] fileData = new byte[fileLength];
            fileInputStream.read(fileData);
            if (handler.isDetect()) {
                audioData = new byte[fileLength - 0x170];
                System.arraycopy(fileData, 0, audioData, 0, fileLength - 0x170);
                seed = handler.getDetector().handler(audioData);
                keyData = new byte[0x170];
                System.arraycopy(fileData, fileLength - 0x171, keyData, 0, 0x170);
                if (seed == null) {
//                    seed=
                    CommonUtils.queryKeyInfo(keyData, rawFilename, rawExt);
                }
                if (seed == null) {
                    System.out.println("=============");
                    System.out.println(rawExt + "格式仅提供实验性支持！");
                    System.out.println("=============");
                }
            } else {
                audioData = fileData;
                seed = handler.getDetector().handler(audioData);
            }

            byte[] dec = seed.decrypt(audioData);

            String ext = CommonUtils.detectAudioExt(dec, handler.getExt());
            String mime = AudioMimeType.valueOf(ext).getValue();

            byte[] mimeInfo = new byte[128];
            System.arraycopy(dec, dec.length - 129, mimeInfo, 0, 128);

            String name = encryptedFile.getName();
            FileUtils.writeBytesToFile(dec, "D://" + name.substring(0, name.lastIndexOf(".") + 1) + ext);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
