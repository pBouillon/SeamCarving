# Seam carving project

Image resizing with the Seam Carving algorithm.

## Usage
Helper
```shell
~$ java SeamCarvingLauncher -h
SeamCarving : Available options
   -c <img> <out> .... compress an image to a pgm file
   -g ................ get greyscale for ppm and save as pgm
   -h ................ displays help
   -d <begin> <end> .. delete pixel between those columns
   -k <begin> <end> .. keep pixel between those columns
   -l ................ use long method instead of simple
   -lines ............ crop lines instead of columns
   -t ................ toggle values (color inversion)
   -v ................ enable verbose mode
```
Example (with a lots of arguments)
```shell
~$ java SeamCarvingLauncher -c PortablePixmaps/ppm/cake.ppm type_swap_output.pgm -v -g -t -lines
Progress : 
	[====================] 100%
	| Values correctly inverted
	|
	| Lines used
	|
	| Saved as ppm instead of pgm
	|
	| Successfully removed 50 columuns in 10197 ms
	| New image saved in:
	|	type_swap_output.pgm
```

## Tasks repartition
### Pierre Bouillon
* .pgm saving function
* .ppm reading function
* .ppm saving function
* Arg parser
* Conversion from .ppm to .pgm
* Dijkstra implementation
* Documentation
* Forward energy implementation
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
