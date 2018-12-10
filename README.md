# Fog Of War

This Project is an online chess-like game called **Fog of War**.

There are two clients, a server and game client. The game client communicate with the server for matchmaking, game instanciation, account creation and verification and game to game communication.

The game consists of two players. Each player is given a number of walls, gunners, and one commander. The walls have a high HP and block Line of Site from the enemy. The gunners deal damage but have little HP. The commander deals high damage and has twice the HP of a gunner. If the commander dies, the game is over. Moving the pawns will remove the fog surrounding the pawns on the map and once the fog has been cleared it will not return. Each player will be given a set number of moves (yet to be play-tested).

The project is written in Java 8 with the use of the graphics [LibGdx](https://github.com/libgdx/libgdx) library.

The project still in alhpa and has many undeveloped features.

All of the pixel art, excluding the login background and gunner sprite, was draw myself "by hand" or mouse I suppose. :P

**Features to develop:**

Game Client:
 - Implment the in-game menu UI include the following features:
  - Move Counter
  - Pawns placement bar (during initialization of game)
  - status label
  - Game over screen
  - "Arcade Cabinet" style surrounding background
 - Pause menu:
  - Leave game button
  - Options:
   - Resize
   - sound control
   - Full screen
 - Repair calcClears()
 - Repair location list
 - Complete login sequence
 - Encrypt local xml game/login data
 
Server Client:
 - Implement matchmaking
 - Rework server/game connection sequence
 - Implement an account verification function
 - Implement a game class:
  - Insanciates game thread
  - Moderates game to game communciation
  - notifies games of leavers and disconnections then terminates game thread
