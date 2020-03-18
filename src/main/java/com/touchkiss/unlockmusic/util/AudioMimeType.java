package com.touchkiss.unlockmusic.util;

/**
 * Created on 2020/03/16 14:57
 *
 * @author Touchkiss
 */
public enum AudioMimeType {
    mp3("audio/mpeg"),
    flac("audio/flac"),
    m4a("audio/mp4"),
    ogg("audio/ogg");
    private String value;

    AudioMimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
