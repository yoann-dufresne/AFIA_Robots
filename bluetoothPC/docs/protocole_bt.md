## robot => pc
 * {ID: [COMMAND;DATA0;DATA;...;DATAN]}

Id : 0-1-2-3 (0 au nord west, 1 au nord est, 2 au sud est, 3 au sud ouest (on tourne sens horaire))

### Commands :
 * Envoyer un update du robot fréquemment    => UPDATE;X;Y;DIR;WALL_NORTH;_EAST;_SOUTH;_WEST
 * Envoyer une liste de murs découverts     => DISCOVERED;X0;Y0;X1;Y1;...;XN;YN



## pc = > robot
 * COMMAND;DATA0;DATA;...;DATAN\n

### Commands :
 * Envoyer liste de murs découverts   => DISCOVERED;X0;Y0;X1;Y1 ... XN;YN
 * Envoyer information partielle   => PARTIAL;X Y (avec ? pour la valeur inconnue)
 * commander déconnection du bluetooth  => STOP
