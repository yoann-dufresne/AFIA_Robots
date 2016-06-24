## robot => pc
 * {ID: [COMMAND;DATA0;DATA;...;DATAN]}

Id : 0-1-2-3 (0 au nord west, 1 au nord est, 2 au sud est, 3 au sud ouest (on tourne sens horaire))

### Commands :
 * Envoyer un update du robot fréquemment    => UPDATE;LINE;COL;DIR;WALL_NORTH;_EAST;_SOUTH;_WEST
 * Envoyer une liste de murs découverts     => DISCOVERED;LINE;COL;DIRECTION;STATE;QUALITY
 * Conflit entre la carte et le terrain lors de l'exploitation => CONFLICT
 * Envoie du prochain point d'arrivée    => NEXT_POS;LINE;COL



## pc = > robot
 * COMMAND;DATA0;DATA;...;DATAN\n

### Commands :
 * Initialiser le robot => INIT;MAIN_TYPE;GRID_WIDTH;GRID_HEIGHT;START_LINE;START_COL;START_ORIENTATION
 * Envoyer liste de murs découverts   => DISCOVERED;LINE;COL;DIRECTION;STATE;QUALITY
 * Envoyer information partielle   => PARTIAL;LINE;COL (avec ? pour la valeur inconnue)
 * commander déconnection du bluetooth  => STOP
 * Attribuer un ID au robot => SETID;ID
