package com.touchkiss.unlockmusic.qmc.detect;

import com.touchkiss.unlockmusic.qmc.QmcMask;
import com.touchkiss.unlockmusic.util.CommonUtils;

/**
 * Created on 2020/03/17 16:58
 *
 * @author Touchkiss
 */
public class QmcMaskDetectMflac implements Detector {
    @Override
    public QmcMask handler(byte[] audioData) throws Exception {
        int search_len = Math.min(0x8000, audioData.length);
        QmcMask mask = null;
        for (int block_idx = 0; block_idx < search_len; block_idx += 128) {
            try {
                short[] temp = new short[128];
                for (int i = 0; i < 128; i++) {
                    temp[i] = (short) (audioData[i + block_idx] &0x00ff);
                }
                mask = new QmcMask(temp, null, null);
                byte[] header = new byte[CommonUtils.FLAC_HEADER.length];
                System.arraycopy(audioData, 0, header, 0, header.length);
                if (CommonUtils.isBytesEquals(CommonUtils.FLAC_HEADER, mask.decrypt(header), 0)) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mask;
    }

    public static void main(String[] args) {
        System.out.println(0b01000110 ^ 0x00ff);
    }
}
