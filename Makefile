all: clean Main.nxj class/main/Main.class

Main.nxj: class/main/Main.class
	nxjlink -o $@ -classpath class/ main.Main

class/main/Main.class: src/main/Main.java
	nxjc $< -d class -sourcepath src -classpath class

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

send-bt3: all
	-nxjupload -b -r -d 00:16:53:0C:C8:0A Main.nxj
	@echo "--------------------\n"

send-bt: send-bt0 send-bt1 send-bt2
