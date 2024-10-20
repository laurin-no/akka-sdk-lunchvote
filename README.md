# Lunchvote Akka SDK Edition

## Run Locally

```bash
mvn compile exec:java
```

If you don't have maven installed, you can run a Docker image that contains maven and Java 21:
```
#Windows
docker run -it --rm --name test-project -v %cd%:/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java

#Linux
docker run -it --rm --name test-project -v $(pwd):/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java
```

`Ctrl+C` to stop.

## Possible Improvements

- [ ] use [vavr](https://github.com/vavr-io/vavr) for error handling and move validation into the domain object
- [ ] streaming the response from the view