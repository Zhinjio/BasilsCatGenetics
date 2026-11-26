package io.github.zhinjio.basilscatgenetics;

import io.github.zhinjio.basilscatgenetics.genetics.CoatCatalog;
import io.github.zhinjio.basilscatgenetics.genetics.Genotype;
import io.github.zhinjio.basilscatgenetics.genetics.Phenotype;
import io.github.zhinjio.basilscatgenetics.genetics.WildCats;
import io.github.zhinjio.basilscatgenetics.mixin.CatAccessor;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.jspecify.annotations.Nullable;

/** The mod's rules applied to real cats. */
public final class Cats {
	private Cats() {
	}

	private static final Random RNG = new Random();

	/**
	 * The cat's genes, assigning wild genes the first time a vanilla-coated cat is seen. Null for cats the mod does
	 * not handle yet (other mods' coats, such as Bray's, until their genes are added).
	 */
	public static @Nullable CatGenes genes(Cat cat) {
		CatGenes g = cat.getAttached(CatGenes.TYPE);
		if (g != null) {
			return g;
		}
		Optional<ResourceKey<CatVariant>> key = cat.getVariant().unwrapKey();
		if (key.isEmpty() || !key.get().identifier().getNamespace().equals("minecraft")) {
			return null;
		}
		String spec = WildCats.VANILLA.get(key.get().identifier().getPath());
		if (spec == null) {
			return null;
		}
		g = CatGenes.of(WildCats.roll(spec, RNG));
		cat.setAttached(CatGenes.TYPE, g);
		return g;
	}

	/** Breeding needs one fertile female and one fertile male. Cats without genes keep vanilla rules. */
	public static boolean canBreed(Cat a, Cat b) {
		CatGenes ga = genes(a), gb = genes(b);
		if (ga == null || gb == null) {
			return true;
		}
		return !ga.sterile() && !gb.sterile() && ga.genotype().male() != gb.genotype().male();
	}

	public static void inherit(ServerLevel level, Cat a, Cat b, Cat baby) {
		CatGenes ga = genes(a), gb = genes(b);
		if (ga == null || gb == null) {
			return;
		}
		Genotype x = ga.genotype(), y = gb.genotype();
		if (x.male() == y.male()) {
			return;   // canMate should have stopped this
		}
		Genotype kitten = x.male() ? Genotype.breed(y, x, RNG) : Genotype.breed(x, y, RNG);
		baby.setAttached(CatGenes.TYPE, CatGenes.of(kitten));
		variantFor(level, kitten).ifPresent(v -> ((CatAccessor) baby).bcg$setVariant(v));
	}

	/** The cat_variant that shows these genes: a vanilla one if the game has that coat, otherwise ours. */
	public static Optional<? extends Holder<CatVariant>> variantFor(ServerLevel level, Genotype g) {
		String coat = Phenotype.textureName(g);
		String vanilla = CoatCatalog.VANILLA_BY_COAT.get(coat);
		Identifier id = vanilla != null
				? Identifier.withDefaultNamespace(vanilla)
				: Identifier.fromNamespaceAndPath(BasilsCatGenetics.MOD_ID, coat.substring(4));
		var found = level.registryAccess().lookupOrThrow(Registries.CAT_VARIANT).get(ResourceKey.create(Registries.CAT_VARIANT, id));
		if (found.isEmpty()) {
			BasilsCatGenetics.LOG.warn("No cat_variant {} for {}", id, g);
		}
		return found;
	}
}
