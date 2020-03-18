package com.touchkiss.unlockmusic.qmc.detect;

import com.touchkiss.unlockmusic.qmc.QmcMask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2020/03/16 15:18
 *
 * @author Touchkiss
 */
public class QmcMaskDetectMgg implements Detector {
    private final static short[] QMOggConstHeader = new short[]{
            0x004F, 0x0067, 0x0067, 0x0053, 0x0000, 0x0002, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
            0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0001, 0x001E, 0x0001, 0x0076, 0x006F, 0x0072,
            0x0062, 0x0069, 0x0073, 0x0000, 0x0000, 0x0000, 0x0000, 0x0002, 0x0044, 0x00AC, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
            0x0000, 0x00EE, 0x0002, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x00B8, 0x0001, 0x004F, 0x0067, 0x0067, 0x0053, 0x0000, 0x0000,
            0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0001, 0x0000, 0x0000, 0x0000,
            0x0000, 0x0000, 0x0000, 0x0000, 0x0010, 0x0000, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x00FF,
            0x00FF, 0x00FF, 0x00FF, 0x00FF, 0x0000, 0x0003, 0x0076, 0x006F, 0x0072, 0x0062, 0x0069, 0x0073, 0x002C, 0x0000, 0x0000, 0x0000,
            0x0058, 0x0069, 0x0070, 0x0068, 0x002E, 0x004F, 0x0072, 0x0067, 0x0020, 0x006C, 0x0069, 0x0062, 0x0056, 0x006F, 0x0072, 0x0062,
            0x0069, 0x0073, 0x0020, 0x0049, 0x0020, 0x0032, 0x0030, 0x0031, 0x0035, 0x0030, 0x0031, 0x0030, 0x0035, 0x0020, 0x0028, 0x00E2,
            0x009B, 0x0084, 0x00E2, 0x009B, 0x0084, 0x00E2, 0x009B, 0x0084, 0x00E2, 0x009B, 0x0084, 0x0029, 0x0000, 0x0000, 0x0000, 0x0000,
            0x0000, 0x0000, 0x0000, 0x0000, 0x0054, 0x0049, 0x0054, 0x004C, 0x0045, 0x003D
    };
    private final static short[] QMOggConstHeaderConfidence = new short[]{
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 0,
            0, 0, 9, 9, 9, 9, 0, 0, 0, 0, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 6, 3, 3, 3, 3, 6, 6, 6, 6,
            3, 3, 3, 3, 6, 6, 6, 6, 6, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 9, 9, 9, 9,
            0, 0, 0, 0, 6, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 1, 9, 9,
            0, 1, 9, 9, 9, 9, 9, 9, 9, 9
    };

    @Override
    public QmcMask handler(byte[] audioData) throws Exception {
        if (audioData.length < QMOggConstHeader.length) {
            return null;
        }
        MatrixConfidence[] matrixConfidences = new MatrixConfidence[58];
        for (int i = 0; i < matrixConfidences.length; i++) {
            matrixConfidences[i] = new MatrixConfidence();
        }
        for (int idx128 = 0; idx128 < QMOggConstHeader.length; idx128++) {
            if (QMOggConstHeaderConfidence[idx128] == 0) {
                continue;
            }
            int idx58 = getMask58Index(idx128);
            Short mask = (short) ((short) (audioData[idx128] ^ QMOggConstHeader[idx128]) & 0xff);
            short confidence = QMOggConstHeaderConfidence[idx128];
            if (matrixConfidences[idx58].getConfidence().containsKey(mask)) {
                short aShort = matrixConfidences[idx58].getConfidence().get(mask);
                aShort += confidence;
                matrixConfidences[idx58].getConfidence().put(mask, aShort);
            } else {
                matrixConfidences[idx58].getConfidence().put(mask, confidence);
            }
        }
        short[] matrix = new short[56];
        Short superA = null, superB = null;
        try {
            for (int i = 0; i < 56; i++) {
                matrix[i] = getMaskConfidenceResult(matrixConfidences[i].getConfidence());
                superA = getMaskConfidenceResult(matrixConfidences[56].getConfidence());
                superB = getMaskConfidenceResult(matrixConfidences[57].getConfidence());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new QmcMask(matrix, superA, superB);
    }

    public static class MatrixConfidence {
        private Map<Short, Short> confidence = new HashMap<>();

        public Map<Short, Short> getConfidence() {
            return confidence;
        }

        public void setConfidence(Map<Short, Short> confidence) {
            this.confidence = confidence;
        }
    }

    int getMask58Index(int idx128) {
        if (idx128 > 127) {
            idx128 = idx128 % 128;
        }
        int col = idx128 % 16;
        int row = (idx128 - col) / 16;
        switch (col) {
            case 0:
                row = 8;
                col = 0;
                break;
            case 8:
                row = 8;
                col = 1;
                break;
            default:
                if (col > 7) {
                    row = 7 - row;
                    col = 15 - col;
                } else {
                    col -= 1;
                }
        }
        return row * 7 + col;
    }

    short getMaskConfidenceResult(Map<Short, Short> confidence) throws Exception {
        if (confidence.size() == 0) {
            throw new Exception("can not match at least one key");
        }
        short result = 0, conf = 0;
        for (Map.Entry<Short, Short> integerIntegerEntry : confidence.entrySet()) {
            if (integerIntegerEntry.getValue() > conf) {
                result = integerIntegerEntry.getKey();
                conf = integerIntegerEntry.getValue();
            }
        }
        return result;
    }
}
