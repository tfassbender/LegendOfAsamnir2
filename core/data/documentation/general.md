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
				- (?) A puzzle where Thorin has to move through some iglus, where it's not directly clear which entry leads to which exit
				- Use pressure switches to move a movable object on a frozen lake, to open a gate
					- The frozen lake cannot be entered by the player, but the switches must be used
					- The movable object doesn't stop while moving on the ice (like Thorin without the ice-pick)
					- Could use the boomerang, the Dwarven Guardian Construct fists from level 1 or a magic switch (the magic switch can be activated to not block the way)
					- The movable object could be moved in a direction if no force is applied (like a pinball, where the dwarven guardian construct's fist is the starter)
- Textures:
    - Final part (maybe use ice tileset here):
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
    - Maybe use desert tileset (for a part of the level)?
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
- Textures:
    - Skeletal Warior
	- Skeletal Archer
	- Skeletal Mage
	- Skeleton King
	- Necromancer 
	- Naga
	- Ghoul
	- Vargouille
	- Lich (boss?)
	- Black Pudding (?)
	- Plague Doctor (?)
	- Banshee (?)
	- Cultist Dungeon Tileset
	- NPCs: 
	    - Dwarf Undead (only as a joke, because thorin payed with his live to enter Helheim)
		- Mideval Executioner


### Level 5
	
- The final level is a castle in the fields of Vigrid (where the Chaos Wizard lives)
- The level shouldn't be too big, but consist of only two boss fights
    - The first boss fight is agains some monster (maybe an Elder Dragon, with some helpers)
	- The second boss fight is against the chaos wizard (who has ~3 different forms)
- When Thorin reaches the Chaos Wizard, they first talk (so I can add many bad jokes):
    - "The princess is an another castle" (but one can see the princess behind a wall because of the 2D graphics)
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






