**V7.4.0 MC_1.16.4**

*You need fabricloader >= 0.9.0*

- Added Auto Right Click (#75): can be used to fast place blocks for speed bridging
- Added Auto Use (#73) experimental: can be used as an auto pot, auto rod, auto soup for example
- Added Panic (#76): it turns all the modules off (it does not write the changes to the config file, you can load the config file to restore)
- Added Speed Bridging (#72) experimental: people usually call this scaffolding

**V7.3.0 MC_1.16.4**

It is the same version as V7.3.0 MC_1.16.2. The engine has been updated to 1.1.1. You need:
- fabricloader >= 0.9.0
- mc: 1.16.4

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
- Miscellaneous:
    - You can now use greater values than the ones given by the UI. To do so, you need to update the config file manually.
    It's not recommended, but I'm sure that some of you will like that.
    - Debug mode now shows the GUI on startup
    - You can put your cursor over an option to get a description of it
    - You can put your cursor over a module to get a description of it
    - Improved modules messages in the console

**V7.2.0 for Minecraft 1.16.2 (fabric)**

It is the same version as 7.2.0 for Minecraft 1.16.2 (forge). The same settings file can be used.

7.2.0 additions:
- Added an option for the triggerbot to attack when it's fully charged ("auto cps")
- Modules are now paused when using GUIs (it wasn't working sometimes)
- Optimized click assistance & triggerbot