![header](gitresources/header.png)

# Bettercolors 6
![Forge dev version: 1.8.9-11.15.1.1722](https://img.shields.io/badge/forge_dev-1.8.9_11.15.1.1722-green.svg)
![Hack status](https://img.shields.io/badge/status-undetected-green.svg)

![Release](https://img.shields.io/github/release/n3roo/bettercolors.svg)
![Downloads](https://img.shields.io/github/downloads/n3roo/bettercolors/latest/total.svg)
![Issues](https://img.shields.io/github/issues/n3roo/bettercolors.svg)

A silent hack built within the forge API. It bypasses powerful anticheats (forge mods and servers).

## Disclamer

I made that project to learn Java in a funny way. I started 2 years ago but the repositories are, and will stay private. Only this repository will stay public. This has been made for educational purposes. I'm still improving it from time to time.

**Use it at your own risk ! I won't be responsible of anything you do wrong with this mod.**

## Features description

**Aim assistance :** A smooth and human-like aim assistance 100% undetectable by any anticheat. It is invisible since it looks absolutely human. You can record videos while using it without any problem.

**Click assistance :** A human-like click assistance that send REAL clicks, to help you clicking faster. It can bypass forge CPS counter mods by toggling packets.

**External interface :** A clean external interface to configure the mod outside the minecraft client, so you can record without any problem !

**Console :** Curious ? There is a console showing everything happening in the mod. 

**Teammate detection :** The mod find your teammates from any server game mode, and prevent hacks to be activated on them.

**Settings :** You can configure every mod and all is saved in real-time.

## Commands

- `HOME` : Toggle aim assist
- `PAGE_UP` : Toggle click assist
- `RIGHT_SHIFT` : Toggle GUI

## Features help

- Aim assistance :
    - "Stop on right click" : it stops the aim assistance if you right click
    - The aim speed is defined by refresh rate,
    - The aim force is defined by step X and step Y,
    - The aim range sets the maximum distance to aim an entity,
    - The aim radius is the FOV in which you will aim at an entity,
    - "Stop when reached" : it means that when you aim at an entity, the aimassist stops

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

The recommended values are the default one.

| Setting                     | Default       | Minimum     | Maximum   | Type    |
|-----------------------------|---------------|-------------|-----------|---------|
| AimAssist (AA)              | true          | false       | true      | boolean |
| ClickAssist (CA)            | true          | false       | true      | boolean |
| AA_RefreshRate (ms)         | 2             | 0           | 10        | integer |
| AA_StepX                    | 5             | 1 (0=OFF)   | 80        | float   |
| AA_StepY                    | 5             | 1 (0=OFF)   | 80        | float   |
| AA_Range                    | 5             | 1 (0=OFF)   | 100       | float   |
| AA_RadiusX                  | 60            | 1 (0=OFF)   | 180       | float   |
| AA_RadiusY                  | 30            | 1 (0=OFF)   | 90        | float   |
| AA_StopRightClick           | true          | false       | true      | boolean |
| AA_StopWhenReached          | true          | false       | true      | boolean |
| AA_ClicksToActivate         | 2             | 0           | 20        | integer |
| AA_TimeToActivate(ms)       | 1500          | 0           | 100000    | integer |
| CA_Addition                 | 2             | 1 (0=OFF)   | 10        | long    |
| CA_CPSChance                | 80            | 1 (0=OFF)   | 100       | integer |
| CA_Packets                  | false         | false       | true      | boolean |
| CA_OnlyOnEntity             | false         | false       | true      | boolean |
| CA_CPSActivationTime        | 1500          | 0.1 (0=OFF) | unlimited | float   |
| CA_CPSClicksToActivate      | 3             | 0           | unlimited | integer |
| CA_TimeToActivate(ms)       | 1500          | 0           | 10000     | integer |
| UseOnMobs                   | false         | false       | true      | boolean |
| TeamFilter                  | true          | false       | true      | boolean |

## Changelog

The changelog is available [here](CHANGELOG.MD).

## Found a bug ?

Go to the [issues](https://github.com/N3ROO/Bettercolors/issues) section, and open a new issue ! :)

## Developer section

This section shwo you how to get a working development environment.

### 1. Download your favorite IDE

It is easier to set up forge with [Eclipse](https://www.eclipse.org/neon/). Otherwise, you can use [IntelliJ](https://www.jetbrains.com/idea/) that is way better java IDE than Ecplise.

### 2. Set up forge & IDE

- Download [forge 1.8.9-11.15.1.1722](https://adfoc.us/serve/sitelinks/?id=271228&url=https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.8.9-11.15.1.1722/forge-1.8.9-11.15.1.1722-mdk.zip),
- Extract everything from the zip file to the project folder (named "Bettercolors" for example),
- Delete the src/ folder (since the example code is useless here),
- Clone or download the content of this repository in the project folder,
- Follow the instructions in the "README.txt" file if you are using Eclipse, otherwise, take a look to this [youtube video](https://www.youtube.com/watch?v=G2aPT36kf60)

Once it's done, you should be able to launch the mod from your favorite IDE.

### 3. Contributing

If you want to contribute, make sure to :
- not push the forge .txt files,
- keep the same coding style that is already used (plus headers describing every function),
- describe what is the purpose of your pull request.
