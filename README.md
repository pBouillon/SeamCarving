# Seam carving project

Image resizing with the Seam Carving algorithm.

## Usage
To show program's usage, use `-h`:
```shell
~$ java SeamCarvingLauncher -h
SeamCarving : Available options
   -c <img> <out> .... compress an image to a pgm file
   -g ................ get greyscale for ppm and save as pgm
   -h ................ displays help
   -d <begin> <end> .. delete pixel between those columns
   -i ................ increase lines/rows instead of cropping
   -k <begin> <end> .. keep pixel between those columns
   -l ................ use long method instead of simple
   -lines ............ crop lines instead of columns
   -t ................ toggle values (color inversion)
   -v ................ enable verbose mode
```
-----------
You can crop an image, either [PPM or PGM](https://fr.wikipedia.org/wiki/Portable_pixmap), 
use `-c source target` and add `-v` if you want to watch the progress.
* Cropping a PGM:
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/pgm/ex1.ppm resized.pgm -v
Progress : 
	[====================] 100% (50 row(s) handled)
	| Successfully removed 50 columuns in 9898 ms
	| New image saved in:
	|	resized.pgm
```
* Cropping a PPM:
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/ppm/cake.ppm resized.ppm -v
Progress : 
	[====================] 100% (50 row(s) handled)
	| Successfully removed 50 columuns in 24724 ms
	| New image saved in:
	|	resized.ppm
```
* Cropping a PPM with a lots of options:
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/ppm/cake.ppm type_swap_output.pgm -v -g -t -lines -i
Progress : 
	[====================] 100%
	| Increasing size
	|
	| Lines used
	|
	| Values correctly inverted
	|
	| Saved as ppm instead of pgm
	|
	| Successfully removed 50 columuns in 19074 ms
	| New image saved in:
	|	type_swap_output.pgm
```
* Cropping a PGM with the double Dijkstra's method:
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/pgm/totem.pgm totem_out_double.pgm -l -v
/!\ WARNING: double Dijkstra take a huge amount of time

Progress (Using double Dijkstra): 
	[====================] 100% (50 row(s) handled)
	| Using Double Dijkstra
	|
	| Successfully removed 50 columuns in 1107473 ms
	| New image saved in:
	|	totem_out_double.pgm
```
## Tasks repartition
### Pierre Bouillon
* .pgm saving function
* .ppm reading function
* .ppm saving function
* Arg parser
* Conversion from .ppm to .pgm
* Dijkstra implementation
* Double Dijkstra implementation
* Double Dijkstra optimization
* Documentation
* Forward energy implementation
* Interest evaluation functions
* Lines handling
* README.md redaction
* Seam Carving implementation for .pgm
* Seam Carving implementation for .ppm
* Size augmentation
* Toggled values for .pgm and .ppm

### Yoan Fath
* Array to graph translation
* Dijkstra implementation
* Double Dijkstra implementation
* Documentation
* Seam Carving implementation for .pgm
* Seam Carving graph transformation with double vertices
* Seam carving double graph generation

## Short term goals
- [x] path removed trace
- [x] handles lines and columns
- [x] handles rgb images
- [x] mark pixels as "to delete"
- [x] mark pixels as "to keep"

## Authors
* [Pierre Bouillon](https://pierrebouillon.tech/)
* [Yoan Fath](https://github.com/yoanFath)
