# Basil's Cat Genetics: design (draft)

Status: draft, 2026-11-26. Nothing is built yet. Basil designs the genetics and the art and has final say on both,
and on all player-facing wording. Zhinjio builds the mod.

Source of truth for the genes: `GeneSheet.csv` (Basil's master sheet, 2026-11-24). Where this file and the sheet
disagree, the sheet wins. One known fix is pending in the sheet: Calico and Jellie read "Ws/W" on the W locus and
should be high white.

## Goal

Cat coats follow (mostly) real feline inheritance. Only the vanilla coats spawn naturally; every other coat has
to be bred.

## Shape

- One Fabric mod, server side only. Players install nothing: new coats are `cat_variant` entries the mod ships
  as data (registries sync to vanilla clients), and their textures go in the server resource pack.
- Built and tested on a staging server first.
- Coats without art yet still get their final texture name and show the black and magenta missing texture until
  painted. Never released that way.

## Genes

From the sheet. Dominant allele first.

| Locus | Alleles | What it does |
|---|---|---|
| O | O, o | X-linked. O/O or O/y red; O/o tortie (females, and XXY males); o/o or o/y not red |
| B | B > b > b1 | Black, chocolate, cinnamon. Hidden on red |
| D | D > d | d/d dilute (black to blue, red to cream) |
| Dm | Dm > dm | Only on dilute cats: dm/dm gives a warmer, pinkish tint (blue becomes lilac) |
| A | A > a | A/? tabby, a/a solid. Red is striped either way |
| M | T > be | Only on tabbies: be/be spotted instead of striped (Bengal). Any color can be spotted |
| I | I > i | i/i silver (inhibitor) |
| Wb | Wb > wb | Only on tabbies: wb/wb golden (vanilla Persian). Golden hides O, B, D, Dm and I, so every golden tabby is the same golden, striped or spotted. No effect on solids |
| W | W > Ws > w | W/? all white, hides everything except bobtail and hairless. Ws/Ws high white, Ws/w low white, w/w none |
| C | C > c | c/c colorpoint (Siamese pattern) |
| Bt | Bt > bt | bt/bt bobtail. Recessive and healthy in game |
| H | H > ha | ha/ha hairless (sphynx). Hides everything except bobtail, including W |

Places where the game differs from real cats on purpose, to keep things simple: bobtail is recessive and healthy
(a real bobtail gene can be dominant and lethal when doubled), widebanding is one gene instead of many, and
spotting is a simple modifier on tabby.

## Coat from genes (phenotype)

1. ha/ha: hairless. Stop (bobtail still shows).
2. W/?: white. Stop (bobtail still shows).
3. Color: red (O/O, O/y), tortie (O/o), or eumelanin (black, chocolate or cinnamon by B).
4. d/d: dilute; with dm/dm also the pinkish modifier.
5. Pattern: red always striped; others striped with A/?, solid with a/a. be/be turns stripes into spots.
   A tortie with A/? (torbie) is its own coat: its dark patches get brownish stripes.
6. wb/wb on a tabby: golden.
7. i/i: silver.
8. c/c: colorpoint.
9. White spotting: Ws/Ws high white, Ws/w low white.
10. bt/bt: bobtail.

Each result maps to one texture name. The vanilla coats keep their own textures:

| Vanilla coat | Genes that show (from the sheet) |
|---|---|
| all_black (Black) | o, B, D, a/a, I, w/w, C |
| black (Tuxedo) | o, B, D, a/a, I, Ws/w, C |
| british_shorthair | o, B, d/d, Dm, a/a, I, w/w, C |
| calico | O/o, B, D, a/a, I, Ws/Ws, C |
| jellie | o, B, d/d, Dm, A, T, I, Wb, Ws/Ws, C |
| persian | A, T, wb/wb, w/w, C |
| ragdoll | o, B, d/d, dm/dm, a/a, i/i, Ws/w, c/c |
| red | O, D, A, T, I, Wb, Ws/w, C |
| siamese | o, B, D, a/a, I, w/w, c/c |
| tabby | o, B, D, A, T, I, Wb, Ws/w, C |
| white | W |

Every other combination gets a generated name, built the same way each time (for example
`cat_lilac_tortie_highwhite` or `cat_cream_spotted_point`). Rough count: around 1,500 coats, each with an adult
and a kitten texture, so coats are composed from layers by a script, not painted one by one.

## Sex and existing cats

- Every cat gets a sex the first time the mod sees it (newly spawned, or already in the world): 50/50, except
  tortie coats are always female.
- It also gets genes that fit its coat. Each hidden slot carries the recessive allele 1 time in 4 for b, b1, i
  and dm, and 1 time in 10 for be, ha and bt.
- Data is stored on the cat (Fabric data attachment): sex, genes, tested, sterile.

## Breeding

- Only a male and a female can breed, and neither may be sterile.
- Each kitten gets one allele per locus from each parent. On O, a father gives his X to daughters and his Y to
  sons.
- 1 male kitten in 200 is XXY: two X (one from each parent) plus Y. It can be tortie and it is sterile.
- The kitten's coat is set from its genes.

## Bray's Cat Overhaul

- Goal: no hard dependency, so this mod never waits on another mod's updates. Zhinjio has asked Bray for
  permission to use his cat textures in our layers, with credit (his pack is All Rights Reserved). Without it,
  Basil paints original coats for those breeds.
- Genes for Bray's 8 village cats are in the sheet. Where two coats share genes (Russian Blue and British
  Shorthair), the vanilla texture wins.
- If Bray's is installed too, its cats map onto our genes instead of duplicating coats (to design).
- Hellcat and Withercat stay out of the genetics and are marked sterile.

## Looking at a cat

- Sneak while looking at a cat: the action bar shows its sex and coat.
- A tested cat shows its genes too.
- Wording to be drafted; Basil approves it.

## Gene test kit

- Shapeless recipe: glass bottle, redstone dust, slime ball, tropical fish.
- Use it on a cat: the kit is used up and the cat counts as tested from then on.
- Icon: Minecraft-Heads "Slime in a Bottle" (head 38393),
  texture `e4c4cd633bda3df477986ae624368397e7b55d2438e31d17170e22f293be113f`. Credit Minecraft-Heads.

## Advancements

Any advancements go in the shared Zhinjio tab.

## Art

- Textures are layers: base colors, tabby and spot patterns, silver, white spotting, points, and overlays such as
  cameo, stacked by a script. Bobtail is a script step (it clears the second tail segment).
- Agreed so far: 64x32 adult layers on the vanilla layout, fully opaque or fully clear pixels in base layers;
  eyes, nose and pads on their own top layer; overlays may be partly transparent; the cameo overlay goes above
  the pattern.
- Kitten layers (32x32) come later.
- `tools/catpreview.py` stacks layers and adds the result to a preview folder; `tools/cat.html` shows it on a 3D
  adult cat. The vanilla textures it compares against are Mojang's and are not in this repo.

## Phases

1. The vanilla coats plus generated names, on staging. Prove sex, gene assignment, breeding and the sneak card.
2. Bray's 8 village coats.
3. Art for generated coats, in groups (dilute calicos and white spotting first).

## To check before building

- Whether a `cat_variant` can be registered so it never spawns naturally.
- How a 26.3 `cat_variant` names its kitten texture.
- What happens to cats wearing a mod coat if the mod is removed.
