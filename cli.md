# Command line interface (Command line format)

## Definitions

```source-file``` - the file, one want to hide (to skulk)  
```carrier-file``` - the file, used as a hidden place for ```source-file```  
```skulked-file``` - the ```carrier-file``` together with hidden inside ```source-file```

## Command line format
```
skulker <command> <options> <file-on-input> [<file-on-output>]
```
### Commands

#### Commands that operate on single file (one carrier and one source)
```s``` - **s**kulk ```source-file``` into ```carrier-file```  
```d``` - **d**eskulk ```source-file``` from ```carrier-file```  
```i``` - display **i**nformation about ```skulked-file```

#### Commands that operate on directories of files

```r``` - **r**ecursively skulk files from ```source-dir``` into files in ```carrier-dir```  
```x``` - **x**tract skulked files in ```skulked-dir``` to ```source-dir```  
```b``` - **b**rowse ```skulked-dir``` and display information about all files

#### Command line format for every command
```
skulker s <options> <source-file> <carrier-file>
skulker d <options> <carrier-file>
skulker i <options> <skulked-file>
skulker r <options> <source-dir> <carrier-dir>
skulker x <options> <carrier-dir> <source-dir>
skulker b <options> <skulked-dir>
```

### Options

| Option | Cmd | Description |
| --- | --- | --- |
| -o <output-file> | s | new ```skulked-file``` is written, ```carrier-file``` is unmodified |
| -s <source-file-name> | d | hidden ```source-file``` is extracted to ```source-file-name``` |
| -c | d | after successfull deskulk of hidden ```source-file```, original ```carrier-file``` is reconstructed and replaces ```skulked-file``` 
| -p <password> | all | password used for en(de)cryption |
| -a | sdrx | reserved for future - save and restore file attributes |
| -v | b | verbosely display all browsed files even if they do not contain skulked files |
| -d <drv-letter> | dx | if files were skulked with absolute file path, they are restored not for current drive, but for given drive |

## Examples











