package io.github.zhinjio.basilscatgenetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zhinjio.basilscatgenetics.genetics.Genotype;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

/**
 * What the mod stores on each cat (saved with the cat). Genes are kept as their text form so a saved world stays
 * readable and survives changes to how the code represents them.
 *
 * @param genes      Genotype.encode()
 * @param tested     a gene test kit has been used on this cat
 * @param sterile    cannot breed: XXY males, Hellcat, Withercat
 */
public record CatGenes(String genes, boolean tested, boolean sterile) {
	public static final Codec<CatGenes> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.fieldOf("genes").forGetter(CatGenes::genes),
			Codec.BOOL.optionalFieldOf("tested", false).forGetter(CatGenes::tested),
			Codec.BOOL.optionalFieldOf("sterile", false).forGetter(CatGenes::sterile)
	).apply(i, CatGenes::new));

	public static final AttachmentType<CatGenes> TYPE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(BasilsCatGenetics.MOD_ID, "genes"),
			b -> b.persistent(CODEC));

	public static CatGenes of(Genotype g) {
		return new CatGenes(g.encode(), false, g.sterile());
	}

	public Genotype genotype() {
		return Genotype.decode(genes);
	}

	public CatGenes withTested() {
		return new CatGenes(genes, true, sterile);
	}

	public static void init() {
		// Class load registers TYPE.
	}
}
