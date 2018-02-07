Since this is an early version, all there is is a small text readme.

Basics:
----------
The core of SkyBash is intended as a standalone or future integrated replacement to the Bashed Patch.  If the coders who know Wrye Bash's internals best return, or those of whom can understand the code have more free time to develop the basic CBash, I'll consider this a fun project that I've been writing.

SkyBash performs subrecord level merging to merge data between various mods.  To put it into context; if two mods edit an NPC, and one edits the hair, and one adds the follower faction - you would get only one change with Skyrim's mod handling system.  Utilizing SkyBash, those two mods would be merged - and you would get the edited hair and the added faction.

SkyBash currently supports merging NPC's, Races, and Weapons.  It does not remove entries from sections applicable (such as a mod removing a vanilla item - it will update all counts, but not remove any items from an inventory).

Upcoming plans are; parsing the BOSS masterlist for bash tags, data locking on imports as a bash tag, removing entries when applicable, GUI for marking mods for import and with bash tags.  Of course, and more types to import.

Usage:
----------
SkyBash requires Java 7.  If you get an error about a main class missing, you need to get Java 7.  Java 7 has several features that simplified the coding process dramatically and it performs much faster (30-40% faster in some cases) with types of code being used in the program.

Once you have Java 7 (or if you already have it), navigate to the folder it is installed into and run the SkyBash.jar.  If you want custom bash tags, the SkyBash.ini has a format listed inside it for listing tags.  A file with the tags list is included in the download.

Installation:
----------
Drop the included SkyProc Patchers folder inside the Skyrim\Data folder.  The final path to the SkyBash.jar should be Skyrim\Data\SkyProc Patchers\SkyBash\SkyBash.jar.  Mod managers should put it in the correct place automatically.

Once it is in the correct place, navigate to the SkyBash.jar and run it.

Uninstallation:
----------
Delete the Bashed Patch, SkyProc.esp and the associated SkyProc Patchers folder.  Consistency data is stored in the Skyrim folder in My Games\Skyrim\SkyProc, close to where the Skyrim.ini and SkyrimPrefs.ini are located.  If not reinstalling, consistency can be deleted.