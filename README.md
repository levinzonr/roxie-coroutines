Fork of https://github.com/ww-tech/roxie

# Installation

## Using AAR artifact
Download latest ARR artifact [here](https://github.com/levinzonr/roxie/packages/62611?version=1.0.1)

## Using Maven repository

### Add github maven repository to your project level build.gradle
```groovy
 maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/levinzonr/roxie")
            credentials {
                username = '<github username>'
                password = '<Your Github access token>'
            }

        }
```

### Add dependencie to your module level build.gradle
```groovy
  implementation 'cz.levinzonr:roxie:1.0.1'
 ```
