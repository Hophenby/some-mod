package com.taikuus.luomuksia.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://github.com/MisionThi/NBTtooltips/blob/main/src/main/java/net/mision_thi/nbttooltips/tooltips/TooltipChanger.java
@OnlyIn(Dist.CLIENT)
public class ComponentTooltip {
    private static final Minecraft client = Minecraft.getInstance();
    public static void modifyTooltip(ItemStack itemStack, @Nullable Player player, List<Component> list, TooltipFlag flags, Item.TooltipContext context) {
        // Initialise the needed variables
        ArrayList<Component> temp = new ArrayList<Component>();

        itemStack.getComponents().forEach(
                (dcPair) -> {
                    //list.add(new TextComponent("§7" + key + ": " + value));
                    MutableComponent component = Component.empty();
                    component.append(Component.literal(dcPair.type().toString()).withStyle(resLocColor));
                    component.append(Component.literal("=").withStyle(symbolColor));
                    component.append(parseToChatFormat(dcPair.value().toString()));
                    temp.add(component);
                }
        );
        list.addAll(temp);
    }
    private static final ChatFormatting stringColor = ChatFormatting.GREEN;
    private static final ChatFormatting symbolColor = ChatFormatting.WHITE;
    private static final ChatFormatting numericOrBoolColor = ChatFormatting.GOLD;
    private static final ChatFormatting fieldColor = ChatFormatting.AQUA;
    private static final ChatFormatting resLocColor = ChatFormatting.GRAY;

    /**
     * Parses a string to a chat format.
     * <a href="https://github.com/MisionThi/NBTtooltips/blob/0.1.4-1.20.x/src/main/java/net/mision_thi/nbttooltips/tooltips/TooltipChanger.java">Original Codes</a>
     * @author MisionThi
     */
    private static Component parseToChatFormat(String s) {
        Pattern p = Pattern.compile("[{}=\"\\[\\],']", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        // Create new literalText, which we will be adding to the list.
        MutableComponent mutableText = Component.empty();


        int lastIndex = 0;
        Boolean singleQuotationMark = Boolean.FALSE;
        String lastString = "";


        while (m.find()) {
            //first match means that there is some field name or value before the first symbol
            if (lastIndex == 0){
                mutableText.append(Component.literal(s.substring(lastIndex, m.start())).withStyle(fieldColor));
            }
            if (s.charAt(m.start()) == '\'') {
                if (singleQuotationMark.equals(Boolean.FALSE)) { // If false color only the quotation mark
                    mutableText.append(Component.literal(String.valueOf(s.charAt(m.start()))).withStyle(symbolColor));
                    singleQuotationMark = Boolean.TRUE;
                } else {
                    mutableText.append(Component.literal(s.substring(lastIndex + 1, m.start())).withStyle(stringColor));
                    mutableText.append(Component.literal(String.valueOf(s.charAt(m.start()))).withStyle(symbolColor));
                    singleQuotationMark = Boolean.FALSE;
                }
                lastString = s.substring(lastIndex, m.start());
                lastIndex = m.start();
            }
            if (singleQuotationMark.equals(Boolean.FALSE)) {
                if (s.charAt(m.start()) == '{' || s.charAt(m.start()) == '[') {
                    mutableText.append(Component.literal(String.valueOf(s.charAt(m.start()))).withStyle(symbolColor));
                    lastString = String.valueOf(s.charAt(m.start()));
                    lastIndex = m.start();
                }
                if (s.charAt(m.start()) == '}' || s.charAt(m.start()) == ']' || s.charAt(m.start()) == ',') {
                    mutableText.append(Component.literal(s.substring(lastIndex + 1, m.start())).withStyle(numericOrBoolColor));
                    mutableText.append(Component.literal(String.valueOf(s.charAt(m.start())))).withStyle(symbolColor);
                    if (s.charAt(m.start()) == ',') {
                        mutableText.append(Component.literal(" ").withStyle(symbolColor));
                    }
                    lastString = String.valueOf(s.charAt(m.start()));
                    lastIndex = m.start();
                }
                if (s.charAt(m.start()) == '=') {
                    if (!lastString.equals("\"")) {
                        mutableText.append(Component.literal(s.substring(lastIndex + 1, m.start())).withStyle(fieldColor));
                        mutableText.append(Component.literal(String.valueOf(s.charAt(m.start()))).withStyle(symbolColor));
                        lastString = String.valueOf(s.charAt(m.start()));
                        lastIndex = m.start();
                    }
                }
                if (s.charAt(m.start()) == '"') {
                    if (lastString.equals("\"")) {
                        mutableText.append(Component.literal(s.substring(lastIndex + 1, m.start())).withStyle(stringColor));
                        lastString = s.substring(lastIndex, m.start());
                        lastIndex = m.start();
                    } else {
                        mutableText.append(Component.literal(String.valueOf(s.charAt(m.start()))).withStyle(symbolColor));
                        lastString = String.valueOf(s.charAt(m.start()));
                        lastIndex = m.start();
                    }
                }
            }
            if (s.charAt(m.start()) == '}' || s.charAt(m.start()) == ']' || s.charAt(m.start()) == ',') { // 2).

                if (lastString.equals("'")) { // 3).
                    mutableText.append(Component.literal(s.substring(lastIndex+1,m.start())).withStyle(stringColor));
                    lastIndex = m.start();
                }

            }
        }
        // No match means all the string is a value
        if (lastIndex == 0){
            mutableText.append(Component.literal(s).withStyle(numericOrBoolColor));
        }
        return mutableText;

    }

}
