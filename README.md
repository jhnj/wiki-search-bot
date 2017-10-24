# Wiki search bot

Telegram bot for doing wikipedia graph searches for the shortest paths between articles.
Uses the files created with [wikipedia-graph](https://github.com/jhnj/wikipedia-graph).

The project is built using functional Scala with [fs2](https://github.com/functional-streams-for-scala/fs2),
[cats](https://github.com/typelevel/cats) and [doobie](https://github.com/tpolecat/doobie).

# Usage

Requirements to run:
  - `Scala 2.12.3` + `sbt 0.13.16`
  - `sqlite3`

To run the bot you will need to get the files `index.db` and `graph.bin` created by
[wikipedia-graph](https://github.com/jhnj/wikipedia-graph). Then you will need to set up a 
Telegram bot, instructions can be found [here](https://core.telegram.org/bots/api).

Next set up [webhooks](https://core.telegram.org/bots/api#setwebhook) for your bot and
create `src/main/resources/application.conf` using `src/main/resources/application.conf.sample`
and add your bot's id and the path to the data files to it.

To start the bot locally simply run `sbt run`. For deployment you can create a executable
jar requiring only Java using `sbt assembly`.

Now your bot should be up and running and able to respond to questions of the form
`/search [Star Trek: The Next Generation] [Planar ternary ring]` resulting in:
`Shortest path: star trek: the next generation -> ntsc -> cie 1931 color space ->
projective plane -> planar ternary ring`




