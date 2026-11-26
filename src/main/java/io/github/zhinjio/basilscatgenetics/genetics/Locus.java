package io.github.zhinjio.basilscatgenetics.genetics;

/**
 * The autosomal loci from GeneSheet.csv. Alleles are listed most dominant first; an allele is stored as its index.
 * O (red) is X-linked and lives in {@link Genotype} separately.
 */
public enum Locus {
	B("B", "b", "b1"),   // black, chocolate, cinnamon
	D("D", "d"),         // d/d dilute
	DM("Dm", "dm"),      // dm/dm: warmer tint on dilute cats
	A("A", "a"),         // A/? tabby, a/a solid
	M("T", "be"),        // be/be spotted instead of striped
	I("I", "i"),         // i/i silver
	WB("Wb", "wb"),      // wb/wb golden (on tabbies)
	W("W", "Ws", "w"),   // W/? white; Ws/Ws high white, Ws/w low white
	C("C", "c"),         // c/c colorpoint
	BT("Bt", "bt"),      // bt/bt bobtail
	H("H", "ha");        // ha/ha hairless

	private final String[] alleles;

	Locus(String... alleles) {
		this.alleles = alleles;
	}

	public String allele(int i) {
		return alleles[i];
	}

	public int alleleCount() {
		return alleles.length;
	}

	public int indexOf(String symbol) {
		for (int i = 0; i < alleles.length; i++) {
			if (alleles[i].equals(symbol)) {
				return i;
			}
		}
		throw new IllegalArgumentException(name() + ": unknown allele " + symbol);
	}

	/** How often a wild cat carries a recessive allele in a hidden slot (Basil, 2026-11-24). */
	public double carrierRate() {
		return switch (this) {
			case M, H, BT -> 0.10;
			default -> 0.25;
		};
	}
}
