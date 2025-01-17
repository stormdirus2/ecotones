package supercoder79.ecotones.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.tree.TreeDecorator;
import net.minecraft.world.gen.tree.TreeDecoratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeDecoratorType.class)
public interface TreeDecoratorTypeAccessor {
    @Invoker(value = "<init>")
    static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(Codec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
