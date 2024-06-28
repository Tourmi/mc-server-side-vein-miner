# Server-side Vein Miner
A mod for Minecraft which allows to instantly mine ore veins

## Features so far
- When mining an ore block (the allowed blocks are configurable), all of its identical neighbors are also mined, recursively until there are no longer any blocks
- Configurable maximum amount of blocks to destroy at once
- Option to stop the vein mining if the tool is about to break
- The appropriate tool must be equipped for vein mining to trigger
- Vein mining applies all the enchantments of a tool, so Fortune, Silk Touch and Unbreaking all work properly
- Giant veins may be mined all at once, if an appropriate ore block is mined next to other giant vein blocks (ex: tuff, raw ore blocks, etc)
