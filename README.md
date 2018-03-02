# Seam carving project

Image resizing with the Seam Carving algorithm.

## Usage
Helper
```shell
~$ java SeamCarvingLauncher -h
seamcarving.SeamCarving : Available options
   -c <img> <out.pgm> ... compress an image to a pgm file
   -h ................... displays help
   -d <begin> <end> ..... delete pixel between those columns
   -k <begin> <end> ..... keep pixel between those columns
   -l ................... use long method instead of double (v2.0)
   -lines ............... alter lines instead of columns
   -t ................... toggle grey values
   -v ................... enable verbose mode
```
Example
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/pgm/totem.pgm totem_out.ppm -v
Warning: Simple method used by default (version < 2.0)

Progress : 
	[====================] 100%
	| Successfully removed 50 columuns in 6400 ms
	| New image saved in:
	|	totem_out.ppm
```

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
* Lines handling
* Seam Carving implementation for .pgm
* Seam Carving implementation for .ppm
* Toggled values for .pgm and .ppm

### Yoan Fath
* Array to graph translation
* Dijkstra implementation
* Documentation
* Seam Carving implementation for .pgm
* Seam Carving graph transformation with double vertices
* Seam carving double Dijkstra
* Seam carving double graph generation

## Short term goals
- [ ] GUI
- [x] path removed trace
- [x] handles lines and columns
- [x] handles rgb images
- [x] mark pixels as "to delete"
- [x] mark pixels as "to keep"

## Authors
* [Pierre Bouillon](https://pierrebouillon.tech/)
* [Yoan Fath](https://github.com/yoanFath)
