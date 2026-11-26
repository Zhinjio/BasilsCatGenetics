package io.github.zhinjio.basilscatgenetics.genetics;

import java.util.ArrayList;
import java.util.List;

/**
 * What a genotype looks like, as an ordered list of words that also names its texture: {@code cat_<words joined by _>}.
 * Order of the rules follows DESIGN.md "Coat from genes".
 *
 * Assumption to confirm with Basil: golden needs agouti (A), as the sheet lists Persian as A/?, so a red cat with
 * a/a and wb/wb is not golden.
 */
public final class Phenotype {
	private Phenotype() {
	}

	public static List<String> words(Genotype g) {
		List<String> w = new ArrayList<>();
		boolean bobtail = g.homo(Locus.BT, 1);
		if (g.homo(Locus.H, 1)) {
			w.add("hairless");
		} else if (g.has(Locus.W, 0)) {
			w.add("white");
		} else {
			colorAndPattern(g, w);
			if (g.homo(Locus.C, 1)) {
				w.add("point");
			}
			int[] ws = g.pair(Locus.W);
			if (ws[0] == 1 && ws[1] == 1) {
				w.add("highwhite");
			} else if (ws[0] == 1 || ws[1] == 1) {
				w.add("lowwhite");
			}
		}
		if (bobtail) {
			w.add("bobtail");
		}
		return w;
	}

	public static String textureName(Genotype g) {
		return "cat_" + String.join("_", words(g));
	}

	private static void colorAndPattern(Genotype g, List<String> w) {
		boolean agouti = g.has(Locus.A, 0);
		boolean spotted = g.homo(Locus.M, 1);
		String pattern = spotted ? "spotted" : "tabby";
		if (agouti && g.homo(Locus.WB, 1)) {   // golden hides O, B, D, Dm and I
			w.add("golden");
			w.add(pattern);
			return;
		}
		int red = 0;
		for (int a : g.xAlleles()) {
			if (a == Genotype.O_RED) {
				red++;
			}
		}
		int xs = g.xAlleles().length;
		boolean allRed = red == xs, tortie = red > 0 && red < xs;
		String dilution = g.homo(Locus.D, 1) ? (g.homo(Locus.DM, 1) ? "dilutemod" : "dilute") : null;
		if (allRed) {
			w.add("red");
			if (dilution != null) {
				w.add(dilution);
			}
			w.add(pattern);   // red is striped (or spotted) whatever A is
		} else {
			w.add(new String[] {"black", "chocolate", "cinnamon"}[g.shown(Locus.B)]);
			if (dilution != null) {
				w.add(dilution);
			}
			if (tortie) {
				w.add(agouti ? "torbie" : "tortie");
				if (agouti && spotted) {
					w.add("spotted");
				}
			} else if (agouti) {
				w.add(pattern);
			} else {
				w.add("solid");
			}
		}
		if (g.homo(Locus.I, 1)) {
			w.add("silver");
		}
	}
}
