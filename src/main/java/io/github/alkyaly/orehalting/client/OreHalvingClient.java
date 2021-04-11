package io.github.alkyaly.orehalting.client;

import io.github.alkyaly.orehalting.OreHalving;
import io.github.alkyaly.orehalting.gui.TheOneThatHalvesScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class OreHalvingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(OreHalving.THE_ONE_THAT_HALVES_SCREEN_HANDLER, TheOneThatHalvesScreen::new);
    }
}
