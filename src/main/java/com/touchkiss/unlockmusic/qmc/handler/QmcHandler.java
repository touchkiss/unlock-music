package com.touchkiss.unlockmusic.qmc.handler;

import com.touchkiss.unlockmusic.qmc.detect.Detector;

/**
 * Created on 2020/03/16 15:09
 *
 * @author Touchkiss
 */
public class QmcHandler {
    private Detector handler;
    private String ext;
    private boolean detect;

    public QmcHandler(Detector handler, String ext, boolean detect) {
        this.handler = handler;
        this.ext = ext;
        this.detect = detect;
    }

    public Detector getDetector() {
        return handler;
    }

    public void setDetector(Detector handler) {
        this.handler = handler;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public boolean isDetect() {
        return detect;
    }

    public void setDetect(boolean detect) {
        this.detect = detect;
    }
}
