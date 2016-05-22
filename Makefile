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

send-bt0: all
	-nxjupload -b -r -d 00:16:53:13:EF:A9 Main.nxj
	@echo "--------------------\n"

send-bt1: all
	-nxjupload -b -r -d 00:16:53:0E:F7:5F Main.nxj
	@echo "--------------------\n"

send-bt2: all
	-nxjupload -b -r -d 00:16:53:0F:F5:A9 Main.nxj
	@echo "--------------------\n"

send-bt: send-bt0 send-bt1 send-bt2
