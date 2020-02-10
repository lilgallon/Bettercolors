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

**V5.1**
- Added HWID verification
- Added right SHIFT key to toggle the GUI which is closed by default

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

**V6.0.0 MC_1.8.9**

This update is backwards compatible with settings files from v6.0.0-b2 and higher.

- Added error messages in the console if some resources were not loaded
- Improved font rendering (anti aliasing)
- Improved error messages (they can be sent even if the window is not ready)
- Fixed & improved version checker

**V6.0.1 MC_1.8.9**

This is an **important** security update. It also fixes some bugs.

- Changed modid to make sure that bettercolors is not recognizable (#28) - thanks @patricktelling
- Fixed stop on right click that stayed activated even if toggled off (#29)
- Fixed aim assistance that was not turned off when the user was in the pause menu (#25)

**V6.1.0 MC1.8.9**

This update changes the settings directory. So if you want to keep your custom settings,
move your settings files to the config/ folder instead of the bettercolors/ folder (#23).

- Added an option to change the toggle key (insert by default) (#15)
- Added "write changelog of last version in console if the mod is out dated" (#30)
- Added 3 new themes: Material Light, Material Oceanic, Material Gold (and the Default one)
- Changed console font to Cascadia (with ligatures & emojis)
- Increased the size of the console (100 -> 200)
- The selected settings file is now saved and loaded during startup
- Fixed window size to match different themes
- Fixed non-bettercolors settings file being shown in the settings tab
- Fixed wrong version difference computation (#39)
