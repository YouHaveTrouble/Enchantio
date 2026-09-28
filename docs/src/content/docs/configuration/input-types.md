---
title: Input types
description: How to specify common input types in configuration
---

## Tags

### Items

#### Item reference
Example: `minecraft:stick`

For specific items, use namespaced vanilla item id. If namespace is not provided, `minecraft` is assumed.

#### Item tag
Example: `#minecraft:axes`

For <a href="https://minecraft.wiki/w/Item_tag_(Java_Edition)" target="_blank">item tags</a>, use namespaced tag id. If
namespace is not provided, `minecraft` is assumed. Custom tags from other plugins and datapacks are also supported.

### Blocks

#### Block reference
Example: `minecraft:dirt`

For specific items, use namespaced vanilla item id. If namespace is not provided, `minecraft` is assumed.

#### Block tag
Example: `#minecraft:planks`

For <a href="https://minecraft.wiki/w/Block_tag_(Java_Edition)" target="_blank">block tags</a>, use namespaced tag id. If
namespace is not provided, `minecraft` is assumed. Custom tags from other plugins and datapacks are also supported.


### Enchantments

#### Enchantment tag
Example: `#minecraft:in_enchanting_table`

For <a href="https://minecraft.wiki/w/Enchantment_tag_(Java_Edition)" target="_blank">enchantment tags</a>, use namespaced tag id. If
namespace is not provided, `minecraft` is assumed. Custom tags from other plugins and datapacks are also supported.

## Slot types
- `ANY` - Any slot.
- `MAINHAND` - Main hand slot.
- `OFFHAND` - Offhand slot.
- `ARMOR` - Any armor slot.
- `HELMET` - Helmet slot.
- `CHESTPLATE` - Chestplate slot.
- `LEGGINGS` - Leggings slot.
- `BOOTS` - Boots slot.
