![header](gitresources/header.png)

# N3RO's silent hack
`Last stable version: 2.0`

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
- AimStepX:10
- AimStepY:10
- AimRange:5
- AimRadiusX:60
- AimRadiusY:30
- CPSIncrement:1
- CPSchance:80
- ActivationTime(sec):1.5
- UseOnMobs:false
- TeamFilter:true
- ClicksToActivate:3
- TimeToActivate(ms):1000

### Maximums
- AimStepX:80
- AimStepY:80
- AimRange:100
- AimRadiusX:180
- AimRadiusY:90
- CPSIncrement:1000
- CPSchance:100
- ActivationTime(sec):unlimited
- UseOnMobs:true
- TeamFilter:true
- ClicksToActivate:unlimited
- TimeToActivate(ms):unlimited

### Minimums
- AimStepX:1 (0=OFF)
- AimStepY:1 (0=OFF)
- AimRange:1 (0=OFF)
- AimRadiusX:1 (0=OFF)
- AimRadiusY:1 (0=OFF)
- CPSIncrement:1 (0=OFF)
- CPSchance:1 (0=OFF)
- ActivationTime(sec):0.1 (0=OFF)
- UseOnMobs:false
- TeamFilter:false
- ClicksToActivate:2
- TimeToActivate(ms):1000

### Types
- AimStepX:float
- AimStepY:float
- AimRange:float
- AimRadiusX:float
- AimRadiusY:float
- CPSIncrement:long
- CPSchance:int
- ActivationTime(sec):float
- UseOnMobs:boolean (true/false)
- TeamFilter:boolean (true/false)
- ClicksToActivate:int
- TimeToActivate(ms):int

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
- [ ] Console warning system (invisible coming / someone close / aim activated / ...)

## Support

Error codes : 1 = okay ; 0 = error

[abcdefghijklmno]
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