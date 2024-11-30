package com.alignedcookie88.sugarlib.config.client_view;

import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.client_view.networking.ClientConfigViewNetworking;

public class PartiallyRemoteConfigView extends LocalConfigView {
    public PartiallyRemoteConfigView(Config config) {
        super(config);
    }

    @Override
    public void save() {
        ClientConfigViewNetworking.ConfigUpdate.start(config.getFullId());

        for (String id : optionMap.keySet()) {
            LCVOption<?> option = optionMap.get(id);

            if (option.hasChanged()) {
                option.sendRemotely();
            }
            option.resetChange();
        }

        ClientConfigViewNetworking.ConfigUpdate.end();
    }
}
