**V7.4.1 MC_1.16.4**

- Fixed loading crash for some users

**V7.4.0 MC_1.16.4**

**Modules:**
- Added Auto Right Click (#75): can be used to fast place blocks for speed bridging
- Added Auto Use (#73) *experimental*: can be used as an auto pot, auto rod, auto soup for example
- Added Panic (#76): it turns all the modules off (it does not write the changes to the config file, you can load the config file to restore)
- Added Speed Bridging (#72) *experimental*: people usually call this scaffolding

**Bettercolors engine 1.1.1:**
- Fixed synchronization issue with sliders
- Fixed toggle key display issue when a module's toggle key is not set

**V7.3.0 MC_1.16.2**

You can use the same settings file as before, but it is recommended to reset it to default since some settings changed,
and some were renamed (see below).

**Bettercolors engine 1.1.0:**
- Added a new tab to change the toggle key of each module
- Updated the theme: new icons and better layout
- Support for linux
- Support for fabric
- Better events system **Modules can be now optimized a lot**
- Options now use spaces instead of "_"'s

**Core:**
- Reach:
    - Improved reach
    - Removed reach_increment: replaced by "combat_reach" and "block_reach"
    - Changed max attack reach from 1.5 to 3.0 (you can go up to 6.0 reach which is not recommended lol)
- Triggerbot:
    - If the item has no attack cooldown (ex: hand), auto CPS will be 6 instead of 10
- Aim Assistance (has been re-coded):
    - Added "Sticky": assist if aiming an entity
    - Modified "Stop when reached": assist if not aiming an entity
    - Removed "time_to_activate" & "clicks_to_activate": (replaced by "cps_to_activate")
    - Removed "refresh_rate"
    - Renamed "step X" and "step Y" to "force X" and "force Y"
    - Renamed "radius X" and "radius Y" to "fov X" and "fov Y"
    - **A lot of improvements**
    - **A lot optimizations**
- Click Assistance (has been re-coded):
    - Renamed "additional_clicks" to "additional_cps"
    - **A lot of improvements**
    - **A lot of optimizations**
- TeamFilter: removed "teamfilter" option from every module. It is now a module, so when turned on, it works on every other module.
- Added Antibot:
    - If the entity is not a player -> bot
    - "Tab_check": If the entity is not in the tab -> bot
    - "Ping_check": If the entity ping is 0 ms -> bot
    - "Ticks_lived_check": If the entity has lived less than "Ticks_lived_min" -> bot
    - "Has_been_hit_check": If the entity has not been hit (did not receive damage yet) -> bot
- Miscellaneous:
    - You can now use greater values than the ones given by the UI. To do so, you need to update the config file manually.
    It's not recommended, but I'm sure that some of you will like that.
    - Debug mode now shows the GUI on startup
    - You can put your cursor over an option to get a description of it
    - You can put your cursor over a module to get a description of it
    - Improved modules messages in the console
    
**V7.2.0 MC_1.16.1**

Built with forge 32.0.108. You can use the same settings file as before.

- Added an option for the triggerbot to attack when it's fully charged ("auto cps")
- Modules are now paused when using GUIs (it wasn't working sometimes)
- Optimized click assistance & triggerbot

**V7.1.0 MC_1.16.1**

Built with forge 32.0.107. You can use the same settings file as before.

- Added **triggerbot** (#58)
    - "Use on mobs" option
    - "Clicks per seconds (CPS)" option (it is random, but near the given CPS)
    - It can't be used along with click assistance
- Fixed a weird bug where all the modules would stop working (isShowingDeathScene is always true for some reason. Is that an issue with forge?)
- Updated engine to 0.4.0 (preview). The only things that change for you are the themes

**V7.0.0 MC_1.16.1**

You can use the same settings file as before.

- Added **Reach** (#52)
- Added a **Friendlist** (#14)
- Added a button to reset the settings to default (#8)
- Added warnings when turning on some modules
- Added a "debug" option (false by default)
- Updated themes (oceanic, dark and light)
- Improved console messages
- Improved config loader (config files are backwards compatible now)
- Improved window's layout

**V6.1.2 MC_1.16.1**
- Updated mod to work with MC1.16.1
- Fixed aim assistance from MC1.15.2_V6.1.1

**V6.1.1 MC_1.15.2**

*Developer notes:*
- Reformatted every single java file
- Gui:
    - Improved stability (small changes to code design)
    - Improved font loading (console)
- Miscellaneous:
    - Added the ability to run tests with JUnit 5 (see VersionTest for an example)
    - Re-coded the version checker
- Aim Assistance:
    - Re-written some functions to be easier to understand
   
*User notes:*
- Improved overall stability
- Gui:
    - Changed the font from Cascadia to CascadiaCode 2005.15
- Aim Assistance:
    - Fixed range (was maxed at 10)

**V6.1.0 MC_1.15.2**
- Same thing as 1.14.4, but for 1.15.2

**V6.1.0 MC_1.14.4**

This update is backwards compatible with V6.1.0-b1 MC_1.14.4. The settings files are compatible with any Minecraft version.
-> You can use the same settings file for MC1.8.9 as MC1.14.4 for example.

- Added 3 new themes: Material Light, Material Oceanic, Material Gold (in addition to the Default one)
- Added 'report a bug' menu item
- Moved update checker to the menu (instead of the footer)
- Changed console font to Cascadia (with ligatures & emojis)
- The selected settings file is now saved and loaded during startup
- Fixed window size to match different themes
- Fixed non-bettercolors settings file being shown in the settings tab
- Fixed wrong version difference computation (#39)
- Only bettercolors' settings files are now shown in the settings selector (#40)
- Loading settings now automatically update the toggle key (#41)
- Improved toggle key selector (#41)

**V6.1.0-b1 MC_1.14.4**

This update changes the settings directory. So if you want to keep your custom settings, move your settings files to the config/
folder instead of the bettercolors folder. 

- Fixed "Stop on right click stays activated even if toggled off" (#29)
- Added "write changelog of last version in console if the mod is out dated" (#30)
- Increased the size of the console (100 -> 200)
- Added an option to change the toggle key (insert by default) (#15)
- Changed settings directory to config/ instead of bettercoolors/ (#36)

**V6.0.1 MC_1.14.4**

- Updated to the latest forge stable version (28.1.0)
- Fixed "missing forge files" during build due to forge's package layout change (#27) - thanks @patricktelling
- Changed modid to make sure that bettercolors is not recognizable (#28) - thanks @patricktelling

**V6.0.0 MC_1.14.4**

Exactly the same version as V6.0.0 MC_1.13, but for Minecraft 1.14.4

**V6.0.0 MC_1.13.2**

Forge 1.13.2 seems to be stable at this point.
This update is backwards compatible with settings files from v6.0.0-b2 and higher.

- Added error messages in the console if some resources were not loaded
- Improved font rendering (antialiasing)
- Improved error messages (they can be sent even if the window is not ready)
- Fixed crash in mod menu (#21)
- Fixed team filter (#20)
- Fixed & improved version checker
- Fixed resource loading (forge fixed the resource loader, so I had to remove the trick to get it working)
    
**V6.0-b3**
- Autosprint :
    - Fixed player sprinting backwards (#11)
- Autosword :
    - Fixed autosword automatically toggling on even if not attacking living entities (#12)
- Miscellaneous
    - Fixed a rare crash probably linked to the fact that there was too much text in the console (#13)
    - Improved version checker
- Interface
    - Added margin in tabs (#9)
    - Changed default toggle key from right shift to insert
    
- Developer section :
    - Changed versioning to semantic versioning
    - Improved documentation to set up the project

**V6.0-b2**
*Settings file of this version are incompatible with the precedents*
- Aim assistance :
   - Fixed random shifts not calculating at each click (#6 - #4)
   - Improved default settings
- Auto sprint :
    - Fixed a crash (#5)
- Settings :
    - Fixed CA team filter and AA team filter (#3)
    - Improved settings design to prevent conflicts
- Interface :
    - Improved option names

**V6.0-b1 : MAJOR UPDATE**
*Everything has been recoded from scratch. Now adding new modules is extremely easy and everything is optimized.*
- Interface :
    - New external interface made to contain a lot of modules,
    - Dynamic components : you can resize it the way you want,
    - A settings management section : you can save, load, and create settings presets.
- Modules :
    - Aim assistance :
        - Added "Stop when reached" option
    - Click Assistance :
        - Fixed the fact that it sometimes stays activated whereas it shouldn't
    - Autosword : automatically use the best sword in your hotbar,
    - Autosprint : automatically sprint when going forward.
- Misc :
    - Overall optimisation,
    - Settings file is now in the game directory,
    - Version checker.

**V5.1**
- Added HWID verification
- Added right SHIFT key to toggle the GUI which is closed by default

**V5.0**
- Added console windows output
- Right click deactivate aimassist during a fight
- Fixed aim step Y in report showing the default value

**V4.1**
-  Fixed aim step Y (was the default value instead of the customized value)

**V4.0**
- Added CPS use only on entity setting (before it was always true V3.0-)
- Fixed a very rare crash
- Auto save settings

**V3.0**
- Very smooth aim
- Aim refresh parameter
- Human / Artificial click assist parameter (artificial bypasses CPS counter)
- Fixed possible freeze

**V2.1**
- Changed mod from "better shadows" to "better colors"

**V2.0**
- Fixed aim toggle
- Improved hack activation: now it activates when the player is playing (before it worked while he was in Gui / outside minecraft)
- Activation settings: time & number of attacks
- Fixed settings not loaded while starting (before you had to reload to use your settings)
- Added "PAGE_UP" key to toggle click assist
- Added aim assist and click assist activated status in report

**V1.7**
- Fixed hack (was not working anymore with team filter turned on)
- The hack stops automatically when no target is found after the beginning of the timer
- Optimized team filter (It does not call useAimAssist & useClickAssist when the target is a teammate)

**V1.6**
- Added team tag filter on aim assist and click assist

**V1.5**
- Fixed debug

**V1.4:**
- Debug key (END)
- Improved setting loader

**V1.3:**
- Hack disabled while in GUI now
- Only left click works now (before right click was ok)
- Added use on mobs setting
- Code cleaning

**V1.2:**
- Added click assist (increment CPS when attacking with chance %)
- Added attacking time customization (= time in which the hacks are toggled)
- Added aimassist toggle system with HOME key

**V1.1:**
- Smoother aimassist
- Refreshrate setting removed
- Removed comments

**V1.0:**
- Settings customization
- AimAssist