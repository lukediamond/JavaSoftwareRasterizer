# Luke Diamond
# 01/22/2018
# Grade 11 Final Project
# Mr. Patterson

all:
	javac src/rasterizer/*.java -d .
run:
	java -XX:+AggressiveOpts rasterizer.SoftwareRenderer
