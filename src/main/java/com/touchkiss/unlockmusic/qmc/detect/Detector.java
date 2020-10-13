package com.touchkiss.unlockmusic.qmc.detect;

import com.touchkiss.unlockmusic.qmc.QmcMask;

/**
 * Created on 2020/03/16 15:17
 *
 * @author Touchkiss
 */
@FunctionalInterface
public interface Detector {
    QmcMask handler(byte[] audioData) throws Exception;
}
