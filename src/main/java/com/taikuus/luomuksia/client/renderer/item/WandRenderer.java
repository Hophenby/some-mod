package com.taikuus.luomuksia.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.taikuus.luomuksia.client.geomodel.WandModel;
import com.taikuus.luomuksia.common.item.Wand;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WandRenderer extends GeoItemRenderer<Wand> {
    public WandRenderer() {
        super(new WandModel());
    }

}
