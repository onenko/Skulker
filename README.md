# Skulker

Command line utility to encrypt your sensitive files, and skulk these files into regular files.

### Example 1

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



