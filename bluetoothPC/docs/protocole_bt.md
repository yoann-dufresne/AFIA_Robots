## robot => pc
 * ID;COMMAND;DATA0;DATA;...;DATAN\n

Id : 0-1-2-3 (0 au nord west, 1 au nord est, 2 au sud est, 3 au sud ouest (on tourne sens horaire))


### Commands :

#### Send :
 * Envoyer changement de case    => MOVE_TO;X Y
 * Envoyer une liste de murs découverts     => DISCOVERED;X0 Y0;X1 Y1;...;XN YN

#### Reception :
 * recevoir liste de murs découverts   => DISCOVERED;X0 Y0;X1 Y1;...;XN YN
 * recevoir information partielle    => PARTIAL;X Y (avec ? pour la valeur inconnue)
 * recevoir déco bluetooth  => STOP






## pc = > robot
 * COMMAND;DATA0;DATA;...;DATAN\n

### Commands :
#### Send :
 * Envoyer liste de murs découverts   => DISCOVERED;X0 Y0 X1 Y1 ... XN YN
 * Envoyer information partielle   => PARTIAL;X Y (avec ? pour la valeur inconnue)
 * commander déconnection du bluetooth  => STOP

#### Reception :
 * changement de case
 * recevoir une liste de murs découverts