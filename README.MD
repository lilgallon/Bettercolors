<h3 align="center">
  <img src=".github/header.png">
</h3>
 
<p align="center">
    <a href="https://img.shields.io/badge/forge_dev-1.14.4_28.1.0-green.svg"><img alt="Forge dev version: 1.14.4-28.1.0" src="https://img.shields.io/badge/forge_dev-1.14.4_28.1.0-green.svg"/></a>
    <a href="https://img.shields.io/github/release/n3roo/bettercolors.svg"><img alt="Release" src="https://img.shields.io/github/release/n3roo/bettercolors.svg"/></a>
    <a href="https://img.shields.io/github/downloads/n3roo/bettercolors/total.svg"><img alt="Downloads" src="https://img.shields.io/github/downloads/n3roo/bettercolors/total.svg"/></a>
    <a href="https://img.shields.io/github/issues/n3roo/bettercolors.svg"><img alt="Open issues" src="https://img.shields.io/github/issues/n3roo/bettercolors.svg"/></a>
    <a href="https://img.shields.io/github/issues-closed-raw/n3roo/bettercolors.svg"><img alt="Closed issues" src="https://img.shields.io/github/issues-closed-raw/n3roo/bettercolors.svg"/></a>
    <a href="https://img.shields.io/github/issues-pr-closed/n3roo/bettercolors.svg"><img alt="Closed pull requests" src="https://img.shields.io/github/issues-pr-closed/n3roo/bettercolors.svg"/></a>
</p>

<p align="center">
    <a href="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.8.9/badge.svg"><img alt="Build Status" src="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.8.9/badge.svg" /></a>
    <a href="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.12.2/badge.svg"><img alt="Build Status" src="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.12.2/badge.svg" /></a>
    <a href="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.13.2/badge.svg"><img alt="Build Status" src="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.13.2/badge.svg" /></a>
    <a href="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.14.4/badge.svg"><img alt="Build Status" src="https://github.com/N3ROO/Bettercolors/workflows/Build%20MC1.14.4/badge.svg" /></a>
</p>

A silent "hack" built within the forge API. It bypasses powerful anticheats (forge mods and servers) to help you improve your gameplay.

<p align="center">
    <img alt="features" src="https://raw.githubusercontent.com/N3ROO/Bettercolors/MC_1.8.9/.github/features_main_crop500.png" />
</p>

## Last update

*New themes, fixes and improvements.*

<p align="center">
    <img alt="themes" src="https://raw.githubusercontent.com/N3ROO/Bettercolors/MC_1.8.9/.github/new_themes_crop500.png" />
</p>

## Disclaimer

I made this project to learn Java in a funny way. I started 2 years ago but the other repositories are, and will stay private. Only this repository will stay public because it can be used by newbies to improve their aim and their click rate without giving them a huge advantage. This has been made for educational purposes.

**Please make sure that the server you are playing on allows the use of this mod ! Use it at your own risk. I won't be responsible for anything you do wrong with this mod.**

## Features description

⚔️ **Aim assistance :** A smooth and human-like aim assistance 100% undetectable by any anticheat. It is invisible since it looks absolutely human. You can record videos while using it without any problem.

🖱️ **Click assistance :**  A human-like click assistance that sends REAL clicks, to help you click faster. It can bypass forge CPS counter mods by toggling packets.

📟 **External interface :** A clean external interface to configure the mod outside the minecraft client, so you can record without any problem !

🎨 **Theme chooser:** 4 Themes are waiting for you: Default, Material Light, Material Oceanic and Material Gold.

📋 **Console :** Curious ? There is a console showing everything happening in the mod. 

🎎 **Teammate detection :** The mod find your teammates from any server game mode, and prevent modules to be activated on them.

🛠 **Settings :** You can configure every mod and all is saved in real time.

⚒ **Settings preset :** You can save, load, and create settings presets from a cool interface.

## Commands

- `HOME` : Toggle aim assist
- `PAGE_UP` : Toggle click assist
- `INSERT` : Toggle GUI

## Features help

- Aim assistance :
    - "Stop on right click" : it stops the aim assistance if you right click
    - The aim speed is defined by refresh rate,
    - The aim force is defined by step X and step Y,
    - The aim range sets the maximum distance to aim an entity,
    - The aim radius is the FOV in which you will aim at an entity,
    - "Stop when reached" : it means that when you aim at an entity, the aim assistance stops

- Click assistance :
    - "Packets" : Send packets instead of REAL clicks (**bypasses forge anticheats mods but riskier on server side**)
    - "Only on entity" : It will click only if you are aiming an entity,
    - Additional clicks define how many clicks will be sent in addition to yours,
    - Chance is the probability of every artificial click to be sent,
    - Duration corresponds to the activation time of the click assistance,
    
- Common :
    - "Use on mobs" : Defines if the modules take into account mobs,
    - "Team filter" : Defines if the modules take into account teammates,
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
| AA_StopWhenReached          | false         | false       | true      | boolean |
| AA_Duration(ms)             | 2000          | 0           | 100000    | integer |
| AA_ClicksToActivate         | 2             | 0           | 20        | integer |
| AA_TimeToActivate(ms)       | 700           | 0           | 100000    | integer |
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

This section show you how to get a working development environment.

### 1. Download your favorite IDE

I did not try setting up forge with [Eclipse](https://www.eclipse.org/neon/). I encourage you to use [IntelliJ](https://www.jetbrains.com/idea/) that is way better java IDE than Ecplise (that also has a free community edition).

### 2. Set up forge & IDE

- Clone this project `git clone <link>`
- Go in the project directory

- Now if you use eclipse (this should be enough, I'll be happy to accept a pull request with better explanations):
    - Open a terminal and type `.\gradlew setupDecompWorkspace`
    - Open a terminal and type `.\gradlew eclipse`
    - Open eclipse and switch your workspace to eclipse/

- And if you use [IntelliJ IDEA](https://www.jetbrains.com/idea/download/):
    - Open IntelliJ IDEA, and open the "build.gradle" file as a project
    - A window will show up, change nothing but "create separate module per source set" that needs to be **disabled**, then click ok (make sure that the Gradle JVM is java < 11). If the dialog does not show, then, go to `.idea/gradle.xml` and write `<option name="resolveModulePerSourceSet" value="false" />` in `<GradleProjectSettings>`. **This is important**, otherwise the minecraft & forge libs may not be mapped correctly.
    - Wait for IntelliJ to start and to download gradle stuff (it can be long, be patient)
    - Open a terminal and type `gradlew --stop` to make sure that gradlew daemon is not running (it may cause issue later)
    - Open the gradle tab (on the right, in the IntelliJ window), right click on "setupDecompWorkspace" (in Tasks/forgegradle), and click on "create bettercolors [setup...]", now in vm option, add "-Xmx4g -Xms4g", click apply, and then click on run (at the top left of IntelliJ window)
    - Open a terminal and type `gradlew genIntellijRuns`
    - Next to the "run" button, select "ADD CONFIGURATION" and select runClient (in Application tab). Then, in "Use classpath of module:", select `dev.nero.bettercolors.main.Bettercolors`. Click ok. Now you can run the client. (the first run takes time, you have to wait a bit).
    - If it does not work, try to run `gradlew clean`, and then redo everything from step 3

When you make a change in build.gradle, make sure to run `gradlew clean`.
If your JAVA_HOME points to a java version below 8 or above 11, replace `gradlew` by `gradlew -Dorg.gradle.java.home="path/to/javaSDK >8 & <11"`.

Now it should be done. Please refer to [FORGE-INSTRUCTIONS.txt](FORGE-INSTRUCTIONS.txt) if you have any problem (my explanations are more detailed than the official ones).

### 3. Build the .jar

I could not make the build working properly with an external library, so here is what you need to do:
- Open a terminal and write `.\gradlew build`.
- Open `build/libs/bettercolors-6.x.x.jar` with winrar (or something like this)
- Go to `META-INF/libraries/material-ui-swing-x.x.x_x`
- Copy all these files into `bettercolors-6.x.x.jar`

### 4. Contributing

If you want to contribute, make sure to :
- keep the same coding style that is already used (plus headers describing every function),
- describe what is the purpose of your pull request.

### 5. Credits & Acknowledgements

- [@N3ROO](https://github.com/N3ROO)  - *Main developer*
- [@patricktelling](https://github.com/patricktelling) - *Pointed out important issues (#27 & #28)*
- [@shorebre4k](https://github.com/shorebre4k) - *Added option to change the GUI's toggle key (issue #15 & pull request #35)*
