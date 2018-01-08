all:
	javac src/rasterizer/*.java -d .
	java rasterizer.SoftwareRenderer
