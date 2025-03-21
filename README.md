
### Overview
This lightweight app serves as a testing ground for experimenting with new Android framework features. As a result, some suboptimal components or code structures may be intentionally used for exploration, and certain shortcuts may be taken since this is not intended for production.
### Background
The game is inspired by a mini-game based on the classic "Daleks" game. The objective is to destroy Enemies by making them collide with each other or using bombs. Daleks move towards the player every turn, and the player can move, teleport, or use bombs to avoid and destroy Daleks.

Inspiration:
- https://furfighters.fandom.com/wiki/Bear_Attack
- https://www.isaacsukin.com/sites/daleks/index.html

![Screenshot 2025-03-20 at 11 15 29 AM](https://github.com/user-attachments/assets/8d649891-fb87-44bb-a0cf-552d049805ae)

#### Gameplay:
- **Grid-Based Gameplay:**
  - Display a grid representing the game board.
  - Control module below the grid for player actions.

- **Player and Enemy Mechanics:**
  - Player can move up, down, left, or right.
  - Enemies move one square closer to the player after each move (including diagonal).
  - Collisions between enemies turn them into collision squares.
  - Enemies should not spawn within 2 tiles of the player.

- **Level and Score Management:**
  - Each new level starts with one additional enemy; collision squares are removed.
  - Reset board to the initial state of the level after player collides with an enemy.

- **Health and Special Actions:**
  - Player has 3 lives.
  - Teleport button (3 uses) relocates player to a random square.
  - Bomb button (3 uses) turns enemies in a 2-square radius into collision squares.
  - Player earns additional teleports and bombs by progressing through levels.

#### Possible future additions:
- Animations for player and enemy movements.
- Sound effects for movements and actions.
- Alternate game modes
- A back-end to allow leaderboards

### Scoring
- Start with 3 enemies at level 1.
- Score:
  + 1 point for every Enemy that runs into a pile or shielded player.
  + 1 point for every Enemy that is bombed.
  + 3 points for two Enemies colliding and creating a pile.
  + 5 points for three Enemies colliding and creating a pile.
  + Additional points for completing each level.

### Components
- Room database, Jetpack Compose

### Game theme link:
http://material-foundation.github.io?primary=%23C5B6A1&bodyFont=Acme&displayFont=Acme&colorMatch=true
----------------

![Screenshot 2025-03-20 at 11 15 15 AM](https://github.com/user-attachments/assets/a3664694-c5c5-4229-b68e-7980ec158a5b)

-----

