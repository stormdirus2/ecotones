package supercoder79.ecotones.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class FarmlandPatchFeature extends Feature<DefaultFeatureConfig> {
    public FarmlandPatchFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos, DefaultFeatureConfig config) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for (int i = 0; i < random.nextInt(4) + 8; i++) {
            //pick random position
            mutable.set(pos, random.nextInt(8) - random.nextInt(8), 0, random.nextInt(8) - random.nextInt(8));
            int y = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, mutable.getX(), mutable.getZ()) - 1;
            mutable.setY(y);

            // test for grass and then continue
            if (world.getBlockState(mutable) == Blocks.GRASS_BLOCK.getDefaultState()) {
                // ensure there is a block under the water
                if (!world.getBlockState(mutable.down()).isOpaque()) {
                    continue;
                }

                boolean canSpawn = true;

                // check surrounding for non-opaque blocks
                BlockPos origin = mutable.toImmutable();
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    mutable.set(origin, direction);
                    if (!world.getBlockState(mutable).isOpaque()) {
                        if (!world.getFluidState(mutable).isIn(FluidTags.WATER)) {
                            canSpawn = false;
                        }

                        break;
                    }
                }

                if (canSpawn) {
                    mutable.set(origin);
                    if (random.nextInt(3) == 0) {
                        world.setBlockState(mutable, Blocks.WATER.getDefaultState(), 3);
                    } else {
                        world.setBlockState(mutable, Blocks.FARMLAND.getDefaultState().with(Properties.MOISTURE, 7), 3);
                        world.setBlockState(mutable.up(), Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, random.nextInt(5)), 3);
                    }
                }
            }
        }

        return false;
    }
}
