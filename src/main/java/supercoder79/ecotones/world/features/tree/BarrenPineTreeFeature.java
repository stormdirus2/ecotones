package supercoder79.ecotones.world.features.tree;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import supercoder79.ecotones.world.features.config.SimpleTreeFeatureConfig;
import supercoder79.ecotones.world.gen.EcotonesChunkGenerator;

import java.util.Random;

public class BarrenPineTreeFeature extends Feature<SimpleTreeFeatureConfig> {
    public BarrenPineTreeFeature(Codec<SimpleTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<SimpleTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getPos();
        Random random = context.getRandom();
        SimpleTreeFeatureConfig config = context.getConfig();
        ChunkGenerator generator = context.getGenerator();

        if (world.getBlockState(pos.down()) != Blocks.GRASS_BLOCK.getDefaultState()) {
            return false;
        }

        world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState(), 3);

        int height = 3 + random.nextInt(3);
        double soilQuality = 0;
        if (generator instanceof EcotonesChunkGenerator) {
            soilQuality = ((EcotonesChunkGenerator)generator).getSoilQualityAt(pos.getX(), pos.getZ());
        }

        height += (int)(soilQuality * 3.5);

        int leafStart = (int) Math.max(1, height * 0.25);

        for (int y = 0; y < height; y++) {
            BlockPos log = pos.up(y);
            if (world.getBlockState(log).isOpaque()) {
                return true;
            }

            world.setBlockState(log,config.woodState, 3);

            // Place leaves after the trunk are ends
            if (y > leafStart) {

                // 1 - [2 - 4]
                int count = 1 + random.nextInt(3 + (int)(soilQuality * 2.5));
                for (int i = 0; i < count; i++) {
                    Direction direction = Direction.Type.HORIZONTAL.random(random);
                    BlockPos center = log.offset(direction);
                    placeLeaves(world, random, center, 0.6 + (soilQuality / 2.5), config);
                }
            }

            // Ensures top of tree is always leaves
            if (height - 2 < y) {
                placeLeaves(world, random, log, 1.0, config);
            }
        }

        return true;
    }

    private static void placeLeaves(StructureWorldAccess world, Random random, BlockPos pos, double chance, SimpleTreeFeatureConfig config) {
        if (world.getBlockState(pos).isAir()) {
            world.setBlockState(pos, config.leafState, 3);
        }

        for (Direction dir : Direction.values()) {
            if (random.nextDouble() < chance) {
                BlockPos leaf = pos.offset(dir);
                if (world.getBlockState(leaf).isAir()) {
                    world.setBlockState(leaf, config.leafState, 3);
                }
            }
        }
    }
}