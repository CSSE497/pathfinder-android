[![Coverage Status](https://coveralls.io/repos/CSSE497/pathfinder-android/badge.svg?branch=dev&service=github)](https://coveralls.io/github/CSSE497/pathfinder-android?branch=dev)
[![Build Status](https://travis-ci.org/CSSE497/pathfinder-android.svg?branch=dev)](https://travis-ci.org/CSSE497/pathfinder-android)
[![Stories in Ready](https://badge.waffle.io/CSSE497/pathfinder-android.png?label=ready&title=Ready)](http://waffle.io/CSSE497/pathfinder-android)
[![Stories in Ready](https://badge.waffle.io/CSSE497/pathfinder-android.png?label=In%20Progress&title=In%20Progress)](http://waffle.io/CSSE497/pathfinder-android)
[![Stories in Ready](https://badge.waffle.io/CSSE497/pathfinder-android.png?label=Under%20Review&title=Under%20Review)](http://waffle.io/CSSE497/pathfinder-android)

# Pathfinder Android/Java Client Library

The Pathfinder Android/Java Client Library allows developers to easily integrate Pathfinder routing service in their Android and Java applications.

Pathfinder provides routing as a service, removing the need for developers to implement their own routing logistics. This SDK allows for Android and Java applications to act as commodities that need transportation or transportation providers. Additionally, there is support for viewing routes for sets of commodities and transportation providers.

To register your application to use Pathfinder, visit our [website](https://thepathfinder.xyz).

To get up to speed on integrating Pathfinder in your Android or Java application, checkout the [Javadocs](https://www.javadoc.io/doc/xyz.thepathfinder/pathfinder-android) or the [tutorials](https://pathfinder.readme.io).

Pathfinder is distributed through Maven Central and JCenter. To use Pathfinder in your application, add the following line to your build.gradle file if you are using Gradle:

    compile 'xyz.thepathfinder:pathfinder-android:0.0.16'
    compile 'com.google.code.gson:gson:2.5'

Note, if you are using Oracle's JDK/JRE you must add Let's Encrypt's X1 and X3 certificates to your keystore to properly connect to the Pathfinder service.

If you want to see the logging messages add the following dependencies:

    compile 'org.slf4j:slf4j-log4j12:1.7.21'
    compile 'ch.qos.logback:logback-classic:1.1.3'
    compile 'ch.qos.logback:logback-core:1.1.3'
