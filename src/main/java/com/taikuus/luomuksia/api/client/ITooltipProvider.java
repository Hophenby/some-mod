package com.taikuus.luomuksia.api.client;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface ITooltipProvider {
    void getTooltip(List<Component> tooltip);
}
