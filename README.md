# MarioParty6
    
Reverse engineering project to merge the minigame libraries of Mario Party 6 and Mario Party 7 (Gamecube). Both games run on the same engine with similar assets, so in theory their minigame content can be combined.
    
## What it does
    
RepointREL.java — Reads an address mapping table (reverse-engineered via Ghidra decompilation) and translates relocation tables between MP6 and MP7 .rel binaries. The code section is copied through, then every address in the relocation table is remapped from one game's memory layout to the other's. Unknown addresses are flagged and abort the translation to prevent silent corruption.
    
SearchHexes.java — Recursive byte-pattern searcher that walks extracted game binaries looking for specific addresses or strings, with optional wildcard support.
    
## The goal
    
Decompile both games' minigame selection logic (a single number range deciding which minigame loads), expand the range to support minigames from both titles, and remap address references so the patched binary runs on console/emulator. Swapping minigames that share identical assets between MP7 and MP6 was achievable; full asset porting across versions proved much harder.
    
## Tech
    
Java, Ghidra reverse engineering, Gamecube REL binary format, hex editing
    
## Status
    
Shelved. A couple of minigames were successfully swapped, but full content merging stalled on asset compatibility between the two titles.
