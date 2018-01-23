# Seam carving project

Resize an image using the seam carving method. 

## Tasks repartition
### Pierre Bouillon
* .pgm saving function
* CLI
* Dijkstra implementation
* Documentation
* Interest evaluation functions
* Seam Carving final implementation
* PPM reading function
* PPM saving function
* Seam Carving implementation for PPM

### Yoan Fath
* Array to graph translation
* Dijkstra implementation
* Documentation
* Seam Carving final implementation

## Usage
```shell
SeamCarving : 
   -c <img> <out.pgm> ... compress an image to a pgm file
   -h ................... displays help
   -s ................... use simple method instead of double (v2.0)
   -v ................... enable verbose mode
```

## Short term goals
- [ ] handles lines and columns
- [ ] handles rgb images
- [x] mark pixels as "to delete"
- [x] mark pixels as "to keep"

## Authors
* [Pierre Bouillon](https://pierrebouillon.tech/)
* [Yoan Fath](https://github.com/yoanFath)
