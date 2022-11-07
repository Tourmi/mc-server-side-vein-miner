# Server-side Vein Miner
A mod for Minecraft which allows to instantly mine ore veins

## Features so far
- When mining an ore block (the allowed blocks are configurable), all of its identical neighbors are also mined, recursively until there are no longer any blocks
- Configurable maximum amount of blocks to destroy at once (default: 100)
- Option to stop the vein mining if the tool is about to break (default: true)
- Holding shift prevents the vein mining from triggering
- The appropriate tool must be equipped for vein mining to trigger
- Vein mining applies all the enchantments of a tool, so Fortune, Silk Touch and Unbreaking all work properly
