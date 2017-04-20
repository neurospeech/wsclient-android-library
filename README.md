# WSClient++ Android Java Library

- Remove original Jar file that came with WSClient++ installer
- Setup Project level gradle as shown below
- Setup Module level gradle as shown below

## Project level gradle

     allprojects {
	  	repositories {
			  maven { url "https://jitpack.io" }
  		}
	  }



## app Module gradle
    dependencies {
		   compile 'com.github.neurospeech:wsclient-android-library:v1.0.1'
	  }


