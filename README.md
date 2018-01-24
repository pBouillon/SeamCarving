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
* Interest evaluation functions
* Seam Carving implementation for .pgm
* Seam Carving implementation for .ppm

### Yoan Fath
* Array to graph translation
* Dijkstra implementation
* Documentation
* Seam Carving implementation for .pgm

## Usage
```shell
~$ java SeamCarvingLauncher -h
SeamCarving : Available options
   -c <img> <out.pgm> ... compress an image to a pgm file
   -h ................... displays help
   -d <begin> <end>...... delete pixel between those columns
   -k <begin> <end>...... keep pixel between those columns
   -s ................... use simple method instead of double (v2.0)
   -v ................... enable verbose mode

~$ java SeamCarvingLauncher -c path/to/image.pgm output.pgm -v
Warning: Simple method used by default (version < 2.0)

PGM values acquired
Beginning of the resize
Progression:
	0% ....... 25% ...... 50% ..... 75% ..... Done !
Successfully saved in output.pgm
```

## Short term goals
- [ ] handles lines and columns
- [ ] handles rgb images
- [x] mark pixels as "to delete"
- [x] mark pixels as "to keep"

## Authors
* [Pierre Bouillon](https://pierrebouillon.tech/)
* [Yoan Fath](https://github.com/yoanFath)
