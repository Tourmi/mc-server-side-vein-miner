package dev.tourmi.svmm.utils;

import dev.tourmi.svmm.config.SVMMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Stream;

public class Utils3D {
    public static Stream<BlockPos> getSameBlocksConnectedToPos(String blockName, BlockPos pos, Level level) {
        return BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                .filter(bpos -> !bpos.equals(pos) && MinecraftUtils.getBlockName(level.getBlockState(bpos)).equals(blockName));
    }

    public static Stream<BlockPos> getGiantVeinBlocksConnectedToPos(BlockPos pos, Level level, List<? extends String> whitelist) {
        return BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                .filter(bpos -> !bpos.equals(pos) && whitelist.contains(MinecraftUtils.getBlockName(level.getBlockState(bpos))));
    }

    public static Collection<BlockPos> getVeinBlocks(BlockState s, BlockPos p, Level l, int limit) {
        HashSet<BlockPos> visitedBlocks = new HashSet<>();
        HashSet<BlockPos> newPositions = new HashSet<>();
        String blockName = MinecraftUtils.getBlockName(s);
        newPositions.add(p);

        while (!newPositions.isEmpty() && visitedBlocks.size() < limit) {
            BlockPos currPos = newPositions.iterator().next();

            if (visitedBlocks.size() + newPositions.size() < limit) {
                Stream<BlockPos> potentialBlocks = getSameBlocksConnectedToPos(blockName, currPos, l);
                newPositions.addAll(potentialBlocks.filter(pos -> !visitedBlocks.contains(pos)).map(BlockPos::immutable).toList());
            }

            visitedBlocks.add(currPos);
            newPositions.remove(currPos);
        }

        return visitedBlocks;
    }

    public static Collection<BlockPos> getGiantVeinBlocks(BlockPos p, Level l, int limit) {
        HashSet<BlockPos> visitedBlocks = new HashSet<>();
        HashSet<BlockPos> newPositions = new HashSet<>();
        newPositions.add(p);

        while (!newPositions.isEmpty() && visitedBlocks.size() < limit) {
            BlockPos currPos = newPositions.iterator().next();

            if (visitedBlocks.size() + newPositions.size() < limit) {
                Stream<BlockPos> potentialBlocks = getGiantVeinBlocksConnectedToPos(currPos, l, SVMMConfig.GIANT_VEIN_WHITELIST.get());
                newPositions.addAll(potentialBlocks.filter(pos -> !visitedBlocks.contains(pos)).map(BlockPos::immutable).toList());
            }

            visitedBlocks.add(currPos);
            newPositions.remove(currPos);
        }

        return visitedBlocks;
    }

    public static Collection<BlockPos> getTunnelBlocks(BlockPos p, BlockState s, Level l, Direction f, int width, int height, int depth) {
        return getTunnelBlocks(p, s, l, f, Direction.UP, Direction.DOWN, f.getClockWise(), f.getCounterClockWise(), width, height, depth);
    }

    private static Collection<BlockPos> getTunnelBlocks(BlockPos p, BlockState s, Level l, Direction blockFace, Direction upDir, Direction downDir, Direction leftDir, Direction rightDir, int width, int height, int depth) {
        HashSet<BlockPos> blocks = new HashSet<>();
        int upDist = (height - 1) / 2;
        int downDist = height / 2;
        int leftDist = (width - 1) / 2;
        int rightDist = width / 2;
        for (int i = 0; i < depth; i++) {
            BlockPos currDepth = p.relative(blockFace.getOpposite(), i);
            Stream<BlockPos> stream = BlockPos.betweenClosedStream(
                    currDepth.relative(leftDir, leftDist).relative(upDir, upDist),
                    currDepth.relative(rightDir, rightDist).relative(downDir, downDist));

            if (SVMMConfig.TUNNELING_SAME_TYPE.get()) {
                String blockName = MinecraftUtils.getBlockName(s);
                stream = stream.filter(pos -> MinecraftUtils.getBlockName(l.getBlockState(pos)).equals(blockName));
            } else {
                stream = stream.filter(pos -> SVMMConfig.TUNNELING_WHITELIST.get().contains(MinecraftUtils.getBlockName(l.getBlockState(pos))));
            }

            List<BlockPos> res = stream.map(BlockPos::immutable).toList();
            blocks.addAll(res);

            if (res.isEmpty()) break;
        }

        return blocks;

    }

    public static Collection<BlockPos> getVerticalTunnelBlocks(BlockPos p, BlockState s, Level l, Direction playerDir, Direction f, int width, int height, int depth) {
        Direction up = playerDir;
        Direction down = playerDir.getOpposite();
        Direction left = playerDir.getCounterClockWise();
        Direction right = playerDir.getClockWise();
        if (f.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            up = down;
            down = playerDir;
        }
        return getTunnelBlocks(p, s, l, f, up, down, left, right, width, height, depth);
    }
}
