# aemette

A Leiningen template for Om.

## Description

aemette is very opinionated, and it is based on CircleCI's [frontend](https://github.com/circleci/frontend) architecture for the most part.
The generated `src` folder structure is organized in the following way:

- `src/my-project/core.cljs` — application setup login;
- `src/my-project/history.cljs` — contains browser history logic;
- `src/my-project/routes.cljs` — place to declare the app's routes;
- `src/my-project/parser.cljs` — only generated in the Om Next case. Contains a sample Om Next parser implementation for the provided components;
- `src/my-project/components/app.cljs` — main component which encapsulates the logic of rendering other components based on the current route;
- `src/my-project/components/landing.cljs` — a sample component which is rendered by the main `App` component;
- `src/my-project/controllers/navigation.cljs` — contains logic to handle route transitions.


## Usage

The following command creates an `om.core` project:
```shell
lein new aemette my-project
```

To create an `om.next` project, use the `+next` option as below:
```shell
lein new aemette my-project +next
```

## License

Parts of this template are originally taken from CircleCI's [frontend](https://github.com/circleci/frontend) and licensed under the [EPL](https://github.com/circleci/frontend/blob/master/LICENSE). As such, this disclaimer must appear in code distributed with this template.

Copyright © 2015 António Nuno Monteiro

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
