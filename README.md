# Seam carving project

Authors :
* Pierre Bouillon 
* Yoan Fath


## TASKS REPARTITION

### Pierre Bouillon
* .pgm saving function
* interest evaluation function
* entrypoint and CLI

### Yoan Fath
* array to graph translation


## SOFTWARE USAGE
```shell
usage: SeamCarvingLauncher
 -c,--compress <source> <dest>   Compress image source into dest
 -h,--help                       Displays help
 -s,--simple                     Uses the 'simple' method instead of the
                                 double
 -v,--verbose                    Shows program's progression
 
$~ SeamCarvingLauncher -c <source> <dest> [-v] [-c]
```

## IMPROVEMENTS BROUGHT
- [ ] handles rvb images
- [ ] mark pixels as "to delete"
- [ ] mark pixels as "to keep"

