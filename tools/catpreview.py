#!/usr/bin/env python3
"""Stack cat texture layers (bottom first) and add the result to a preview folder for the 3D viewer.

    catpreview.py --dir PREVIEW --name "Silver cameo bengal" silver.png bengal.png cameo.png

Writes PREVIEW/<slug>.png (the stacked texture) and <slug>_x8.png (8x, gray where transparent), copies cat.html
into PREVIEW if missing, and adds or replaces the entry in PREVIEW/textures.json. Serve the folder with
python3 -m http.server and open cat.html (3D) or the _x8 images (flat). Also reports pixels outside the vanilla
layout and holes where the vanilla cat is painted, using a vanilla adult texture (--vanilla, default all_black).
Layers are only read, never changed. Adult (64x32) textures only for now.
"""
import argparse, json, os, re, shutil, sys

HERE = os.path.dirname(os.path.abspath(__file__))


def main():
    ap = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    ap.add_argument("--dir", required=True, help="preview folder (created if missing)")
    ap.add_argument("--name", required=True, help="button label in the viewer")
    ap.add_argument("--vanilla", default=os.path.join(HERE, "..", "vanilla", "cat_all_black.png"))
    ap.add_argument("layers", nargs="+", help="layer PNGs, bottom first")
    a = ap.parse_args()
    from PIL import Image
    ims = [Image.open(p).convert("RGBA") for p in a.layers]
    for p, im in zip(a.layers, ims):
        if im.size != (64, 32):
            sys.exit(f"{p}: {im.size}, expected 64x32")
    out = ims[0]
    for im in ims[1:]:
        out = Image.alpha_composite(out, im)
    os.makedirs(a.dir, exist_ok=True)
    slug = re.sub(r"[^a-z0-9]+", "_", a.name.lower()).strip("_")
    out.save(os.path.join(a.dir, slug + ".png"))
    bg = Image.new("RGBA", out.size, (60, 60, 60, 255))
    Image.alpha_composite(bg, out).resize((512, 256), Image.NEAREST).save(os.path.join(a.dir, slug + "_x8.png"))
    if os.path.exists(a.vanilla):
        v = Image.open(a.vanilla).convert("RGBA")
        px = [(x, y) for x in range(64) for y in range(32)]
        extra = [p for p in px if out.getpixel(p)[3] and not v.getpixel(p)[3]]
        holes = [p for p in px if v.getpixel(p)[3] and not out.getpixel(p)[3]]
        print(f"outside vanilla layout: {len(extra)} {extra[:10]}")
        print(f"holes where vanilla is painted: {len(holes)} {holes[:10]}")
    if not os.path.exists(os.path.join(a.dir, "cat.html")):
        shutil.copy(os.path.join(HERE, "cat.html"), a.dir)
    tj = os.path.join(a.dir, "textures.json")
    lst = json.load(open(tj)) if os.path.exists(tj) else []
    lst = [t for t in lst if t["file"] != slug + ".png"]
    lst.append({"file": slug + ".png", "name": a.name, "layers": [os.path.basename(p) for p in a.layers]})
    json.dump(lst, open(tj, "w"), indent=1)
    print(f"wrote {slug}.png; {len(lst)} textures in {tj}")


if __name__ == "__main__":
    main()
