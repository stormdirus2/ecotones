package supercoder79.ecotones.api;

import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import supercoder79.ecotones.Ecotones;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public enum Climate {
    HOT_DESERT,
    HOT_VERY_DRY,
    HOT_DRY,
    HOT_MODERATE,
    HOT_MILD,
    HOT_HUMID,
    HOT_VERY_HUMID,
    HOT_RAINFOREST,

    WARM_DESERT,
    WARM_VERY_DRY,
    WARM_DRY,
    WARM_MODERATE,
    WARM_MILD,
    WARM_HUMID,
    WARM_VERY_HUMID,
    WARM_RAINFOREST,

    COOL_DESERT,
    COOL_VERY_DRY,
    COOL_DRY,
    COOL_MODERATE,
    COOL_MILD,
    COOL_HUMID,
    COOL_VERY_HUMID,
    COOL_RAINFOREST,

    COLD_DESERT,
    COLD_VERY_DRY,
    COLD_DRY,
    COLD_MODERATE,
    COLD_MILD,
    COLD_HUMID,
    COLD_VERY_HUMID,
    COLD_RAINFOREST;

    private final List<Entry> biomeEntries = Lists.newArrayList();
    private double weightTotal;

    Climate() {

    }

    public Integer choose(LayerRandomnessSource rand) {
        if (this.biomeEntries.size() == 0) {
            throw new UnsupportedOperationException("No biomes registered for climate " + this + "!!! This is a problem!");
        }

        double randVal = target(rand);
        int i = -1;

        while (randVal >= 0) {
            ++i;
            randVal -= this.biomeEntries.get(i).weight;
        }

        Biome biome = Ecotones.REGISTRY.get(this.biomeEntries.get(i).biome);

        int id = Ecotones.REGISTRY.getRawId(biome);
//        System.out.println(id);

        return id;
    }

    public void add(Biome biome, double weight) {
        this.biomeEntries.add(new Entry(biome, weight));
        this.weightTotal += weight;
    }

    private double target(LayerRandomnessSource random) {
        return (double) random.nextInt(Integer.MAX_VALUE) * this.weightTotal / Integer.MAX_VALUE;
    }

    private static class Entry {
        private final RegistryKey<Biome> biome;
        private final double weight;
        private Entry(Biome biome, double weight) {
            // Attempt from builtin
            Optional<RegistryKey<Biome>> optional = BuiltinRegistries.BIOME.getKey(biome);

            // Mod compat mode: use dynamic registry
            this.biome = optional.orElseGet(() -> Ecotones.REGISTRY.getKey(biome).get());
            this.weight = weight;

//            System.out.println(this.biome);
        }
    }
}
