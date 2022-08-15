package com.example.fastdownloader.sample;

import org.greenrobot.eventbus.EventBus;

public class DownloadHolder {
    private static EventBus eventBus;

    public static EventBus getInstnace() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    private DownloadHolder() {
    }
}
