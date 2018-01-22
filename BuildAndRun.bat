javac src/rasterizer/*.java -d .
java -XX:+AggressiveOpts rasterizer.SoftwareRenderer
