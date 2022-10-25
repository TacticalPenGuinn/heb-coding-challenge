# HEB Coding Challenge
## Submitted by Zachary Guinn

### Getting the app
```
$git clone https://github.com/TacticalPenGuinn/heb-coding-challenge.git
```

### Pre-reqs
- java jdk 17

### Running the app
```
$ ./gradlew clean build
$ ./gradlew bootrun
```

### Accessing the GUI
```
http://localhost:8080
```

### Accessing the H2 database GUI
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./db/testdb
Username: sa
Password: password   //(lol I know...it's supper secure)
```

### End Points

#### Get all images
```
GET /images
```

#### Get images by object
```
GET /images?objects="dog,cat"
```

#### Get image by id
```
GET /images?objects/{imafeId}
```

#### Upload Image
```
POST /images

form-data Payload: 
 - multipartFile: {file to upload}
 - imageURL: {http image url to upload}
 - imageJson: {"label":"<optional image lable>","enableObjectDetection":<true|false>}
```
