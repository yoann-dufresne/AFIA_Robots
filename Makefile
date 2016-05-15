all: clean Main.nxj class/main/MainPC.class

Main.nxj: class/main/Main.class
	nxjlink -o $@ -classpath class/ main.Main

class/main/Main.class: src/main/Main.java
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
	nxjupload -r -u Main.nxj

send-bt: all
	nxjupload -b -r Main.nxj

