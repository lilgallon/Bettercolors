![header](gitresources/header.png)

# N3RO's silent hack
![Slient hack](https://img.shields.io/badge/silent-hacking-ff69b4.svg)
![Last stable version: 5.0](https://img.shields.io/badge/bettercolors-5.0-aqua.svg)
![Forge dev version: 1.8.9-11.15.1.1722](https://img.shields.io/badge/forge_dev-1.8.9_11.15.1.2318-brightgreen.svg)
![Forge client-side compatibility: 1.8.9](https://img.shields.io/badge/forge_client-1.8.9-brightgreen.svg)
![Hack status](https://img.shields.io/badge/status-undetected-brightgreen.svg)

A silent hack built within the forge API. Click assistance undetectable from any anticheat mod (i.e. CheatBreaker).

## Disclamer

Use it at your own risk!

## Instructions

Put launcher_log(1).txt in %appdata%/.minecraft.
You can edit the values in this file.

## Description

- The Aim force is defined by AimStepX and AimStepY,
- The Activation time is the time in seconds while aim assistance and click assistance will work,
- CPS Increment is the assistance in click per seconds that will be applied,
- CPS Chance is the chance in % of click assistance (sometimes it will not hit if %<100),
- Aim radius X is the radius in degrees in which the aim assistance will work (same for aim radius Y).
- Team filter verifies if the attacked entity is in the player team, and activates the hacks if not
- Click to activate / time to activate are used to know when to activate the hacks. If you click more than "clickToActivate" within "timeToActivate", the hacks will be turned on
- AimStopDef: aim assist turns off when you use right click (defense with sword)

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
| AimStopDef                  | true          | false       | true      | boolean | s            |
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

To desactivate click assist, just put CPSIncrement to 0 (V1.8 and less)

## Todo list

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
- [ ] CheatSmasher bypass

## Changelog

**V1.0:**
- Settings customization
- AimAssist

**V1.1:**
- Smoother aimassist
- Refreshrate setting removed
- Removed comments

**V1.2:**
- Added click assist (increment CPS when attacking with chance %)
- Added attacking time customization (= time in which the hacks are toggled)
- Added aimassist toggle system with HOME key

**V1.3:**
- Hack disabled while in GUI now
- Only left click works now (before right click was ok)
- Added use on mobs setting
- Code cleaning

**V1.4:**
- Debug key (END)
- Improved setting loader

**V1.5**
- Fixed debug

**V1.6**
- Added team tag filter on aim assist and click assist

**V1.7**
- Fixed hack (was not working anymore with team filter turned on)
- The hack stops automatically when no target is found after the beginning of the timer
- Optimized team filter (It does not call useAimAssist & useClickAssist when the target is a teammate)

**V2.0**
- Fixed aim toggle
- Improved hack activation: now it activates when the player is playing (before it worked while he was in Gui / outside minecraft)
- Activation settings: time & number of attacks
- Fixed settings not loaded while starting (before you had to reload to use your settings)
- Added "PAGE_UP" key to toggle click assist
- Added aim assist and click assist activated status in report

**V2.1**
- Changed mod from "better shadows" to "better colors"

**V3.0**
- Very smooth aim
- Aim refresh parameter
- Human / Artificial click assist parameter (artificial bypasses CPS counter)
- Fixed possible freeze

**V4.0**
- Added CPS use only on entity setting (before it was always true V3.0-)
- Fixed a very rare crash
- Auto save settings

**V4.1**
-  Fixed aim step Y (was the default value instead of the customized value)

**V5.0**
- Added console windows output
- Right click desactivate aimassist during a fight
- Fixed aim step Y in report showing the default value
