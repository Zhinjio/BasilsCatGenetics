package io.github.zhinjio.basilscatgenetics.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatAccessor {
	@Invoker("setVariant")
	void bcg$setVariant(Holder<CatVariant> variant);
}
