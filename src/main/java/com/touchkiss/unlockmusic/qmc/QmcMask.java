package com.touchkiss.unlockmusic.qmc;

import com.touchkiss.unlockmusic.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020/03/16 15:11
 *
 * @author Touchkiss
 */
public class QmcMask {
    private short[] matrix128;
    private short[] matrix58;
    private short super58A;
    private short super58B;

    public QmcMask(short[] matrix, Short superA, Short superB) throws Exception {
        if (superA == null || superB == null) {
            this.matrix128 = matrix;
            this.generateMask58from128();
        } else {
            this.matrix58 = matrix;
            this.super58A = superA;
            this.super58B = superB;
            this.generateMask128from58();
        }
    }

    void generateMask58from128() throws Exception {
        if (this.matrix128.length != 128) {
            throw new Exception("incorrect mask128 length");
        }
        short superA = this.matrix128[0], superB = this.matrix128[8];
        List<Short> matrix58 = new ArrayList<>();
        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            int lenStart = 16 * rowIdx;
            int lenRightStart = 120 - lenStart;
            if (this.matrix128[lenStart] != superA || this.matrix128[lenStart + 8] != superB) {
                System.out.println("decode mask-128 to mask-58 failed");
            }
            short[] rowLeft = new short[7];
            System.arraycopy(this.matrix128, lenStart + 1, rowLeft, 0, 7);
            short[] rowRight = new short[7];
            for (int i = 7; i > 0; i--) {
                rowRight[7 - i] = this.matrix128[lenRightStart + i];
            }
            if (CommonUtils.isArrayEquals(rowLeft, rowRight)) {
                for (short i : rowLeft) {
                    matrix58.add(i);
                }
            } else {
                System.out.println("decode mask-128 to mask-58 failed");
            }
        }
        this.matrix58 = CommonUtils.listToIntArray(matrix58);
        this.super58A = superA;
        this.super58B = superB;
    }

    void generateMask128from58() throws Exception {
        if (this.matrix58.length != 56) {
            throw new Exception("incorrect mask58 matrix length");
        }
        List<Short> matrix128 = new ArrayList<>();
        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            matrix128.add(this.super58A);
            for (int i = 7 * rowIdx; i < 7 * rowIdx + 7; i++) {
                matrix128.add(this.matrix58[i]);
            }
            matrix128.add(this.super58B);
            for (int i = 56 - 7 * rowIdx - 1; i >= 56 - 7 - 7 * rowIdx; i--) {
                matrix128.add(this.matrix58[i]);
            }
        }
        this.matrix128 = CommonUtils.listToIntArray(matrix128);
    }

    public byte[] decrypt(byte[] data) {
        int dataLength = data.length;
        byte[] dst = new byte[dataLength];
        System.arraycopy(data, 0, dst, 0, dataLength);
        int index = -1;
        int maskIdx = -1;
        for (int cur = 0; cur < dataLength; cur++) {
            index++;
            maskIdx++;
            if (index == 0x8000 || (index > 0x8000 && (index + 1) % 0x8000 == 0)) {
                index++;
                maskIdx++;
            }
            if (maskIdx >= 128) {
                maskIdx -= 128;
            }
            dst[cur] ^= this.matrix128[maskIdx];
        }
        return dst;
    }
}
