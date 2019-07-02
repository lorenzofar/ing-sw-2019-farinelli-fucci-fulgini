# Adrenaline (Prova finale Ingegneria del Software)
This is a digital adaptation of [Adrenaline](https://czechgames.com/en/adrenaline/), a board game developed by Czech Game Creations and distributed in Italy by Cranio Creations.
The game has been developed in Java as a Software Engineering project at [Politecnico di Milano](https://www.polimi.it).

## Team
<!-- TODO: Remove sensible data before publishing public repo -->
| Name                | Matricola      |
| :------------------ | :------------- |
| Lorenzo Farinelli   | 866236         |
| Tiziano Fucci       | 873622         |
| Alessandro Fulgini  | 866390         |

## Features
- [x] Complete rules
- [x] GUI
- [x] CLI
- [x] TCP connection
- [x] RMI connection
- [x] Multiple simultaneous matches (on the server)
- [ ] Game persistence
- [ ] Terminator
- [ ] Domination mode
- [ ] Turret mode

## System requirements
This software requires Oracle's Java Runtime Envirionment (including JavFX)
to be installed and properly configured on the system.

## Server
The server can handle multiple matches at a time and both RMI and Socket
clients.\
It is recommended to start the server with the command
```
java -Dadrenaline.server.hostname=[hostname] -jar adrenaline-client.jar
```
where *hostname* is an address resolvable from the clients who want to connect.
This has the advantages of seeing logs on the console.

The server can also be started by double-clicking the JAR, but in this case
you'll have to set the parameter with a properties file (see more on the
dedicated section).

When the server has started, it will wait for login requests by the players.
Once a login request has been accepted, the player will be waiting in the
lobby until there are enough waiting players to start the match.

When the match starts, each player has a maximum amount of time to end his turn.
If the time runs out, he will be suspended and disconnected, but he can
reopen his client, login with the same name and will rejoin the match at the
end of the current turn.
For this reason there cannot be two players with the same name at the same time
in the whole server.

A match may end either because all the players complete their turns
or because at the end of a turn, the number of active players is below
the minimum.
In both cases the scoring is performed and the scores are shown to the
active players.

## Client
To run the client run the following command in a console window:
```
java -jar adrenaline-client.jar
```
and make sure to add any needed parameters such as `adrenaline.client.hostname`
if you want to connect with RMI (the hostname must be resolvable by the server).

You can select the interface with a parameter afer the JAR name:
`cli` or `gui`. If no parameter is selected, then GUI is chosen by default.
The client can also be started in GUI mode by double-clicking the JAR.

#### Connection and login
At first you will be asked to insert the server's address, the connection
mode and your username.
Then you will be waiting in a lobby until there are enough players to start
a match.

#### Match screen
<!-- TODO: Add screenshots -->
Once you have completed the login procedure and the match has stared,
the _match screen_ will be shown to you. This includes: the game board,
the weapons in the spawn points, your player board, your items and other
player's boards and weapons.
The match screen will be automatically updated when something changes,
both during your or another player's turn.

#### Selections

During the game you will be asked to make selections.
+ In GUI this is very simple: just click the item you want to select.
+ In CLI each choice has a number: to reply type the number and press
Enter on your keyboard.

## Configuration parameters

The game has various parameters which can be modified by the user.
The parameters can be loaded from a custom file by specifying the following
JVM option `-Dadrenaline.config=path/to/file.properties` where the file
is the property file described later.
If such option is not provided, the game will try to load properties
from a default file `adrenaline.properties` in the directory where the
game has been launched.
If this file does not exist, then the default values are used.
The parameters can also be overridden by specifying the corresponding
JVM arguments (`-Dparameter.name`) when running the game from command line.

It's important to note that there is no dirty checking for the configured
parameters, to it's important to check the their correctness before
starting the game.

#### Definitions
These are the parameters:
+ `adrenaline.server.hostname`:
an hostname or IP address for the server, must be resolvable for the clients who
want to connect (used by RMI)
+ `adrenaline.client.hostname`:
an hostname or IP address for the client, must be resolvable for the server to
which the client connects (used by RMI)
+ `adrenaline.rmi.port`:
port of the RMI registry (default 1099)
+ `adrenaline.socket.port`:
port of the server socket (default 3000)
+ `adrenaline.players.min`:
the minumum number of players for a match (default 3).
This must also be less than the maximum number of players, determined by
the colors, which is currently 5.
If there are less waiting players, a match won't start. If at the end of a turn
the number of active players is less than this, the match ends.
+ `adrenaline.rmi.ping.interval`:
the interval, in seconds, between the ping commands sent from RMI clients to the
server (default 10 s). Note that the server does not ping the clients.
+ `adrenaline.timeout.turn`:
the maximum duration of a player's turn (default 30 s).
If the player does not complete the turn in this time, he will be suspended.
+ `adrenaline.timeout.lobby`:
the timeout after which the lobby starts the match if there are at least
the minimum amount of players connected (default 30 s)
+ `adrenaline.timeout.config`:
the amount of time available for the first player to select the initial match
configuration: board and number of skulls (default 30 s)
If the time runs out, these are selected automatically.
+ `adrenaline.timeout.spawn`:
the amount of time available for players to respawn at the end of the turn
(default 30 s). If they don't respawn, a respawn location is chosen
from the first drawn powerup.
+ `adrenaline.timeout.revenge`:
the amount of time available, after a weapon has been used, for each player
who has a revenge powerup (Tagback grenade) to select whether to use it or
not (default 30 s)

#### Examples
Example run with JVM arguments:
```
java -Dadrenaline.server.hostname=192.168.1.1 -jar adrenaline-server.jar
```

Example configuration file (with default values):
```
adrenaline.rmi.port = 1099
adrenaline.socket.port = 3000
adrenaline.server.hostname = 192.168.1.1
adrenaline.client.hostname = 192.168.1.2
adrenaline.players.min = 3
adrenaline.rmi.ping.interval = 10
adrenaline.timeout.turn = 180
adrenaline.timeout.lobby = 30
adrenaline.timeout.config = 30
adrenaline.timeout.spawn = 30
adrenaline.timeout.revenge = 30
```

#### Logging
Both client and server use the default Java logging library.
A custom logging configuration file can be provided by setting the JVM
parameter `-Dadrenaline.loggerconfig` which must be a path to a valid
logging properties file.
By default the server will log in console with level INFO, while
the client will log to file (`client.log`) with level FINE.

## Copyright
Adrenaline is a trademark of Czech Game Editions and Cranio Creations.
