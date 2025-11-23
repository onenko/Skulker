# Skulker

Command line utility to encrypt your sensitive files, and skulk these files into regular files.

### Examples of command line (outdated)

You have secret agreement file '\Users\nenko\Documents\Secret_Agreement.docx', which you want to hide.
You select some carrier file, like '\Users\nenko\Downloads\IMG_3456.JPG' with photo of your cat, and run

```
java -jar Skulker a \Users\nenko\Documents\Secret_Agreement.docx \Users\nenko\Downloads\IMG_3456.JPG
```

This command encrypt the document, and attach the encrypted stream to the end of the JPG file.
The file IMG_3456.JPG will look like broken image file.

```
java -jar Skulker a <directory-with-files-to-hide> <directory-with-carrier-files>
```
Above command recursively create the list of sensitive files, and skulk every such file into a carrier file.
Definitely, the number of carrier files should be greater or equal to the number of files being skulked.

```
java -jar Skulker x \Users\nenko\Documents\Secret_Agreement.docx \Users\nenko\Downloads\IMG_3456.JPG
java -jar Skulker x <directory-with-files-to-hide> <directory-with-carrier-files>
```
Reverse operations, with 1 file and with directory tree of files.

## Command line format

[Moved to separate file](cli.md)

### Abbreviation

- CL - carrier file length in bytes
- VSN - variable size number - data type used to store the lengths in the minimum number of bytes, LSB last
- RVSN - reverse variable size number - like VSN, but bytes LSB is first
- SPN - skulked path name
- SFL - skulked file length


### File layout


Bytes from - to - explanation

- 0 - (CL-1) - carrier file itself, which has the length of N bytes
- CL - (CL+VSNL1-1) - VSN - length of skulked path name, SPNL
- CL+VSNL1 - (CL+VSNL1+SPNL-1) - skulked path name
- CL+VSNL1+SPNL - (CL+VSNL1+SPNL+VSNL2-1) - VSN - length of extension, EXTL
- CL+VSNL1+SPNL+VSNL2 - (SFL-VSNL3-1) - skulked content
- (SFL-VSNL3-1) - (SFL-1) - carrier file original length

### VSN and RVSN data types

https://exercism.org/tracks/java/exercises/variable-length-quantity
https://en.wikipedia.org/wiki/Variable-length_quantity

Value Storage
- 0 - 127 1 byte contain this value, contains MSB zero
- 128 - 128*127 - total 2 bytes, 1st is least 7 bits, and 2nd byte has 0 MSB and the value, multiplied by 128
- ...












