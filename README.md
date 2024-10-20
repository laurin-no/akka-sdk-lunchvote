# Lunchvote Akka SDK Edition

## Run Locally

```bash
mvn compile exec:java
```

`Ctrl+C` to stop.

The `http` directory contains example requests for the existing endpoints.

## Possible Improvements

- [ ] use [vavr](https://github.com/vavr-io/vavr) for error handling and move validation into the domain object
- [ ] streaming the response from the view