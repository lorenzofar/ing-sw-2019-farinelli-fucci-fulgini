## Weapon description in JSON
**Conventions**:
ids in snake_case, names with Capitalized Initials.

### Weapon
+ `id: string`: unique identifier of the weapon (e.g. `vortex_cannon`).
+ `name: string`: printable name of the weapon (e.g. `Vortex Cannon`)
+ `type: <ALTERNATIVE_MODES, OPTIONAL_EFFECTS>`
  - `ALTERNATIVE_MODES`: the weapon can be used in only one between different
  modes, which will be chosen at the beginning.
  All the effects **must** be of type MODE.
  Weapons with a single usage mode are modeled with this type and they
  will only have one effect.
  - `OPTIONAL_EFFECTS`: the weapon has one BASIC effect, which will always
  be applied and then has various optional effects, whose possible execution
  order is determined by the _dependsOnEffect_ attribute (explained later).
+ `cost: <RED, YELLOW, BLUE, ANY>[]` is the reload cost for using the weapon
(defaults to empty), by convention the first cube is pre-loaded.
+ `direction: <ANY, CARDINAL>` (default ANY)
  - `ANY`: when navigating squares no specific direction is checked.
  - `CARDINAL`: the weapon will detect the cardinal direction of the
  firs target and then you'll only be able to select targets in that
  direction
+ `effects: Effect[]`: array containing the effects; the order does not matter,
but for presentation purposes it is advised to put them in the most
probable execution order (e.g. BASIC effect first)

### Effect
+ `id: string`: unique identifier of the effect inside this weapon
(e.g. `basic_effect`, `phase_glide`)
+ `name: string`: printable name of the effect (e.g. `Phase Glide`)
+ `type: <BASIC, MODE, OPTIONAL, MOVEMENT>`
+ `cost: <RED, YELLOW, BLUE, ANY>[]` is the additional cost for using this
effect (defaults to empty). Basic effects/modes must have zero additional cost.

#### Basic and Optional effects
These can only be used in OPTIONAL_EFFECTS weapons.
+ `dependsOnEffects: string[]`(defaults to empty): this list contains the ids of
other effects in this weapon that must be executed before this (e.g. because
they provide needed targets).
This means that the list is left empty for BASIC effects, while other effects
may even be executed before the basic effect if this list is empty.
+ `targets: Target[]` is a list of targets for this effect.
They will be evaluated in declaration order, so you should insert mandatory
targets first and optional targets then.

#### Movement Effect
Movement effect can only be used in OPTIONAL_EFFECTS weapons.
They don't have targets, so they have only two attributes.
+ `dependsOnEffects: string[]`: list of effects that must be executed before
this (defaults to empty). Typically it is left empty
+ `playerMoves`: maximum steps the player can move

#### Mode
A MODE must and can only be used in ALTERNATIVE_MODES weapons.
It has the same expressive power as a BASIC or OPTIONAL effect, except for
the fact that it has no _dependsOnEffect_ attribute, because only one
mode can be used during an action.

### Target
Each effect can have multiple targets. By game design, usually there is one
mandatory target, while the other ones are optional.
+ `id: string`: unique identifier for this target in this weapon;
usually player targets are referred to by their color on the card
(e.g. `"red"`).
+ `targetMode: <PLAYER, SQUARE, ROOM, RADIUS, VORTEX>` specific targeting mode.

When the user selects a PLAYER or SQUARE target, this will be saved on
the weapon, in `savedPlayers` or `savedSquares` respectively, as a <id, target>
pair which can later be referenced by other targets with its id.

+ `optional: boolean`(default to false): indicates that it is not mandatory to
use this target
+ `damage: int` and `marks: int`: number of tokens which will be applied
to all players represented by this target (default to 0).
+ `visibility: <ANY, VISIBLE, NOT_VISIBLE, IGNORE_WALLS>`(default VISIBLE)
  - `ANY`: no visibility rule is applied, usually because the field is
  then restricted by distance. Even when setting this, walls are still
  considered.
  - `VISIBLE`: only visible squares are taken into account.
  - `NOT_VISIBLE`: only not visible squares are taken into account.
  - `IGNORE_WALLS`: will not consider walls when navigating squares.

The targeting scope can be restricted with parameters, described later for
each mode, which will filter out possible targets.
If applying these filters leads to only one possible choice and the target
is mandatory, it will be automatically selected. If the target is optional
or there are multiple choices the user will be asked to choose one.
In the extreme condition that there are no choices for a mandatory target,
an error will be thrown to the user.
If a target's id has a match in the `savedPlayers` or `savedSquares`
(for the respective modes), it will be selected automatically if it is
not optional (the same behavior as single-choice).

Also note that not all parameters are applicable in all modes, and some
parameters may have slightly different meanings between modes.

#### Player mode
This allows you to shoot a single player. The following parameters apply:
+ `chooseBetweenTargets: string[]` will force your target choice among these
**players** (defaults to null: no constraint)
+ `moveTargetBefore: int` allows you
to pick a target anywhere on the board and move it for the specified amount
of steps (or less)  (defaults to 0: cannot move the target).
The square where it lands must be in the targeting scope, i.e. it must satisfy
the remaining filters.
+ `minDist: int` and `maxDist: int` (defaults to null: no constraint) specify
the minimum/maximum distance of the square the targeted player is in from the
shooter's square.
The distance is calculated considering walls (walking distance), unless
visibility IGNORE_WALLS is set (air distance).
+ `excludeSquares: string[]` will exclude players in specified squares
(id must be in `savedSquares`) from targeting choices (defaults to empty).
+ `excludePlayers: string[]` will exclude specified players (id must be in
`savedPlayers`) from targeting choices (defaults to empty).
+ `visibleFromPlayer: string` specifies that the target must be visible from
specified player (id must be in `savedPlayers`) (defaults to null: no constr.)
+ `moveShooterHere: boolean` will force the shooter to move to target's square
before shooting (the square in which it is damaged, pre/after movements of the
target are not taken into account) (defaults to false).
+ `squareRef: string` forces the system to save the square the target
is in (same rules as above) in `savedSquares` for later reference.
+ `moveTargetAfter: int` allows the player to move its target of specified
amount after shooting it. _Important note: the movement must be only in one
direction_ (defaults to 0: cannot move the target)

#### Square mode
This allows you to target all players in a single square. The filters are
similar to player.
+ `chooseBetweenTargets: string[]` will force your target choice among these
**squares** (defaults to null: no constraint)
+ `minDist: int` and `maxDist: int`: same as in player mode
+ `excludeSquares: string[]` excludes specified squares from being targeted
(defaults to empty).
+ `excludePlayers: string[]` excludes specified players from being targeted,
even if they are on the selected square (defaults to empty).
+ `visibleFromPlayer: string` works as in player mode
+ `moveShooterHere: boolean` works as in player mode
+ `squareRef: string` works as in player mode

Notice that the following are **NOT APPLICABLE**:
`moveTargetBefore`, `moveTargetAfter`

#### Room mode
Allows you to target all players in a single room (all of its squares).
Rooms cannot be saved/referenced so the choice of filters is quite limited:
+ `minDist: int` and `maxDist: int`: distance from the nearest square of the
room.
+ `excludeSquares: string[]` excludes specified squares from being targeted
(defaults to empty).
+ `excludePlayers: string[]` excludes specified players from being targeted,
even if they are in the selected room (defaults to empty).

#### Radius mode
Targets all players within a certain radius from shooter's square (no user
interaction).
+ `minDist: int` and `maxDist: int`: minimum and maximum distance of squares
that will be selected.
+ `excludeSquares: string[]` excludes specified squares from being targeted
(defaults to empty).
+ `excludePlayers: string[]` excludes specified players from being targeted,
even if they are in the radius (defaults to empty).

#### Vortex mode
First the player is asked to select a square whose requirements
are: meeting the _visibility_ requirement and having at least one
player within the distance specified by _maxDist_.
This square is automatically saved with a special tag ("vortex").
Then the shooter chooses a player within _maxDist_ from the vortex,
this player will be moved to the vortex and receives damage.
Subsequent targets in vortex mode will automatically detect
the vortex square.
Parameters that apply:
+ `visibility` of the vortex from the shooter.
+ `maxDist` of the targeted player from the vortex.
+ `excludePlayers: string[]` excludes specified players from being selected.
