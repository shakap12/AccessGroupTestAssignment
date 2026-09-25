# Hotel Availability

A command line program that reads hotel and booking data from two JSON files,
then answers availability questions typed on standard input.

Needs Java 21 and Maven.

## Build and run

```
mvn clean package
java -jar target/hotel-availability.jar --hotels hotels.json --bookings bookings.json
```

Sample `hotels.json` and `bookings.json` matching the examples in the brief are
in the root of the repository.

The program reads one command per line and writes one line of output for each.
A blank line or the end of the input ends it.

```
Availability(H1, 20240901, SGL)
2
Availability(H1, 20240901-20240903, DBL)
1
Search(H1, 365, SGL)
(20240901-20240902, 2), (20240902-20240903, 1), (20240904-20240906, 1), (20240906-20250901, 2)
```

The `Search` output above assumes the current date is 20240901, as the example
in the brief does. Search always looks ahead from today, so with the sample
bookings now in the past it will report the full room count instead. The dated
examples from the brief are covered in `SearchTest`, which passes the date in.

`Availability` takes one date or a range and prints a single integer, which can
be negative where the hotel is over capacity. `Search` looks a number of nights
ahead from today and prints the stretches with a room free, or an empty line if
there are none.

Run the tests with `mvn test`.

## How it works

Everything rests on one calculation: **free rooms on a night = the rooms the
hotel has of that type, minus the bookings covering that night.**

Three rules come out of the brief and its examples.

A stay runs from arrival inclusive to departure exclusive, so the departure
night is free for someone else. That is the rule the examples turn on:
`Availability(H1, 20240902-20240903, SGL)` is 1, and would be -1 if the end
date counted.

A stay of several nights is worth its worst night, not a total or an average,
because the guest needs the same room throughout. One full night in the middle
makes the whole stay unbookable.

Identical bookings each count, since two guests booking the same room type on
the same dates take two rooms. That is where the -1 on 20240903 comes from:
two rooms, three overlapping bookings.

Search applies the same per-night sum across the window and joins neighbouring
nights into one range while the count stays the same. A night with nothing free
is left out and splits the ranges either side of it, which is why the 365 day
example reports `20240902-20240903` and `20240904-20240906` separately.

## Layout

`Main` reads the arguments and loops over standard input. `CommandRunner` takes
a line and returns the line to print. `AvailabilityService` does both
calculations. `HotelStore` holds the loaded data, grouped by hotel and room
type as it is read since that is the only way it is looked up. `DateRange` is a
run of nights. The `model` package is the four records the JSON maps onto.

Two things worth pointing out. `DateRange` holds the arrival/departure rule on
its own, so nothing else has to remember it, and it is the easiest thing here
to get wrong by a day. `CommandRunner` takes today's date as a parameter rather
than reading the clock inside the calculation, which is what lets the Search
examples, each of which names a current date, run as tests.

## Assumptions

- The JSON matches the schema in the brief. A malformed file stops the program
  at startup rather than being silently skipped.
- Dates in the files and in commands are valid `YYYYMMDD`.
- `roomRate` does not affect availability, so `Prepaid` and `Standard` are
  treated the same.
- Bookings are for a room type, not a named room, so one booking uses up one
  room of its type.
- An unknown hotel or room type gives 0, and an empty line from Search, rather
  than an error.
- A command that cannot be read gives an empty line. The results go to a
  checker reading one line out per line in, so failing on one bad line would
  throw away every result after it.
- `Search(H1, N, SGL)` looks at N nights starting tonight, so the window is
  today up to but not including today + N. The `Search(H2, 7, SGL)` example
  confirms this: H2's only single is booked until the 7th, and the 7th is
  reported as free.
- Hotel IDs and room type codes match exactly, including case.
- A range ending where it starts, such as `20240901-20240901`, is read as that
  single night, since zero nights is not a stay anyone can book.
- Output lines end with `\n` rather than the Windows `\r\n`, so the output does
  not depend on the machine it runs on.

## Note on AI usage

I used Claude in bits and places rather than throughout. Mainly to put together
the initial structure and skeleton of the classes, and then to check for edge
cases and anything I had left unimplemented. The rules, the decisions and the
final code are mine, and I am happy to talk through any of it.
