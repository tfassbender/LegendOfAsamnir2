# Legend of Asamnir 2 - Rise of the Chaos Wizard


## Story

- Basics:
    - Continue the story of the first part: Thorin is back in Svartalfheim, and still has the five runes and the items from the prequel
	- The "Strange Narrator Guy" from the prequel is the "Chaos Wizard"
	    - I add myself into the game as the programmer of the game
		- Many possibilities for bad jokes

- Intro: 
    - Thorin awakens in his house in Svartalfheim where he is woke up by an alarm clock (tutorial: destroy the alarm clock)
    - Thorin doesn't remember having an alarm clock (because it was created by the chaos wizard)
	- Then Thorin is requested by the Dwarven King 
	    - He first has to find some mead, and armor and something to eat as part of the tutorial
	    - To be faster he has to run (explain running by double clicking the direction button)
	- At the castle of the king, Thorin finds the Chaos Wizard, who steals the princess and explains his quest (and also that the "Chaos Wizard" is the creator of the game)
	    - The "Chaos Wizard" forgot to add the Princess to the game first and patches that on the fly
	- Thorin is not interested in saving the princess, so the Chaos Wizard also steals his battle axe "Asamnir"



## General Ideas

- Some new Items need to be added (or maybe existing items can be improved ?)
  - An ice pick that is needed to get up a slippery stairway or to stop on an icy way (without the item you cannot stop or change your movement anymore) (in Niflheim)
  - A rope tool (or "grapping hook" / "hookshot") (like in the old legend of zeldas) could be used in Muspelheim to cross lava rivers
	- The hookshot could also be in combination with the rope, to connect to things that would be out of reach otherwise
  - A compas, that can be used to find a path in an area where the lantern isn't enough to light up the path (in Helheim)
    - Could also be used to show the direction to the nearest Triforce piece (if any is present on the map)
- Use something like ForceFields (see boss fight of level 1) in a puzzle, where a projectile weapon of the player has to be redirected (pushed by the force field) to hit a target
- add a loading screen or splash screen while the main menu is loading (takes quite a while now)
	- or use a new texture atlas for the main menu that loads faster


## Side Quests

- Collect the chickens (level 2)
- Find pieces of the Triforce (side quest by an Elf https://elthen.itch.io/2d-pixel-art-elven-archer-warrior) 
  - If the player collects all pieces of the triforce the elf gives the player a piece of information that is needed in the final level to reach the chaos wizard
  - Like in "Links Awakening" where the side quest leads to the information on which route to take in the final dungeon to reach the boss
  - Some hints where the remaining parts of the triforce can be found should be added as a dialog option
  - One piece should be buyable in the shop for a quite high price



## Levels

- Four Runes are left, so four levels are needed (and a fifth with a boss fight)
    - See [Runes](#to-be-added)
- Final (fifth) level consists only of a cutscene and the boss fight (defeat the Chaos Wizard)
	- A rainbow bridge leads to the fields of Vigrid
    - A castle on the fields of Vigrid is the last dungeon


### Intro

- Intro level consists of only tutorial and story
- Needed map parts:
    - Thorins house (simple - one room)
	- Svartalfheim:
	    - The central map from which all dungeons can be reached
		- Not to big - more direct ways to the dungeons than in the prequel
		- A village where all the dwarfs live
	- A castle where the Chaos Wizard steals the princess (part of the village in Svartalfheim)
- Needed textures:
    - Something like an alarm clock (analog clock)
	- NPCs:
	    - One that tells Thorin to go to the castle
		- The princess that is stolen
		- The Chaos Wizard (not the final form, but something like the cultists or medieval-thief)
		- The dwarven king that tells Thorin to save the princess
		- castle guards

### Level 1

- first Thorin should try to get to the Chaos Wizard's castle directly
    - Thorin finds that he needs to be able to access the Bifröst, which leads him to the rune in the first dungeon
- in svartalfheim (not on the main map)
- in a dungeon that is accessible not too far away from the village
- Map: 
    - some dwarfs are already in the dungeon and crafting for ore or something similar (maybe a chance for some small side quests?)
	- add some castle parts to the map (maybe even 2 or more maps - split in multiple parts)
- some items / runes from the prequel should be needed in this dungeon (but not all of them yet)
- Textures:
    - mainly golems (small golem - free, golem - 2018 - 3$, giant golem - 2019 - 5$ - arcane golem - 2022 - 6$)
	- giant eyed monster (?) - 2019 - 6$
	- flying eye - free
	- gargoyle - 2020 - 3$
    - Shield Guardian
	- Dwarven Guardian Construct (not as enemy, but as tool to defeat the boss)
	- Beast
	- Minotaur


### Level 2

- Thorin can (again) try to get to the castle of the Chaos Wizard (enforce that)
- Thorin is able to get to the fields of Vigrid but he cannot enter the castle (the rune from the fourth dungeon is needed)
- Only the way to the second level is free
    - Third level is reachable, but thorin takes constant damage, which makes it not possible to win this level yet (rune of level 2 negates this constant damage)
	- Fourth level needs to be payed with a live (see [Runes](#to-be-added))
- Level is in Niflheim (cold area in the north - entry near Asgard / Vigrid)
- Map:
    - The map should not consist of only the dungeon, but also a view other things and NPCs
	- Use the ice tileset for the final part of the level
	- The map is split up into three parts
		- A main part, with a village
			- Not many enemies here (only bandits)
			- A village that needs help because of the bandits
				- The bandits have stolen many things from the village, including an ice-pick that is needed to proceed in the final part of this level
			- Chicken side quest (for a piece of the triforce)
		- A small map with a bandit camp, where thorin has to defeat the bandits and find the ice-pick
			- Use the Medival-Warcamp tileset for the bandit camp
			- Include a (small) puzzle that has to be solved to enter the bandit camp
			- Include a mini boss (bugbear)
				- The mini boss does only take damage from bombs (all other attacks are blocked - bugbear has a blocking animation)
				- The player gets an unlimited number of bombs
				- The mini boss runs awas from the bombs, what makes it difficult to attack him
				- After blocking an attack the mini boss stays in the blocking mode for a short while
				- The blocking mode of the mini boss must be used to prevent him from running away from the bombs
				- After falling below a given health amount (~ 10%) the mini boss retreats to the necromancer that supports him
				- The necromancer makes it impossible to throw bombs at the mini boss (using a force field like in the boss fight of level 1)
				- That's where Impa comes in (in a cutscene) and fires a hadouken and defeats the mini boss
		- The final part of the level (which is reachable without going through the bandit camp, but one cannot succeed there without the ice-pick)
			- Include some puzzles
				- At the beginning, a frozen lake has to be crossed, where thorin needs to use the ice-pick (to let the player know that he cannot succeed the level without the item)
				- Also a puzzle where the rope has to be used in combination with ice fields could be made (where thorin can change the direction with the rope)
				- Use pressure switches to move a movable object on a frozen lake, to open a gate
					- The frozen lake cannot be entered by the player, but the switches must be used
					- The movable object doesn't stop while moving on the ice (like Thorin without the ice-pick)
					- Could use the boomerang, the Dwarven Guardian Construct fists from level 1 or a magic switch (the magic switch can be activated to not block the way)
					- The movable object could be moved in a direction if no force is applied (like a pinball, where the dwarven guardian construct's fist is the starter)
		- Enemies:
			- Ice Elementals and Yetis as normal enemies
			- Wolf can be used as only countering enemy (like the gargoyle)
			- Frost Giant is the boss enemy:
				- Target is to make him throw his axe so he can be attacked without dealing much damage
				- Could be fought on an ice field, which makes it much more difficult to reach him
- Textures:
    - Final part (maybe use ice tileset here):
		- Penguin
        - Ice Elemental (small)
	    - Frost Giant
		- Yeti
		- Wolf
		- Santa (? - maybe only as joke, but not as actuall enemy)
    - Use common monsters in the other parts (since there are not many ice monsters)
		- Bandits
		- Bugbear (mini boss)


### Level 3

- Thorin can access this level after he went to the fields of vigrid for the first time (same time he's able to access level 2)
  - But he takes constant damage (from the heat), which is negated by the rune from level 2 (without this rune it's not possible to win this level)
- Level is in Muspelheim (hot area in the south)
- Map:
	- Divide into multiple parts (with unique tilesets each)
		- Desert tileset for the first part of the level
			- Quite small part
			- Only the entry to the second part (in a cave) and a small puzzle to reach it
			- Also place a part of the triforce here (a bit hidden)
			- Only a few cobolds and a pyromancer as enemies
		- Vocanic Lava tileset for the second part
			- A single map of medium size
			- One or two puzzles but more enemies
			- Add a new item here: hookshot - lets the player cross lava rivers at some points (where a target can be reached with it)
			- Send the player on a way is blocked by a lava river first - there should be a way to unlock a differen path where the hookshot can be found
			- Cobolds and fire elementals as enemies (hell critter?)
			- Add Phoenixling as an animal / enemy (like the gargoyle) - doesn't fight back; can be killed; revives from the ashes after the distance to the player is high enough
		- Lava Dungeon for the third part
			- Multiple small maps that are connected to a dungeon (like one of the old legend of zelda dungeons)
			- Puzzles accross the dungeon parts (use keys)
			- Imp and Demon as normal enemies - also some fire elementals
			- Ifrit (Surtur) as boss enemy
- Textures:
    - Hell Critter
	- Imp
	- Fire Elemental (small, medium)
	- Ifrit
	- Demon
	- Warlock & Hell Portal
	- Pyromancer
	- Kobold Pyromancer
	- Phoenixling (2024)


### Level 4

- Thorin has gained a Rune (in level 3) that can revive him after he died, so he can pay with his live to enter Helheim (level 4)
- Level is in Helheim (land of the dead)
- Three boss fights should be added:
	- Skeletal King
		- Boss of the first area, where mainly skeletons are to be found
	- Spider Queen
		- Boss of the second area, where some skeletons, cultists and many spiders are to be found
	- Lich
		- Boss of the third area, where more cultists and ghosts (wisp, vengefull spirit, ...) are to be found
	- After all three bosses are defeated, one can reach Hel's throne room
		- Every boss drops a special key (with a special texture - similar to the keys in slay the spire)
		- Hel (texture: Death) is an NPC, that does not want to fight (Hel should not be the antagonist)
		- Tells the player where to find the last rune (in a room near her throne room) and what to do next
		- Hel doesn't like the Chaos Wizard very much and therefore does not want to fight Thorin
		- After Thorin gains the last rune (Laguz), the Chaos Wizard is scared, because he thaught that Thorin would never defeat Hel
- Map:
	- The single parts of the map should not be too big, because the focus is on the three boss fights (but also not too small - it's not only the boss fights)
	- The map should consist of five parts:
		- The main map, from which the three boss areas and Hel's temple can be reached
			- This part of the map is quite small and does not contain any enemies
			- Only used as connection between the other parts and for some NPCs
			- Ratatosk appears in this part of the map
		- Skelet Zone
			- Some enemies to fight and some (easy) puzzles
			- At the end the player reaches the vault of the Skeleton King (boss room)
				- The player can take the key to Hel's temple before the boss fight
				- After the player takes the key, the Skeleton King appears from the ground and has to be defeated before the player can leave the area
				- The Skeleton King is a straight forward enemy 
					- Not many tricks, but only much health and some heavy attacks 
					- Maybe add a fiew minions, but not to many because that's the style of the spider queen
		- Spider Zone
			- Some enemies to fight and some (easy) puzzles
			- At the end the player reaches the nest of the spider queen, where he has to defeat her to get the key to Hel's temple
			- The Spider Queen mainly fights using the minions she spawns
				- Many spiders with low damage, to make the fight a bit longer
		- Cultist Dungeon
			- Make sure this is the last part of the map - the order of the other two is not important
				- The Compas could be given to the player after he adds the other two key parts to the key in Hel's temple
				- Without the Compas one can enter this zone, but can't get far in it
			- Not many enemies, but more puzzles in this area
			- The Compas should be used in this zone
				- At the beginning of the zone there should be a room full of traps with only one way through them
				- The path cannot be seen, but the compas is needed to find it
				- The traps all look the same but some are just dummies
				- All traps are insta-kill traps, so the path cannot be brute forced (or at least it would be realy annoying to try that)
			- The cultists (the small ones) are no enemies but try to block the player's path (they work as NPCs)
			- Some cultist brutes also work as enemies (one should be able to distinguish between them because of the size and a health bar)
			- At some points the cultists do sacrifice rituals to summon strong enemies:
				- A ritual to summon a vengefull spirit
					- Some cultists form a circle and sacrifice one of them
					- Maybe use this two or even three times, because this enemy is not too hard to fight
				- A ritual to summon the Lich (the boss) at the end of the level (just before the room, where the key to Hel's temple can be found)
					- Some cultists form a circle where in the middle of the circle one cultist is transformed to a cultist horror
					- The cultist horror has to be fought in the beginning of the boss fight (quite easy, straight forward enemy, but much health)
					- When the cultist horror is almost defeated, a cutscene starts
						- Some cultists have to somehow block Thorin from the summoning ritual (maybe using some kind of magic)
						- The cultists come back to the summoning area in which the cultist horror already stands
						- Some cultists sacrifice themselves to the cultist horror (use the devour animation - this should not be used before)
						- After devouring all of the cultists the cultist horror dies and the Lich appears
					- The Lich mainly fights with his magic (some projectiles, some novas and some attacks at the player's position - like in the fight against Surtur)
					- Also a fiew minions could be used (totems, wisps or magical weapons - but not too many)
					- The boss fight has to have a fiew parts, where the player has to reach the Lich first after he goes into a defense mode (like the fight agains Surtur)
					- Also add a final part, where the Lich is almost defeated and changes his attack pattern a bit
			- After the Lich is defeated, the player can take the key to Hel's temple and the dungeon is conquered
		- Hel's temple (Eljudnir)
			- Easy to reach from the main map (close to the entrance, so the player reaches it first)
			- One cannot enter the temple without the three keys (from the three boss fights)
			- In Hel's temple there are no fights or enemies anymore (only a fiew minion NPCs and Hel)
	- Maybe add some shortcuts from the three zones back to the main part of the map?
	- Each of the three Zones should contain one Triforce item
- Textures:
	- Skeletal Warior
	- Skeletal Archer
	- Skeletal Mage
	- Skeletal Trumpetist
	- Skeleton King (boss)
	- Necromancer 
	- Cultist
	- Cultist Brute
	- Cultist Horror
	- Mimics
	- Bat
	- Spider
	- Bloodsilk Spider
	- Spider Queen (boss)
	- Wisp
	- Vengefull Spirit
	- Lich (boss)
	- Death (as Hel)
	- Magic Wasteland Tileset
	- Cultist Dungeon Tileset
	- NPCs:
		- Dwarf Undead (only as a joke, because thorin payed with his live to enter Helheim)
		- Mideval Executioner
		- Plague Doctor
		- Succubus (maybe as enemy)
		- Incubus (maybe as enemy)


### Level 5
	
- The final level is a castle in the fields of Vigrid (where the Chaos Wizard lives)
- The level shouldn't be too big, but consist of only two boss fights
    - The first boss fight is agains some monster (maybe an Elder Dragon, with some helpers)
	- The second boss fight is against the chaos wizard (who has ~3 different forms)
- When Thorin reaches the Chaos Wizard, they first talk (so I can add many bad jokes):
    - "The princess is an another castle" (but one can see the princess behind a wall because of the 2D graphics)
	- The Chaos Wizard has borrowed the "Vorpal Laserblaster of Pittenweem" from a friend for the fight against Thorin
	- When the fight beginns: "So it beginns ... " (yes, another lord of the rings reference :D )
	- Thorin can't defeat the Chaos Wizard without asamnir 
		- "You have no power here, Thorin the Gray!!! *evil laughter*" 
		- Then Freydis manages to give Thorin the Axe Asamnir with which he is able to defeat the Chaos Wizard
	- After defeating the Chaos Wizard, thorin forgetts to save the princess, but goes after his axe Asamnir (cutscene)
	- When leaving with Asamnir and the Princess, the Chaos Wizard stands up again and talks about the sequel
	


## Runes

### Used

- Othala: Carry and use items
- Gebo: Sacrifice gold to buy supplies
- Ansuz: Solve puzzles (use switches, ...)
- Mannaz: Reflect magical attacks
- Algiz: See hidden paths

### To be added

- Raidho: 
    - Travel, end and beginning
	- opens the shurtcuts to other worlds where the other dungeons are
	- first dungeon (the only dungeon that is located in Svartalfheim - should be in a cave)
	- Svartalfheim
- Hagalaz: 
    - Balance of forces
	- prevents constant damage (from heat in muspelheim)
	- second dungeon
	- Niflheim
- Kenaz: 
    - Fire, regeneration
	- Revive after defeated (needs to be re-forged by Sindri and Brockr)
	- to open the way to the next dungeon (in Helheim) you have to pay with your live (so you must use the rune, to not actually die)
	- third dungeon
	- Muspelheim
- Laguz: 
    - Origin, test
	- Final Rune that opens the last gate (to the final castle)
	- fourth dungeon
	- Helheim






