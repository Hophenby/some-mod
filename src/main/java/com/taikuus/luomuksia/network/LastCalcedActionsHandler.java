package com.taikuus.luomuksia.network;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.client.gui.WandInfoHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Packet direction: Server -> Client
public class LastCalcedActionsHandler {
    public static void handleData(final LastCalcedActions packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level clientWorld = Minecraft.getInstance().level;
            if (clientWorld == null) {
                return;
            }
            WandInfoHUD.setLastCast(packet.actions());
        });
    }

    public record LastCalcedActions(List<AbstractWandAction> actions) implements CustomPacketPayload {
        public static final Type<LastCalcedActions> TYPE = new Type<>(RegistryNames.getRL("last_calced_actions"));

        public static final StreamCodec<FriendlyByteBuf, LastCalcedActions> STREAM = StreamCodec.of(
                (buf, packet) -> buf.writeCollection(packet.actions(), AbstractWandAction.STREAM),
                (buf) -> {
                    int size = buf.readVarInt();
                    List<AbstractWandAction> actions = new CopyOnWriteArrayList<>();
                    for (int i = 0; i < size; i++) {
                        actions.add(AbstractWandAction.STREAM.decode(buf));
                    }
                    return new LastCalcedActions(actions);
                }
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
