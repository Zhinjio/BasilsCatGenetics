package io.github.zhinjio.basilscatgenetics.genetics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Every coat the genes can produce, and which vanilla coat (if any) each one is.
 * Run as a program to (re)write the mod's cat_variant files: {@code ./gradlew generateCoats}.
 */
public final class CoatCatalog {
	private CoatCatalog() {
	}

	/** Coat texture name (cat_...) -> vanilla variant name, for coats the game already has. */
	public static final Map<String, String> VANILLA_BY_COAT = new TreeMap<>();

	static {
		Random rng = new Random(0);
		WildCats.VANILLA.forEach((vanilla, spec) -> VANILLA_BY_COAT.put(Phenotype.textureName(WildCats.roll(spec, rng)), vanilla));
	}

	/** All coat texture names, by walking every genotype. */
	public static TreeSet<String> allCoats() {
		Locus[] ls = Locus.values();
		TreeSet<String> out = new TreeSet<>();
		int[][] xs = {{0}, {1}, {0, 0}, {0, 1}, {1, 1}};
		int[][] p = new int[ls.length][2];
		walk(ls, 0, p, xs, out);
		return out;
	}

	private static void walk(Locus[] ls, int i, int[][] p, int[][] xs, TreeSet<String> out) {
		if (i == ls.length) {
			for (int[] x : xs) {
				out.add(Phenotype.textureName(new Genotype(p, x, x.length == 1)));
				if (x.length == 2) {
					out.add(Phenotype.textureName(new Genotype(p, x, true)));   // XXY
				}
			}
			return;
		}
		int n = ls[i].alleleCount();
		for (int a = 0; a < n; a++) {
			for (int b = a; b < n; b++) {
				p[i][0] = a;
				p[i][1] = b;
				walk(ls, i + 1, p, xs, out);
			}
		}
	}

	/** args[0]: the resources folder (src/main/resources). Writes data/<mod>/cat_variant/<coat>.json. */
	public static void main(String[] args) throws IOException {
		String mod = "basils_cat_genetics";
		Path dir = Path.of(args[0], "data", mod, "cat_variant");
		Files.createDirectories(dir);
		try (var old = Files.list(dir)) {
			for (Path f : old.filter(f -> f.toString().endsWith(".json")).toList()) {
				Files.delete(f);   // generated files only; this folder holds nothing else
			}
		}
		int n = 0;
		for (String coat : allCoats()) {
			if (VANILLA_BY_COAT.containsKey(coat)) {
				continue;
			}
			String json = "{\n  \"asset_id\": \"" + mod + ":entity/cat/" + coat + "\",\n  \"baby_asset_id\": \"" + mod
					+ ":entity/cat/" + coat + "_baby\",\n  \"spawn_conditions\": []\n}\n";
			Files.writeString(dir.resolve(coat.substring(4) + ".json"), json);
			n++;
		}
		System.out.println("wrote " + n + " cat_variant files; vanilla coats: " + VANILLA_BY_COAT);
	}
}
