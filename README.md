# FoW

This Project is intened to be an online chess-like game called **Fog of War**.

There are two clients, a server and game client. The game client communicate with the server for matchmaking, game instanciation, account creation and verification and game to game communication.

The project is written in Java 8 with the use of the graphics[LibGdx](https://github.com/libgdx/libgdx) library.

The project still in alhpa and has many undeveloped features.

All of the pixel art, excluding the login background, was draw myself "by hand" or mouse I suppose. :P

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
 - Fix calcClears() to work properly
 - Complete login sequence
 - encrypt local xml game/login data
 
Server Client:
 - Implement matchmaking
 - Rework server/game connection sequence
 - Implement an account verification function
 - Implement a game class:
  - Insanciates game thread
  - Moderates game to game communciation
  - notifies games of leavers and disconnections then terminates game thread
