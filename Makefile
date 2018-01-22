# Luke Diamond
# Mr. Patterson
# Grade 11 Final Project
# 01/22/2018

all:
	javac src/rasterizer/*.java -d .
run:
	java -XX:+AggressiveOpts rasterizer.SoftwareRenderer
