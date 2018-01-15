all:
	javac src/rasterizer/*.java -d .
run:
	java -XX:+AggressiveOpts rasterizer.SoftwareRenderer
