# README

This is a small example of using [Cask](https://com-lihaoyi.github.io/cask/) and [htmx](https://htmx.org/).

## Features

- Server-side rendering using Cask
- htmx polling: updates a random percentage every 5 seconds
- htmx click event: adds timestamps to a log list when Shift + Click is triggered
- Toggle color block using htmx and server-side state

## Running the app

This app uses Scala CLI. If you don't have it yet, install from [scala-cli.virtuslab.org](https://scala-cli.virtuslab.org/).

Run the app with:
```
scala-cli App.scala
```

The server will start at: [http://localhost:8080](http://localhost:8080)
