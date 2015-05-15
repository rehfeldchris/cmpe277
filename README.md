CMPE 277 Project
---------------------------------------

Photo Sharing App

Chris Rehfeld	008520603	rehfeldchris@gmail.com
Carita Ou	006004479	carita.ou@gmail.com
Thong Nguyen	008122270	nhthong2007@gmail.com 
Sai Karra		006671782	saikarra@gmail.com


Build Instructions:

Note that there's 2 sub-projects in our git repo:
1. android - our android app project/client
2. server - our api server that the app interacts with (Prodution url: https://srkarra.com:444/



Usage:

Log in with facebook. You will reach a screen titled "PhotoAlbums". You may or may not see some
 photoalbums at this point, depending on whether or not there's any publicly viewable albums in 
 the database at the moment. Create an album by clicking the icon in the actionbar. Once you create an album, you can click 
 on it and upload images into it using the "up" icon in the action bar. You can add images via the filesystem, or from your camera.
 We try to extract gps coordinates from your images, or by looking at your current location. This is done to support our search feature.
 
 You can click on an image to see it larger, as well as see or create comments on it. When viewing an image, you can swipe 
 left/right to go to other images in the current album. 
 
 You can delete images and albums when viewing them in a grid, by long pressing on them, as long as you're the original creator of it.
 
Use the menu in the actionbar to go to the search screen. The search works like a filter, so if you enter keywords, it will return a list of images
which have at least one of the keywords somewhere in its data, or one of its comments. Additionally, you can filter for images in proximity to
a location. You can click images in the search result.



Building the server (requires maven):
1. Create the packaged war file
2. Create a config file with the identified properties below
3. Run the war file in the same directory as the properties file
```
mvn package
```

```properties
server.port=444
server.ssl.key-store=
server.ssl.key-store-password=
server.ssl.key-password=

spring.data.mongodb.host=
spring.data.mongodb.port=
spring.data.mongodb.database=
spring.data.mongodb.username=
spring.data.mongodb.password=
spring.data.mongodb.repositories.enabled=true

multipart.maxFileSize=100Mb
imageFileSaveDir=~/photoapp/uploaded-images
tempDir=~/photoapp/tempdir

server.tomcat.accessLogEnabled=true
server.tomcat.accessLogPattern=%h %l %u %t "%r" %s %b %D "%{User-Agent}i"
server.tomcat.basedir=~/photoapp/tomcat/
```
```
java -jar photoapp.1.0.0.war
```