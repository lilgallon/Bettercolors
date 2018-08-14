![header](gitresources/header.png)

# N3RO's Better colors
![Last stable version](https://img.shields.io/badge/bettercolors-6.0-gold.svg)
![Forge dev version: 1.8.9-11.15.1.1722](https://img.shields.io/badge/forge_dev-1.8.9_11.15.1.1722-green.svg)
![Hack status](https://img.shields.io/badge/status-undetected-brightgreen.svg)

A silent hack built within the forge API. It bypasses the best anticheats (forge mods and servers).

## Disclamer

Use it at your own risk!

## Features description

**Aim assistance :** A smooth and human-like aim assistance 100% undetectable by any anticheat. It is invisible since it looks absolutely human. You can record videos while using it without any problem.

**Click assistance :** A human-like click assistance that send REAL clicks, to help you clicking faster. It can bypass forge CPS counter mods by toggling packets.

**External interface :** A clean external interface to configure the mod outside the minecraft client, so you can record without any problem !

**Console :** Curious ? There is a console showing everything happening in the mod. 

**Teammate detection :** The mod find your teammates from any server game mode, and prevent hacks to be activated on them.

**Settings :** You can configure every mod and all is saved in real-time.

## Features help

- Aim assistance :
    - "Stop on right click" : it stops the aim assistance if you right click
    - The aim speed is defined by refresh rate,
    - The aim force is defined by step X and step Y,
    - The aim range sets the maximum distance to aim an entity,
    - The aim radius is the FOV in which you will aim at an entity,

- Click assistance :
    - "Packets" : Send packets instead of REAL clicks (**bypass forge anticheat but more risky server-side**)
    - "Only on entity" : It will click only if you are aiming entity,
    - Additional clicks defines how many clicks will be sent in addition to yours,
    - Chance is the chance of every artificial click to be sent,
    - Duration corresponds to the activation time of the click assistance,
    
- Common :
    - "Use on mobs" : Defines if the modules works on mobs,
    - "Team filter" : Defines if the modules works on teammates,
    - Clicks / Time to activate defines the number of clicks per time that you need to do in order to activate the modules.

## Values

The recommended values are the default one. This configuration bypasses ANY anticheat.


| Setting                     | Default       | Minimum     | Maximum   | Type    |
|-----------------------------|---------------|-------------|-----------|---------|
| AimAssist (AA)              | true          | false       | true      | boolean |
| ClickAssist (CA)            | true          | false       | true      | boolean |
| AA_RefreshRate (ms)         | 2             | 0           | 10        | int     |
| AA_StepX                    | 5             | 1 (0=OFF)   | 80        | float   |
| AA_StepY                    | 5             | 1 (0=OFF)   | 80        | float   |
| AA_Range                    | 5             | 1 (0=OFF)   | 100       | float   |
| AA_RadiusX                  | 60            | 1 (0=OFF)   | 180       | float   |
| AA_RadiusY                  | 30            | 1 (0=OFF)   | 90        | float   |
| AA_StopRightClick           | true          | false       | true      | boolean |
| AA_StopWhenReached          | true          | false       | true      | boolean |
| AA_ClicksToActivate         | 2             | 0           | 20        | int     |
| AA_TimeToActivate(ms)       | 1500          | 0           | 100000    | int     |
| CA_Addition                 | 2             | 1 (0=OFF)   | 10        | long    |
| CA_CPSChance                | 80            | 1 (0=OFF)   | 100       | int     |
| CA_Packets                  | false         | false       | true      | boolean |
| CA_OnlyOnEntity             | false         | false       | true      | boolean |
| CA_CPSActivationTime        | 1500          | 0.1 (0=OFF) | unlimited | float   |
| CA_CPSClicksToActivate      | 3             | 0           | unlimited | int     |
| CA_TimeToActivate(ms)       | 1500          | 0           | 10000     | int     |
| UseOnMobs                   | false         | false       | true      | boolean |
| TeamFilter                  | true          | false       | true      | boolean |


## Commands

- `HOME` : Toggle aim assist
- `PAGE_UP` : Toggle click assist
- `RIGHT_SHIFT` : Toggle GUI


## Misc

- Changelog available [here](CHANGELOG.MD).
- Setup IntelliJ with Forge & updating forge - [Youtube video](https://www.youtube.com/watch?v=G2aPT36kf60)
