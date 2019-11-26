Fork of https://github.com/ww-tech/roxie

# Installation

### Add github maven repository to your project level build.gradle
```groovy
 maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/levinzonr/roxie")
            credentials {
                username = 'levinzor'
                password = '1a5f3f2042fe4ece475dccbe3d1cf9ea1973c1ed'
            }

        }
```

### Add dependencie to your module level build.gradle
```groovy
  implementation 'cz.levinzonr:roxie:1.0.0-test'
 ```
