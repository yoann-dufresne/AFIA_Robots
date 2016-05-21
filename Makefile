all: clean Main.nxj class/main/Main.class

Main.nxj: class/main/Main.class
	nxjlink -o $@ -classpath class/ main.Main

MainExploration.nxj: class/main/MainExploration.class
	nxjlink -o $@ -classpath class/ main.MainExploration

class/main/Main.class: src/main/Main.java
	nxjc $< -d class -sourcepath src -classpath class

class/main/MainExploration.class: src/main/MainExploration.java
	nxjc $< -d class -sourcepath src -classpath class

class/main/MainPC.class: src/main/MainPC.java
	nxjpcc -d class $<

runPC: class/main/MainPC.class
	nxjpc -cp ./class  main.MainPC



clean:
	rm -rf class/
	mkdir class
	mkdir class/main

send: all
	nxjupload -r -u MainExplorer.nxj

send-bt: all
	nxjupload -b -r Main.nxj

