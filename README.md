# ktor-ecommerce

ktor-ecommerce is your new project powered by [Ktor](http://ktor.io) framework.

<img src="https://repository-images.githubusercontent.com/40136600/f3f5fd00-c59e-11e9-8284-cb297d193133" alt="Ktor" width="100" style="max-width:20%;">

Company website: skyz.com Ktor Version: 1.5.1 Kotlin Version: 1.4.10
BuildSystem: [Gradle with Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

# Ktor Documentation

Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.

* Ktor project's [Github](https://github.com/ktorio/ktor/blob/master/README.md)
* Getting started with [Gradle](http://ktor.io/quickstart/gradle.html)
* Getting started with [Maven](http://ktor.io/quickstart/maven.html)
* Getting started with [IDEA](http://ktor.io/quickstart/intellij-idea.html)

Selected Features:

* [Routing](#routing-documentation-jetbrainshttpswwwjetbrainscom)
* [Authentication](#authentication-documentation-jetbrainshttpswwwjetbrainscom)
* [ContentNegotiation](#contentnegotiation-documentation-jetbrainshttpswwwjetbrainscom)
* [GSON](#gson-documentation-jetbrainshttpswwwjetbrainscom)

## Routing Documentation ([JetBrains](https://www.jetbrains.com))

Allows to define structured routes and associated handlers.

### Description

Routing is a feature that is installed into an Application to simplify and structure page request handling. This page
explains the routing feature. Extracting information about a request, and generating valid responses inside a route, is
described on the requests and responses pages.

```application.install(Routing) {
    get("/") {
        call.respondText("Hello, World!")
    }
    get("/bye") {
        call.respondText("Good bye, World!")
    }

```

`get`, `post`, `put`, `delete`, `head` and `options` functions are convenience shortcuts to a flexible and powerful
routing system. In particular, get is an alias to `route(HttpMethod.Get, path) { handle(body) }`, where body is a lambda
passed to the get function.

### Usage

## Routing Tree

Routing is organized in a tree with a recursive matching system that is capable of handling quite complex rules for
request processing. The Tree is built with nodes and selectors. The Node contains handlers and interceptors, and the
selector is attached to an arc which connects another node. If selector matches current routing evaluation context, the
algorithm goes down to the node associated with that selector.

Routing is built using a DSL in a nested manner:

```
route("a") { // matches first segment with the value "a"
  route("b") { // matches second segment with the value "b"
     get {…} // matches GET verb, and installs a handler
     post {…} // matches POST verb, and installs a handler
  }
}
```

```
method(HttpMethod.Get) { // matches GET verb
   route("a") { // matches first segment with the value "a"
      route("b") { // matches second segment with the value "b"
         handle { … } // installs handler
      }
   }
}
```

Route resolution algorithms go through nodes recursively discarding subtrees where selector didn't match.

Builder functions:

* `route(path)` – adds path segments matcher(s), see below about paths
* `method(verb)` – adds HTTP method matcher.
* `param(name, value)` – adds matcher for a specific value of the query parameter
* `param(name)` – adds matcher that checks for the existence of a query parameter and captures its value
* `optionalParam(name)` – adds matcher that captures the value of a query parameter if it exists
* `header(name, value)` – adds matcher that for a specific value of HTTP header, see below about quality

## Path

Building routing tree by hand would be very inconvenient. Thus there is `route` function that covers most of the use
cases in a simple way, using path.

`route` function (and respective HTTP verb aliases) receives a `path` as a parameter which is processed to build routing
tree. First, it is split into path segments by the `/` delimiter. Each segment generates a nested routing node.

These two variants are equivalent:

```
route("/foo/bar") { … } // (1)

route("/foo") {
   route("bar") { … } // (2)
}
```

### Parameters

Path can also contain parameters that match specific path segment and capture its value into `parameters` properties of
an application call:

```
get("/user/{login}") {
   val login = call.parameters["login"]
}
```

When user agent requests `/user/john` using `GET` method, this route is matched and `parameters` property will
have `"login"` key with value `"john"`.

### Optional, Wildcard, Tailcard

Parameters and path segments can be optional or capture entire remainder of URI.

* `{param?}` –- optional path segment, if it exists it's captured in the parameter
* `*` –- wildcard, any segment will match, but shouldn't be missing
* `{...}` –- tailcard, matches all the rest of the URI, should be last. Can be empty.
* `{param...}` –- captured tailcard, matches all the rest of the URI and puts multiple values for each path segment
  into `parameters` using `param` as key. Use `call.parameters.getAll("param")` to get all values.

Examples:

```
get("/user/{login}/{fullname?}") { … }
get("/resources/{path...}") { … }
```

## Quality

It is not unlikely that several routes can match to the same HTTP request.

One example is matching on the `Accept` HTTP header which can have multiple values with specified priority (quality).

```
accept(ContentType.Text.Plain) { … }
accept(ContentType.Text.Html) { … }
```

The routing matching algorithm not only checks if a particular HTTP request matches a specific path in a routing tree,
but it also calculates the quality of the match and selects the routing node with the best quality. Given the routes
above, which match on the Accept header, and given the request header `Accept: text/plain; q=0.5, text/html` will
match `text/html` because the quality factor in the HTTP header indicates a lower quality fortext/plain (default is 1.0)
.

The Header `Accept: text/plain, text/*` will match `text/plain`. Wildcard matches are considered less specific than
direct matches. Therefore the routing matching algorithm will consider them to have a lower quality.

Another example is making short URLs to named entities, e.g. users, and still being able to prefer specific pages
like `"settings"`. An example would be

* `https://twitter.com/kotlin` -– displays user `"kotlin"`
* `https://twitter.com/settings` -- displays settings page

This can be implemented like this:

```
get("/{user}") { … }
get("/settings") { … }
```

The parameter is considered to have a lower quality than a constant string, so that even if `/settings` matches both,
the second route will be selected.

### Options

No options()

## Authentication Documentation ([JetBrains](https://www.jetbrains.com))

Handle Basic and Digest HTTP Auth, Form authentication and OAuth 1a and 2

### Description

Ktor supports authentication out of the box as a standard pluggable feature. It supports mechanisms to read credentials,
and to authenticate principals. It can be used in some cases along with the sessions feature to keep the login
information between requests.

### Usage

## Basic usage

Ktor defines two concepts: credentials and principals. A principal is something that can be authenticated: a user, a
computer, a group, etc. A credential is an object that represents a set of properties for the server to authenticate a
principal: a `user/password`, an API key or an authenticated payload signature, etc. To install it, you have to call
to `application.install(Authentication)`. You have to install this feature directly to the application and it won't work
in another `ApplicationCallPipeline` like `Route`. You might still be able to call the install code inside a Route if
you have the `Application` injected in a nested DSL, but it will be applied to the application itself. Using its DSL, it
allows you to configure the authentication providers available:

```
install(Authentication) {
    basic(name = "myauth1") {
        realm = "Ktor Server"
        validate { credentials ->
            if (credentials.name == credentials.password) {
                UserIdPrincipal(credentials.name)
            } else {
                null
            }
        }
    }
}

```

After defining one or more authentication providers (named or unnamed), with the routing feature you can create a route
group, that will apply that authentication to all the routes defined in that group:

```
routing {
    authenticate("myauth1") {
        get("/authenticated/route1") {
            // ...
        }
        get("/other/route2") {
            // ...
        }
    }
    get("/") {
        // ...
    }
}

```

You can specify several names to apply several authentication providers, or none or null to use the unnamed one. You can
get the generated Principal instance inside your handler with:

```
val principal: UserIdPrincipal? = call.authentication.principal<UserIdPrincipal>()

```

In the generic, you have to put a specific type that must match the generated Principal. It will return null in the case
you provide another type. The handler won't be executed if the configured authentication fails (when returning null in
the authentication mechanism)

## Naming the AuthenticationProvider

It is possible to give arbitrary names to the authentication providers you specify, or to not provide a name at all (
unnamed provider) by not setting the name argument or passing a null. You cannot repeat authentication provider names,
and you can define just one provider without a name. In the case you repeat a name for the provider or try to define two
unnamed providers, an exception will be thrown:

```
java.lang.IllegalArgumentException: Provider with the name `authName` is already registered
```

Summarizing:

```
install(Authentication) {
    basic { // Unamed `basic` provider
        // ...
    }
    form { // Unamed `form` provider (exception, already defined a provider with name = null)
        // ...
    }
    basic("name1") { // "name1" provider
        // ...
    }
    basic("name1") { // "name1" provider (exception, already defined a provider with name = "name1")
        // ...
    }
}

```

## Skipping/Omitting Authentication providers

You can also skip an authentication based on a criteria.

```
/**
 * Authentication filters specifying if authentication is required for particular [ApplicationCall]
 * If there is no filters, authentication is required. If any filter returns true, authentication is not required.
 */
fun AuthenticationProvider.skipWhen(predicate: (ApplicationCall) -> Boolean)

```

For example, to skip a basic authentication if there is already a session, you could write:

```
authentication {
    basic {
        skipWhen { call -> call.sessions.get<UserSession>() != null }
    }
}

```

### Options

No options()

## ContentNegotiation Documentation ([JetBrains](https://www.jetbrains.com))

Provides automatic content conversion according to Content-Type and Accept headers.

### Description

The `ContentNegotiation` feature serves two primary purposes:

* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in the specific format, which is provided by either the
  built-in `kotlinx.serialization` library or external ones, such as `Gson` and `Jackson`, amongst others.

### Usage

## Installation

To install the `ContentNegotiation` feature, pass it to the `install` function in the application initialization code.
This can be the `main` function ...

```
import io.ktor.features.*
// ...
fun Application.main() {
  install(ContentNegotiation)
  // ...
}```
... or a specified `module`:

```

import io.ktor.features.*
// ... fun Application.module() { install(ContentNegotiation)
// ... }

```
## Register a Converter
To register a converter for a specified `Content-Type`, you need to call the register method. In the example below, two custom converters are registered to deserialize `application/json` and `application/xml` data:

```

install(ContentNegotiation) { register(ContentType.Application.Json, CustomJsonConverter())
register(ContentType.Application.Xml, CustomXmlConverter())
}

```
### Built-in Converters
Ktor provides the set of built-in converters for handing various content types without writing your own logic:

* `Gson` for JSON

* `Jackson` for JSON

* `kotlinx.serialization` for JSON, Protobuf, CBOR, and so on

See a corresponding topic to learn how to install the required dependencies, register, and configure a converter.

## Receive and Send Data
### Create a Data Class
To deserialize received data into an object, you need to create a data class, for example:

```

data class Customer(val id: Int, val firstName: String, val lastName: String)

```
If you use `kotlinx.serialization`, make sure that this class has the `@Serializable` annotation:

```

import kotlinx.serialization.Serializable

@Serializable data class Customer(val id: Int, val firstName: String, val lastName: String)

```

### Receive Data
To receive and convert a content for a request, call the `receive` method that accepts a data class as a parameter:

```

post("/customer") { val customer = call.receive<Customer>()
}

```
The `Content-Type` of the request will be used to choose a converter for processing the request. The example below shows a sample HTTP client request containing JSON data that will be converted to a `Customer` object on the server side:

```

POST http://0.0.0.0:8080/customer
Content-Type: application/json

{
"id": 1,
"firstName" : "Jet",
"lastName": "Brains"
}

```
### Send Data
To pass a data object in a response, you can use the `respond` method:

```

post("/customer") { call.respond(Customer(1, "Jet", "Brains"))
}

```
In this case, Ktor uses the `Accept` header to choose the required converter.

## Implement a Custom Converter
In Ktor, you can write your own converter for serializing/deserializing data. To do this, you need to implement the `ContentConverter` interface:

```

interface ContentConverter { suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType:
ContentType, value: Any): Any? suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest,
ApplicationCall>): Any? }

```
Take a look at the [GsonConverter](https://github.com/ktorio/ktor/blob/master/ktor-features/ktor-gson/jvm/src/io/ktor/gson/GsonSupport.kt) class as an implementation example.



### Options

No options()

## GSON Documentation ([JetBrains](https://www.jetbrains.com))

Handles JSON serialization using GSON library

### Description

`ContentNegotiation` provides the built-in `Gson` converter for handing JSON data in your application.



### Usage

To register the Gson converter in your application, call the `gson` method:

```

import io.ktor.gson.*

install(ContentNegotiation) { gson()
}

```
Inside the `gson` block, you can access the [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html) API, for example:

```

install(ContentNegotiation) { gson { setPrettyPrinting()
disableHtmlEscaping()
// ... } }

```
To learn how to receive and send data, see [Receive and Send Data](https://helpserver.labs.jb.gg/help/ktor/1.5.1/serialization.html#receive_send_data).



### Options

No options()

# Reporting Issues / Support

Please use [our issue tracker](https://youtrack.jetbrains.com/issues/KTOR) for filing feature requests and bugs. If you'd like to ask a question, we recommmend [StackOverflow](https://stackoverflow.com/questions/tagged/ktor) where members of the team monitor frequently.

There is also community support on the [Kotlin Slack Ktor channel](https://app.slack.com/client/T09229ZC6/C0A974TJ9)

# Reporting Security Vulnerabilities

If you find a security vulnerability in Ktor, we kindly request that you reach out to the JetBrains security team via our [responsible disclosure process](https://www.jetbrains.com/legal/terms/responsible-disclosure.html).

# Contributing

Please see [the contribution guide](CONTRIBUTING.md) and the [Code of conduct](CODE_OF_CONDUCT.md) before contributing.

TODO: contribution of features guide (link)