package com.taikuus.luomuksia.client.geomodel;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.common.item.Wand;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WandModel extends GeoModel<Wand> {

    @Override
    public ResourceLocation getModelResource(Wand wand) {
        return RegistryNames.getRL("geo/model/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Wand wand) {
        return RegistryNames.getRL("textures/item/wand_tobereplaced.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Wand wand) {
        return RegistryNames.getRL("animations/item/wand.animation.json");
    }
}
