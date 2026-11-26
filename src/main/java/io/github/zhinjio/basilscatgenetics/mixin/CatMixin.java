package io.github.zhinjio.basilscatgenetics.mixin;

import io.github.zhinjio.basilscatgenetics.Cats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public abstract class CatMixin {
	/** Only a fertile female and a fertile male can breed. */
	@Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
	private void bcg$canMate(Animal partner, CallbackInfoReturnable<Boolean> cir) {
		if (partner instanceof Cat other && !Cats.canBreed((Cat) (Object) this, other)) {
			cir.setReturnValue(false);
		}
	}

	/** The kitten's genes come from its parents, and its coat from its genes. */
	@Inject(method = "getBreedOffspring", at = @At("RETURN"))
	private void bcg$offspring(ServerLevel level, AgeableMob partner, CallbackInfoReturnable<Cat> cir) {
		Cat baby = cir.getReturnValue();
		if (baby != null && partner instanceof Cat other) {
			Cats.inherit(level, (Cat) (Object) this, other, baby);
		}
	}
}
