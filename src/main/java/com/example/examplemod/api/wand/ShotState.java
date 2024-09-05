package com.example.examplemod.api.wand;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShotState {
    private final List<Entity> projList = new ArrayList<>();
    private final int numFirstDraw;
    private final Player player;
    private final Level world;

    public ShotState(int numFirstDraw, Level world, Player player) {
        this.numFirstDraw = numFirstDraw;
        this.player = player;
        this.world = world;
    }
    public int getNumFirstDraw() {
        return numFirstDraw;
    }

    public List<Entity> getProjList() {
        return projList;
    }
}
