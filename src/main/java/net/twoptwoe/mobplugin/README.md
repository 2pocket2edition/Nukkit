# Nukkit Mob Plugin
Development: **[kniffo80](https://github.com/kniffo80)**
             **[matt404](https://github.com/matt404)**
             **[augesrob](https://github.com/augesrob)**
             **[PetteriM1](https://github.com/PetteriM1)**
             **[PikyCZ](https://github.com/PikyCZ)**
             **[DaPorkchop_](https://github.com/DaMatrix)**

MobPlugin is a plugin that implements the mob entities for MCPE including movement, aggression etc.

## Notice
This plug-in is in development. Therefore, It is possible to function abnormally.

[![Nukkit.io](https://img.shields.io/badge/Nukkit%20MobPlugin-Download-yellow.svg)](https://potestas.xyz/resources/mob-plugin.3/)
[![Discord](https://discordapp.com/api/guilds/386601650963349504/widget.png)](https://discord.gg/rBew6kc)
[![Github All Releases](https://img.shields.io/github/downloads/Nukkit-coders/mob-plugin/total.svg)](https://github.com/Nukkit-coders/mob-plugin/releases)
[![GitHub release](https://img.shields.io/github/release/Nukkit-coders/mob-plugin.svg)](https://github.com/Nukkit-coders/mob-plugin/releases/latest)
[![CircleCI builds](https://img.shields.io/circleci/project/github/Nukkit-coders/mob-plugin.svg)](https://circleci.com/gh/Nukkit-coders/mob-plugin)

# Plugin Example configuration
Place this plugin jar file to your Nukkit's home directory "${NUKKIT_HOME}/plugins".

Make Sure to include all mobs. Previous builds had few missing.

The following configuration sets mobs AI enabled and the auto spawn task will be triggered all 300 ticks.

#### Recommended Settings: 
Change the 0 to 1 and nothing higher.
0 = Disabled
1 = Allow them to spawn

```yaml
entities:
  mob-ai: true
  auto-spawn-tick: 300
  worlds-spawn-disabled: "exampleworld"

max-spawns:
  bat: 0
  blaze: 0
  cave-spider: 0
  chicken: 1
  cow: 1
  creeper: 0
  donkey: 0
  enderman: 0
  ghast: 0
  horse: 0
  iron-golem: 0
  mooshroom: 0
  mule: 0
  ocelot: 0
  pig: 1
  pig-zombie: 0
  rabbit: 0
  silverfish: 0
  sheep: 1
  skeleton: 0
  skeleton-horse: 0
  snow-golem: 0
  spider: 0
  squid: 0
  witch: 0
  wolf: 0
  zombie: 0
  zombie-horse: 0
  zombie-villager: 0
```
