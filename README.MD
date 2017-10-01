![header](gitresources/header.png)

# N3RO's silent hack
`Last stable version: 4.0`

`Forge dev version: 1.8.9-11.15.1.1722`

`Forge client-side compatibility: 1.8.9-xxxx`


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

## Values

The recommanded values are the default one.

### Default
- AimRefreshRate(x/sec):200
- AimStepX:10 (V3.0+: 4)
- AimStepY:10 (V3.0+: 4)
- AimRange:5
- AimRadiusX:60
- AimRadiusY:30
- CPSIncrement:1
- CPSchance:80
- CPSBypass(Artificial/Human):false
- CPSOnlyOnEntity:false
- ActivationTime(sec):1.5
- UseOnMobs:false
- TeamFilter:true
- ClicksToActivate:3
- TimeToActivate(ms):1000
- AimAssist:true
- ClickAssist:true

### Maximums
- AimRefreshRate:1000
- AimStepX:80
- AimStepY:80
- AimRange:100
- AimRadiusX:180
- AimRadiusY:90
- CPSIncrement:10
- CPSchance:100
- CPSBypass(Artificial/Human):true
- CPSOnlyOnEntity:true
- ActivationTime(sec):unlimited
- UseOnMobs:true
- TeamFilter:true
- ClicksToActivate:unlimited
- TimeToActivate(ms):unlimited
- AimAssist:true
- ClickAssist:true

### Minimums
- AimRefreshRate:1 (0=OFF)
- AimStepX:1 (0=OFF)
- AimStepY:1 (0=OFF)
- AimRange:1 (0=OFF)
- AimRadiusX:1 (0=OFF)
- AimRadiusY:1 (0=OFF)
- CPSIncrement:1 (0=OFF)
- CPSchance:1 (0=OFF)
- CPSBypass(Artificial/Human):false
- CPSOnlyOnEntity:false
- ActivationTime(sec):0.1 (0=OFF)
- UseOnMobs:false
- TeamFilter:false
- ClicksToActivate:2
- TimeToActivate(ms):1000
- AimAssist:false
- ClickAssist:false

### Types
- AimRefreshRate:int
- AimStepX:float
- AimStepY:float
- AimRange:float
- AimRadiusX:float
- AimRadiusY:float
- CPSIncrement:long
- CPSchance:int
- CPSBypass(Artificial/Human):boolean
- CPSOnlyOnEntity:boolean
- ActivationTime(sec):float
- UseOnMobs:boolean (true/false)
- TeamFilter:boolean (true/false)
- ClicksToActivate:int
- TimeToActivate(ms):int
- AimAssist:boolean
- ClickAssist:boolean

## Commands

`INSERT` Refresh config

`HOME` Toggle aim assist

`PAGE_UP` Toggle click assist (V2.0 and higher)

`END` Debug message

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
- [ ] CheatSmasher bypass
- [ ] Console warning system (invisible coming / someone close / aim activated / ...)

## Support

Error codes : 1 = okay ; 0 = error

[abcdefghijklmnopq]
- a: The file "launcher_log(1)" is not in the .minecraft directory
- b: Could not create "launcher_log(1)" file
- c: Syntax incorrect (should be: "description:value")
- d: Could not load aim step x value
- e: Could not load aim step y value
- f: Could not load aim range value
- g: Could not load aim radius x value
- h: Could not load aim radius y value
- i: Could not load cps increment value
- j: Could not load cps chance value
- k: Could not load activation time value
- l: Could not load use on mobs value
- m: Could not load team filter value
- n: Could not load click to activate value
- o: Could not load time to activate value
- p: Could not load aim refresh rate value
- q: Could not load cps bypass value
- r: Could not load cps only on entity value
- t: Could not load aimassist value
- u: Could not load clickassist value

## Changelog

**V1.0:**
>- Personnalistion parametres
>- AimAssist

**V1.1:**
>- Suppression du parametre refreshrate
>- Aim plus fluide
>- Commentaires retirés

**V1.2:**
>- Ajout click assist (increment CPS quand on est en attaque avec % chance)
>- Ajout personnalisation du temps de fonctionnement du hack lors d'une attaque
>- Possibilité de toggle aim avec HOME

**V1.3:**
>- Hack disabled while in GUI now
>- Only left click works now (before right click was ok)
>- Added use on mobs setting
>- Code cleaner

**V1.4:**
>- Debug key (END)
>- Improved setting loader

**V1.5**
>- Fixed debug

**V1.6**
>- Added team tag filter on aim assist and click assist

**V1.7**
>- Fixed hack (was not working anymore with team filter turned on)
>- The hack stops automatically when no target is found after the beginning of the timer
>- Optimized team filter (It does not call useAimAssist & useClickAssist when the target is a teammate) 

**V2.0**
>- Fixed aim toggle
>- Improved hack activation: now it activates when the player is playing (before it worked while he was in Gui / outside minecraft)
>- Activation settings: time & number of attacks
>- Fixed settings not loaded while starting (before you had to reload to use your settings)
>- Added "PAGE_UP" key to toggle click assist
>- Added aim assist and click assist activated status in report

**V2.1**
>- Changed mod from "better shadows" to "better colors"

**V3.0**
>- Very smooth aim
>- Aim refresh parameter
>- Human / Artificial click assist parameter (artificial bypasses CPS counter)
>- Fixed possible freeze

**V4.0**
>- Added CPS use only on entity setting (before it was always true V3.0-)
>- Fixed a very rare crash
>- Auto save settings