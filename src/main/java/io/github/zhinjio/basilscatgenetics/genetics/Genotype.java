package io.github.zhinjio.basilscatgenetics.genetics;

import java.util.Arrays;
import java.util.random.RandomGenerator;

/**
 * A cat's genes. Autosomal loci hold two allele indexes each ({@link Locus}); O is on the X chromosome: a female
 * has two X, a male one X and a Y, and an XXY male two X and a Y. O alleles: 0 = O (red), 1 = o.
 * Plain Java with no Minecraft types, so the rules can be tested on their own.
 */
public final class Genotype {
	public static final int O_RED = 0, O_NOT_RED = 1;
	/** 1 male kitten in 200 is XXY (Basil). */
	public static final int XXY_ONE_IN = 200;

	private final int[][] pairs;   // [locus.ordinal()][2]
	private final int[] x;         // O allele per X chromosome, length 1 or 2
	private final boolean y;

	public Genotype(int[][] pairs, int[] x, boolean y) {
		if (pairs.length != Locus.values().length || x.length < 1 || x.length > 2 || (!y && x.length != 2)) {
			throw new IllegalArgumentException("bad genotype shape");
		}
		this.pairs = new int[pairs.length][];
		for (int i = 0; i < pairs.length; i++) {
			this.pairs[i] = pairs[i].clone();
		}
		this.x = x.clone();
		this.y = y;
	}

	public boolean male() {
		return y;
	}

	public boolean xxy() {
		return y && x.length == 2;
	}

	/** XXY males are sterile (Basil). Hellcat and Withercat are marked sterile separately, not here. */
	public boolean sterile() {
		return xxy();
	}

	public int[] pair(Locus l) {
		return pairs[l.ordinal()].clone();
	}

	public int[] xAlleles() {
		return x.clone();
	}

	/** True when at least one copy is the given allele. */
	public boolean has(Locus l, int allele) {
		int[] p = pairs[l.ordinal()];
		return p[0] == allele || p[1] == allele;
	}

	/** True when both copies are the given allele. */
	public boolean homo(Locus l, int allele) {
		int[] p = pairs[l.ordinal()];
		return p[0] == allele && p[1] == allele;
	}

	/** The allele that shows: the more dominant of the two (lower index). */
	public int shown(Locus l) {
		int[] p = pairs[l.ordinal()];
		return Math.min(p[0], p[1]);
	}

	/** One kitten from a mother and a father. */
	public static Genotype breed(Genotype mother, Genotype father, RandomGenerator rng) {
		if (mother.male() || !father.male()) {
			throw new IllegalArgumentException("need a female and a male");
		}
		int[][] p = new int[Locus.values().length][2];
		for (Locus l : Locus.values()) {
			p[l.ordinal()][0] = mother.pairs[l.ordinal()][rng.nextInt(2)];
			p[l.ordinal()][1] = father.pairs[l.ordinal()][rng.nextInt(2)];
		}
		int fromMother = mother.x[rng.nextInt(mother.x.length)];
		if (rng.nextBoolean()) {   // daughter: the father's X
			return new Genotype(p, new int[] {fromMother, father.x[rng.nextInt(father.x.length)]}, false);
		}
		if (rng.nextInt(XXY_ONE_IN) == 0) {   // XXY son: one X from each parent, plus the Y
			return new Genotype(p, new int[] {fromMother, father.x[rng.nextInt(father.x.length)]}, true);
		}
		return new Genotype(p, new int[] {fromMother}, true);
	}

	/** Compact text form, e.g. "XX O/o B/b D/D ..." (also used for storage and the tested-cat readout). */
	public String encode() {
		StringBuilder sb = new StringBuilder(y ? (x.length == 2 ? "XXY" : "XY") : "XX");
		sb.append(' ').append(x[0] == O_RED ? "O" : "o");
		if (x.length == 2) {
			sb.append('/').append(x[1] == O_RED ? "O" : "o");
		} else {
			sb.append("/y");
		}
		for (Locus l : Locus.values()) {
			int[] q = pairs[l.ordinal()];
			sb.append(' ').append(l.allele(Math.min(q[0], q[1]))).append('/').append(l.allele(Math.max(q[0], q[1])));
		}
		return sb.toString();
	}

	public static Genotype decode(String s) {
		String[] t = s.trim().split(" ");
		boolean y = t[0].endsWith("Y");
		String[] o = t[1].split("/");
		int[] x = o[1].equals("y")
				? new int[] {o[0].equals("O") ? O_RED : O_NOT_RED}
				: new int[] {o[0].equals("O") ? O_RED : O_NOT_RED, o[1].equals("O") ? O_RED : O_NOT_RED};
		Locus[] ls = Locus.values();
		if (t.length != 2 + ls.length) {
			throw new IllegalArgumentException("bad genotype: " + s);
		}
		int[][] p = new int[ls.length][2];
		for (int i = 0; i < ls.length; i++) {
			String[] a = t[2 + i].split("/");
			p[i][0] = ls[i].indexOf(a[0]);
			p[i][1] = ls[i].indexOf(a[1]);
		}
		return new Genotype(p, x, y);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Genotype g && g.encode().equals(encode());
	}

	@Override
	public int hashCode() {
		return encode().hashCode();
	}

	@Override
	public String toString() {
		return encode();
	}

	static int[][] emptyPairs() {
		int[][] p = new int[Locus.values().length][2];
		for (int[] q : p) {
			Arrays.fill(q, 0);
		}
		return p;
	}
}
