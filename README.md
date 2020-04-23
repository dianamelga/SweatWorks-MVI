# sweatworks-android
A challenge from SweatWorks for Android

Support: Android 4.1 (API level 16) and above

### Libraries:
* [RxKotlin](https://github.com/ReactiveX/RxKotlin) //Threading
* [Material](https://material.io/develop/android/docs/getting-started/) //Design
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) //Lifecycle
* [Retrofit](https://square.github.io/retrofit/) //Networking
* [Gson](https://github.com/google/gson) //JSON parser
* [Lottie](https://github.com/airbnb/lottie-android) //Animations
* [Picasso](https://github.com/square/picasso) //Image handler
* [CircleImageView](https://github.com/hdodenhof/CircleImageView) //Circle ImageView 

### Project Structure:

[MVI - A Reactive Architecture Pattern for Android](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started)

* resources:  fonts, strings, images, generated files etc.
* mvibase: interfaces that describes behavior of MVI
* models:  model objects
* modules: contains app modules (UI + Code + MVI Interfaces/Impl)
* network: retrofit implementations
* repository: data handling
* utils: extension and utility classes

### The architecture overview:

![Guide to app architecture](https://raw.githubusercontent.com/oldergod/android-architecture/todo-mvi-rxjava-kotlin/art/MVI_detail.png)

### Maintainability

 ***Ease of amending or adding a feature***
High. Side effects are restrained and since every part of the architecture has a well defined purpose, adding a feature is only a matter of creating a new isolated processor and plug it into the existing stream.
***Learning cost***
Medium as reactive and functional programming, as well as Observables are not trivial.


### ToDos

 - Write Functional/Instrumental/UI Tests

