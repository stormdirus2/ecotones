package supercoder79.ecotones.world.features.tree;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import supercoder79.ecotones.util.Shapes;
import supercoder79.ecotones.world.features.config.SimpleTreeFeatureConfig;

import java.util.Random;

public class AspenTreeFeature extends Feature<SimpleTreeFeatureConfig> {
    public AspenTreeFeature(Codec<SimpleTreeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos, SimpleTreeFeatureConfig config) {
        if (world.getBlockState(pos.down()) != Blocks.GRASS_BLOCK.getDefaultState()) return false;

        double maxRadius = 2 + ((random.nextDouble() - 0.5) * 0.2);
        int leafDistance = random.nextInt(4) + 3;

        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int y = 0; y < 8; y++) {
            world.setBlockState(mutable, config.woodState, 0);
            //add branch blocks
            if (maxRadius * radius(y / 7.f) > 2.1) {
                Direction.Axis axis = getAxis(random);
                world.setBlockState(mutable.offset(getDirection(axis, random)).up(leafDistance), config.woodState.with(Properties.AXIS, axis), 0);
            }

            mutable.move(Direction.UP);
        }

        if (config.leafState.isAir()) {
            return true;
        }

        mutable = pos.mutableCopy();
        mutable.move(Direction.UP, leafDistance);

        for (int y = 0; y < 8; y++) {
            Shapes.circle(mutable.mutableCopy(), maxRadius * (radius(y / 7.f)), leafPos -> {
                if (AbstractTreeFeature.isAirOrLeaves(world, leafPos)) {
                    world.setBlockState(leafPos, config.leafState, 0);
                }
            });
            mutable.move(Direction.UP);
        }

        return true;
    }

    private double radius(double x) {
        return -Math.pow(((1.4 * x) - 0.3), 2) + 1.2;
    }

    private Direction.Axis getAxis(Random random) {
        return random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
    }

    private Direction getDirection(Direction.Axis axis, Random random) {
        if (axis == Direction.Axis.X) {
            return random.nextBoolean() ? Direction.EAST : Direction.WEST;
        } else {
            return random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
        }
    }
}
