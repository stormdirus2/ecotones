package supercoder79.ecotones.world.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import supercoder79.ecotones.api.TreeGenerationConfig;
import supercoder79.ecotones.util.DataPos;
import supercoder79.ecotones.world.gen.EcotonesChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class AnalyticTreePlacementDecorator extends Decorator<TreeGenerationConfig.DecorationData> {
    public AnalyticTreePlacementDecorator(Codec<TreeGenerationConfig.DecorationData> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, TreeGenerationConfig.DecorationData config, BlockPos pos) {
        double soilQuality = 0.0; // default for if the chunk generator is not ours
        if (context.generator instanceof EcotonesChunkGenerator) {
            soilQuality = ((EcotonesChunkGenerator)context.generator).getSoilQualityAt(pos.getX() + 8, pos.getZ() + 8);
        }

        //get the height from minSize to minSize + noiseCoefficient (can be more because of noise map)
        int maxHeight = (int) (config.minSize + Math.max(soilQuality * config.noiseCoefficient, 0));

        int targetCount = (int) config.targetCount;
        if (random.nextDouble() < (config.targetCount - targetCount)) {
            targetCount++;
        }

        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();
            int y = context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);

            //make trees smaller as the height increases
            int maxFinal = maxHeight;
            if (y > 80) {
                maxFinal = Math.max(maxHeight, maxHeight - ((y - 80) / 15));
            }

            int solidAround = 0;
            int solidBase = 0;
            for (int x1 = -1; x1 <= 1; x1++) {
                for (int z1 = -1; z1 <= 1; z1++) {
                    if (context.getBlockState(new BlockPos(x + x1, y - 1, z + z1)).getMaterial().isSolid()) {
                        solidBase++;
                    }

                    for (int y1 = 0; y1 <= 1; y1++) {
                        if (context.getBlockState(new BlockPos(x + x1, y + y1, z + z1)).getMaterial().isSolid()) {
                            solidAround++;
                        }
                    }
                }
            }

            if (solidAround > 1 || solidBase < 9) {
                continue;
            }

            positions.add(new DataPos(x, y, z).setMaxHeight(maxFinal + random.nextInt(4)));
        }

        return positions.stream();
    }

    // Desmos: x^{3}+2.75x-1.5
    private double qualityToDensity(double q) {
        return (q * q * q) + (2.75 * q) - 1.5;
    }
}
