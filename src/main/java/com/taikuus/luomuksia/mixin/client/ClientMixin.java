package com.taikuus.luomuksia.mixin.client;

import com.taikuus.luomuksia.api.client.lighter.ProjLightUtils;
import com.taikuus.luomuksia.utils.IUseCooldownSetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Minecraft.class)
public abstract class ClientMixin implements IUseCooldownSetter {
    @Shadow
    private int rightClickDelay;
    public ClientMixin() {
    }
    @Override
    public void setItemUseCooldown(int cooldown) {
        this.rightClickDelay = cooldown;
    }
    @Override
    public int debugGetRightClickDelay() {
        return this.rightClickDelay;
    }
    @Inject(method = "updateLevelInEngines", at = @At("HEAD"))
    private void onSetWorld(ClientLevel world, CallbackInfo ci) {
        ProjLightUtils.clearLightSources();
    }

}
