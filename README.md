# Seam carving project

Resize an image using the seam carving method. 

## Tasks repartition
### Pierre Bouillon
* .pgm saving function
* .ppm reading function
* .ppm saving function
* CLI
* Dijkstra implementation
* Documentation
* GUI
* Interest evaluation functions
* Seam Carving implementation for .pgm
* Seam Carving implementation for .ppm

### Yoan Fath
* Array to graph translation
* Dijkstra implementation
* Documentation
* Seam Carving implementation for .pgm
* Seam Carving graph transformation with double vertices
* Seam carving double Dijkstra 

## Usage
Helper
```shell
~$ java SeamCarvingLauncher -h
SeamCarving : Available options
   -c <img> <out.pgm> ... compress an image to a pgm file
   -h ................... displays help
   -d <begin> <end>...... delete pixel between those columns
   -k <begin> <end>...... keep pixel between those columns
   -s ................... use simple method instead of double (v2.0)
   -t ................... toggle grey values (for pgm only)
   -v ................... enable verbose mode
```
Example
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/pgm/totem.pgm totem_out.ppm -v
Warning: Simple method used by default (version < 2.0)

Progress:
	PGM values acquired
	[====================] 100%
	| Successfully removed 50 columuns in 7189 ms
	| New image saved in: PortablePixmaps/pgm/totem_out.pgm
```

## Short term goals
- [ ] GUI
- [ ] path removed trace
- [ ] handles lines and columns
- [x] handles rgb images
- [x] mark pixels as "to delete"
- [x] mark pixels as "to keep"

## Authors
* [Pierre Bouillon](https://pierrebouillon.tech/)
* [Yoan Fath](https://github.com/yoanFath)
