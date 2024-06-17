
### Background
The game is inspired by the classic "Daleks" game. The objective is to destroy Daleks by making them collide with each other or using bombs. Daleks move towards the player every turn, and the player can move, teleport, or use bombs to avoid and destroy Daleks.

### Requirements

#### Must Have:
- **Grid-Based Gameplay:**
  - Display a grid representing the game board.
  - Track player position, enemy tokens, and collision squares.
  - Control module below the grid for player actions.

- **Player and Enemy Mechanics:**
  - Player can move up, down, left, or right.
  - Enemies move one square closer to the player after each move.
  - Collisions between enemies turn them into collision squares.

- **Level and Score Management:**
  - Track player's score.
  - Levels increase by one enemy each time all enemies are destroyed.
  - Reset board to the initial state of the level after player collides with an enemy.

- **Health and Special Actions:**
  - Player has 3 lives.
  - Teleport button (3 uses) relocates player to a random square.
  - Bomb button (3 uses) turns enemies in a 2-square radius into collision squares.

#### Should Have:
- Enemies should not spawn within 2 tiles of the player.
- Visual indication of lives remaining.

#### Could Have:
- Animations for player and enemy movements.
- Sound effects for movements and actions.

#### Won't Have:
- Multiplayer functionality.

### Scoring
- Start with 3 enemies at level 1.
- Score:
  - 1 point for every Dalek that runs into a pile or shielded player.
  - 1 point for every Dalek that is bombed.
  - 3 points for two Daleks colliding and creating a pile.
  - 5 points for three Daleks colliding and creating a pile.
  - Additional points for completing each level.

### Additional Features
- Button for starting a new game.
- Mechanism for keeping track of scores via a Room database.

----------------

Architectural Overview
Architecture Components:
Model Layer:

Data classes for Player, Enemy, Position, and GameState.
Repository for game logic and data management.
ViewModel Layer:

ViewModel to manage UI-related data in a lifecycle-conscious way.
View Layer:

Composable functions to render the game grid and control module using Jetpack Compose.
Dependency Injection:

Hilt for providing dependencies.


-----

https://furfighters.fandom.com/wiki/Bear_Attack
https://www.isaacsukin.com/sites/daleks/index.html
