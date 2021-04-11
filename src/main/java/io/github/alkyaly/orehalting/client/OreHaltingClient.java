package io.github.alkyaly.orehalting.client;

import io.github.alkyaly.orehalting.OreHalting;
import io.github.alkyaly.orehalting.gui.OreHalterScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class OreHaltingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(OreHalting.ORE_HALTER_SCREEN_HANDLER, OreHalterScreen::new);
    }
}
