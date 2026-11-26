package io.github.zhinjio.basilscatgenetics.genetics;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GeneticsTest {
	@Test
	void everyVanillaSpecShowsOneDistinctCoat() {
		Random rng = new Random(1);
		Map<String, String> coatOf = new HashMap<>();
		for (var e : WildCats.VANILLA.entrySet()) {
			Set<String> seen = new HashSet<>();
			for (int i = 0; i < 20000; i++) {
				seen.add(Phenotype.textureName(WildCats.roll(e.getValue(), rng)));
			}
			assertEquals(1, seen.size(), e.getKey() + " shows several coats: " + seen);
			String coat = seen.iterator().next();
			String other = coatOf.put(coat, e.getKey());
			assertNull(other, e.getKey() + " and " + other + " look the same: " + coat);
		}
		coatOf.forEach((coat, v) -> System.out.println(v + " -> " + coat));
	}

	@Test
	void torties_are_female_and_encoding_round_trips() {
		Random rng = new Random(2);
		for (int i = 0; i < 5000; i++) {
			Genotype g = WildCats.roll(WildCats.VANILLA.get("calico"), rng);
			assertFalse(g.male());
			assertEquals(g, Genotype.decode(g.encode()));
		}
	}

	@Test
	void breedingFollowsTheRules() {
		Random rng = new Random(3);
		Genotype mom = WildCats.roll(WildCats.VANILLA.get("calico"), rng);
		Genotype d = WildCats.roll(WildCats.VANILLA.get("red"), rng);
		while (!d.male()) {
			d = WildCats.roll(WildCats.VANILLA.get("red"), rng);
		}
		final Genotype dad = d;
		int sons = 0, xxy = 0, n = 200000;
		for (int i = 0; i < n; i++) {
			Genotype k = Genotype.breed(mom, dad, rng);
			if (k.male()) {
				sons++;
				if (k.xxy()) {
					xxy++;
					assertTrue(k.sterile());
				} else {
					assertEquals(1, k.xAlleles().length);
				}
			} else {
				assertEquals(Genotype.O_RED, k.xAlleles()[1], "daughters get the red father's X");
			}
		}
		assertEquals(0.5, sons / (double) n, 0.01);
		assertEquals(1.0 / Genotype.XXY_ONE_IN, xxy / (double) sons, 0.002);
		assertThrows(IllegalArgumentException.class, () -> Genotype.breed(dad, dad, new Random()));
	}
}
