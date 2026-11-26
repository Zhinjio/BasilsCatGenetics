package io.github.zhinjio.basilscatgenetics.genetics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

/**
 * Genes for cats the mod meets for the first time (wild spawns and cats already in the world), from their vanilla
 * coat. Each spec is GeneSheet.csv's row: O first, then the loci in {@link Locus} order. Per locus:
 * "X/Y" exact, "X/?" X shows and the second copy is hidden, "?" fully hidden.
 * O: "red" (O/O or O/y), "notred" (o/o or o/y), "tortie" (O/o, always female), "?" hidden.
 */
public final class WildCats {
	private WildCats() {
	}

	//                    O        B     D     Dm     A     M     I     Wb     W       C     Bt    H
	public static final Map<String, String> VANILLA = new LinkedHashMap<>();

	static {
		VANILLA.put("all_black", "notred B/? D/? ? a/a ? I/? ? w/w C/? Bt/? H/?");
		VANILLA.put("black", "notred B/? D/? ? a/a ? I/? ? Ws/w C/? Bt/? H/?");
		VANILLA.put("british_shorthair", "notred B/? d/d Dm/? a/a ? I/? ? w/w C/? Bt/? H/?");
		VANILLA.put("calico", "tortie B/? D/? ? a/a ? I/? ? Ws/Ws C/? Bt/? H/?");
		VANILLA.put("jellie", "notred B/? d/d Dm/? A/? T/? I/? Wb/? Ws/w C/? Bt/? H/?");
		VANILLA.put("persian", "? ? ? ? A/? T/? ? wb/wb w/w C/? Bt/? H/?");
		VANILLA.put("ragdoll", "notred B/? d/d dm/dm a/a ? i/i ? Ws/w c/c Bt/? H/?");
		VANILLA.put("red", "red ? D/? ? A/? T/? I/? Wb/? Ws/w C/? Bt/? H/?");
		VANILLA.put("siamese", "notred B/? D/? ? a/a ? I/? ? w/w c/c Bt/? H/?");
		VANILLA.put("tabby", "notred b/? D/? ? A/? T/? I/? Wb/? Ws/w C/? Bt/? H/?");
		VANILLA.put("white", "? ? ? ? ? ? ? ? W/? ? Bt/? H/?");
	}

	/** A random wild genotype that shows the given spec. */
	public static Genotype roll(String spec, RandomGenerator rng) {
		String[] t = spec.trim().split("\\s+");
		Locus[] ls = Locus.values();
		if (t.length != 1 + ls.length) {
			throw new IllegalArgumentException("bad spec: " + spec);
		}
		int[][] p = new int[ls.length][2];
		for (int i = 0; i < ls.length; i++) {
			Locus l = ls[i];
			String s = t[1 + i];
			if (s.equals("?")) {
				p[i][0] = hiddenAllele(l, 0, rng);
				p[i][1] = hiddenAllele(l, 0, rng);
			} else {
				String[] a = s.split("/");
				p[i][0] = l.indexOf(a[0]);
				p[i][1] = a[1].equals("?") ? hiddenAllele(l, p[i][0], rng) : l.indexOf(a[1]);
			}
		}
		boolean male;
		int[] x;
		switch (t[0]) {
			case "tortie" -> {
				male = false;
				x = new int[] {Genotype.O_RED, Genotype.O_NOT_RED};
			}
			case "red", "notred" -> {
				int o = t[0].equals("red") ? Genotype.O_RED : Genotype.O_NOT_RED;
				male = rng.nextBoolean();
				x = male ? new int[] {o} : new int[] {o, o};
			}
			case "?" -> {
				male = rng.nextBoolean();
				x = male ? new int[] {randomO(rng)} : new int[] {randomO(rng), randomO(rng)};
			}
			default -> throw new IllegalArgumentException("bad O in spec: " + spec);
		}
		return new Genotype(p, x, male);
	}

	/**
	 * A hidden copy behind a shown allele: the shown allele again, or (at the locus's carrier rate) a more recessive
	 * one. Assumption to confirm with Basil: with two recessive options (b and b1) the carrier rate is split evenly.
	 */
	static int hiddenAllele(Locus l, int shown, RandomGenerator rng) {
		int more = l.alleleCount() - 1 - shown;
		if (more == 0 || rng.nextDouble() >= l.carrierRate()) {
			return shown;
		}
		return shown + 1 + rng.nextInt(more);
	}

	private static int randomO(RandomGenerator rng) {
		return rng.nextDouble() < 0.25 ? Genotype.O_RED : Genotype.O_NOT_RED;
	}
}
