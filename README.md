# Lorenzo il Magnifico

## Developers
- Alberto Mario Bellini
- Lorenzo Barcella
- Federico Di Dio

## Implementation

These are the core elements that has been implemented and tested.

- Complete rules of the game
- RMI Networking
- Socket Networking
- Graphical user interface (GUI)
- Command line interface (CLI)
- Fifth player (We extended the basic game rules to account for a fifth player)
- Game persistence: stop playing and resume later. 
- Management of network failure: disconnection & reconnection
- Complete configuration of game parameters, cards etc. via JSON files

## Starting the game
In the client package there is the complete launcher that allows to start GUI / CLI. 
To start the server, navigate to the server package, contrller, game and start GameEngine.

## How does it work
After registering or logging in, you automatically enter a lobby waiting for other players.
When there are at least two players, the timeout for the beginning of the match starts.
If you log in with a username from a player who was in an unfinished game, you enter a persistent lobby: In this case no automatic timeouts start when you join. The game will resume normally after all the others players have joined back the lobby.

By default each player has up to 120s to make a move, after which he will be suspended and will have to reconnect. 
Possible disconnections by the players have been managed: in this case, doing a login will immediately resume the game. 
If the connection falls while the turn is not over, the turn is given to the next player.
If the server goes offline it is able to resume immediately from the beginning of the last player's turn.

## Additional rules crafted for the fifth player
In the draft we use all 20 leader cards and also the 'default' tile bonus of the simplified rules. The initial resources of the fifth player are in line with those of the others (9 coins, 2 stones, 2 woods, 3 servants). The game board does not change. The algorithm of throwing the dice is no longer random but guarantees a total sum of the 3 dice that is at least 14, this because the main problem is not so much not knowing where to put the family members but rather being able to put them in high positions towers. Moreover, with the fifth player the rule is no longer valid for which in a tower there can not be two family members of the same player (the additional price of 3 coins is still active in the case of tower occupied). By placing all 5 family members, the players 'occupy' 20 action places on the board. With our rules the available places in which to put family members even excluding the compound action places (council palace, harvest & production) are 22 and therefore perfect to make the game 'playable' (16 seats on the towers + 4 in the market + 2 in the individual action places).

## External libraries

We used GSON for the JSON and JFoenix parsing for a button and a spinner in the GUI. 
The rest, including CSS styling on the GUI, Styiling CLI and more, was made by hand.
