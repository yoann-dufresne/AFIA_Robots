
all: clean Main.nxj

Main.nxj: class/main/Main.class
	nxjlink -o $@ -classpath class/ main.Main

class/main/Main.class: src/main/Main.java
	nxjc $< -d class -sourcepath src -classpath class

clean:
	rm -r class/
	mkdir class
	mkdir class/main

send: all
	nxjupload -r -u Main.nxj

