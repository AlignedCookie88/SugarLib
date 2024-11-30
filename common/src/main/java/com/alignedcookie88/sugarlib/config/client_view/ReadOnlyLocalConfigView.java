package com.alignedcookie88.sugarlib.config.client_view;

import com.alignedcookie88.sugarlib.config.Config;

public class ReadOnlyLocalConfigView extends LocalConfigView {

    public ReadOnlyLocalConfigView(Config config) {
        super(config);
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public void save() {

    }
}
