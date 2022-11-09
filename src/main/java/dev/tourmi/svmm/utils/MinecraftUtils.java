package dev.tourmi.svmm.utils;

import net.minecraft.world.level.block.state.BlockState;

public class MinecraftUtils {
    public static String getBlockName(BlockState bs) {
        String stateString = bs.toString();
        return stateString.substring(stateString.indexOf('{') + 1, stateString.indexOf('}'));
    }
}
