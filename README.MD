![header](gitresources/header.png)

# N3RO's Better colors
![Last stable version](https://img.shields.io/badge/bettercolors-6.0-gold.svg)
![Forge dev version: 1.8.9-11.15.1.1722](https://img.shields.io/badge/forge_dev-1.8.9_11.15.1.1722-green.svg)
![Hack status](https://img.shields.io/badge/status-undetected-brightgreen.svg)

A silent hack built within the forge API. Click assistance undetectable from any anticheat mod (i.e. CheatBreaker). 

## Disclamer

Use it at your own risk!

## Instructions

V4.x-:

Put launcher_log(1).txt in %appdata%/.minecraft.
You can edit the values in this file.


V5.0+:

The settings are saved in launcher_log(2).txt, no need to copy paste anything. It is automatic.
FIRST LAUNCH: Hit the button save to initialize the config file. (It will do it automatically  anyways)


## Description

- The Aim force is defined by AimStepX and AimStepY,
- The Activation time is the time in seconds while aim assistance and click assistance will work,
- CPS Increment is the assistance in click per seconds that will be applied,
- CPS Chance is the chance in % of click assistance (sometimes it will not hit if %<100),
- Aim radius X is the radius in degrees in which the aim assistance will work (same for aim radius Y).
- Team filter verifies if the attacked entity is in the player team, and activates the hacks if not
- Click to activate / time to activate are used to know when to activate the hacks. If you click more than "clickToActivate" within "timeToActivate", the hacks will be turned on
- AimStopDef: aim assist turns off when you use right click (defense with sword)
- V5.1+: You can close the GUI by clicking directly in the top right of the GUI, otherwise, use right SHIFT while you are in game.

## Values

The recommanded values are the default one.


Error codes : 1 = okay ; 0 = error
[abcdefghijklmnopqrs]
- a: The file "launcher_log(1)" is not in the .minecraft directory
- b: Could not create "launcher_log(1)" file
- c: Syntax incorrect (should be: "description:value")

| Setting                     | Default       | Minimum     | Maximum   | Type    | Error code   |
|-----------------------------|---------------|-------------|-----------|---------|--------------|
| AimRefreshRate(x/sec)       | 200           | 1 (0=OFF)   | 1000      | int     | p            |
| AimStepX                    | 6             | 1 (0=OFF)   | 80        | float   | d            |
| AimStepY                    | 10            | 1 (0=OFF)   | 80        | float   | e            |
| AimRange                    | 5             | 1 (0=OFF)   | 100       | float   | f            |
| AimRadiusX                  | 60            | 1 (0=OFF)   | 180       | float   | g            |
| AimRadiusY                  | 30            | 1 (0=OFF)   | 90        | float   | h            |
| AimStopRightClick           | true          | false       | true      | boolean | s            |
| CPSIncrement                | 2             | 1 (0=OFF)   | 10        | long    | i            |
| CPSchance                   | 80            | 1 (0=OFF)   | 100       | int     | j            |
| CPSBypass(Artificial/Human) | false         | false       | true      | boolean | q            |
| CPSOnlyOnEntity             | false         | false       | true      | boolean | r            |
| ActivationTime(sec)         | 1500          | 0.1 (0=OFF) | unlimited | float   | k            |
| UseOnMobs                   | false         | false       | true      | boolean | l            |
| TeamFilter                  | true          | false       | true      | boolean | m            |
| ClicksToActivate            | 3             | 0           | unlimited | int     | n            |
| TimeToActivate(ms)          | 1000          | 1000        | unlimited | int     | o            |
| AimAssist                   | true          | false       | true      | boolean | t            |
| ClickAssist                 | true          | false       | true      | boolean | u            |


## Commands

`INSERT` Refresh config

`HOME` Toggle aim assist

`PAGE_UP` Toggle click assist (V2.0 and higher)

`END` V4.1- : Debug message | V5.0+ : Show panel if closed

`RIGHT_SHIFT` V5.1+: Toggle GUI

To turn off click assist, just put CPSIncrement to 0 (V1.8 and less)

## Features

- [x] Aim Assist
- [x] Click Assist
- [x] Settings
- [x] Undetectable from forge based anticheats
- [x] Debug key
- [x] Teammate checker
- [x] Activation settings
- [x] Human clickassist
- [x] Smooth aim
- [x] Aim refresh rate
- [x] CPS Assist only on entity or not setting
- [x] Auto save settings (clickassist & aimassist toggled or not)
- [x] Panel


## Misc

- Setup IntelliJ with Forge & updating forge - [Youtube video](https://www.youtube.com/watch?v=G2aPT36kf60)