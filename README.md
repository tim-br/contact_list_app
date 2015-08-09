# contact_app_json

This app uses Reagent and Luminus to display a simple contact list app.

It runs on a local postgres database.

To run the app locally, run the `make_contact_table.sql` file on a local database.

You will need to create a file in the root directory called `profiles.clj`

which must follow this template:

```
{:profiles/dev  {:env {:database-url "jdbc:postgresql://localhost/DATABASE_NAME?user=USER_NAME&password=PASSWORD"}}
 :profiles/test {:env {:database-url "jdbc:postgresql://localhost/DATABASE_NAME?user=USER_NAME&password=PASSWORD="}}}
```

Then it can be run with `lein run` and `lein figwheel`, like normal.



