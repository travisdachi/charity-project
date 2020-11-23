# TAMBOON MOBILE

This project was built with Kotlin 1.4.10 on Android Studio 4.1. The code **can runs** but the IDE will show the error on some `@Serializable` class.
It was due to the incompatibility between Android Gradle plugin and Android Studio, it happens.      

## Project Structure

This project was organized by screen, classes related to each screen are grouped into a single package (high cohesion).
In a larger project each screen would be further grouped by flow, for example in ths case, `SuccessFragment` is in the same package as Donation Fragment.
For those classes that doesn't belong anywhere or being used everywhere are in `shared` package.

## Design Pattern: MVVM

MVVM is one of the popular patterns these day. With official support from Google, it simplifies Android development a lot better than MVP.
It deals with Android-ish problems (like life cycle) for us.

## ViewModel and State

ViewModels are business logic part of the app. Every screen that is not trivial should observe one or more ViewModels. LiveData is used as a mean for view to observe the data and render accordingly.
Each screen has 1 `sealed class` to represent every possible state of the UI. This eliminates invalid UI state and inconsistency altogether.

## Asynchronicity: Coroutine

Coroutine helps avoid callback hell doing networking off the main thread. It allows us to write asynchronous code as if it was synchronous code.
Under the hood, coroutine turns our code cleverly into some sort of state machine and manage threading for us. (Definitely not an AsyncTask like all the meme out there said)

## Unit Test

Unit tests are in test source set, the only 2 files are `CharityListViewModelTest` and `DonationViewModelTest` which considered business logic should be tested.
The goal here is to mock dependencies (`TamBoonApi`) and check if the actual result is what we expected or not.
There are no UI test in the project but if I were to do them. I would supply the fragment with the ViewModel and see if it renders as expect.

## Security concerns

There is an unsecured http call to get the image, while we prefer https, but in this case there is a `network_security_config.xml` to allow domain specific unsecured connection.

## Dependencies & Rationalization

```groovy
implementation "androidx.navigation:navigation-fragment-ktx:2.3.1"
implementation "androidx.navigation:navigation-ui-ktx:2.3.1"
```
Single Activity pattern was used because it helps avoid all those lifecycle problem and plus, give us the app-wide scope for view models (since every fragment lives under the same Activity).
Navigation graph was used to avoid the "FragmentTransaction" hell and for better navigation stack control.

```groovy
implementation 'com.github.bumptech.glide:glide:4.11.0'
```
Glide is an obvious choice for image loading. It caches and resize automatically.

```groovy
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
```
Retrofit is kind of an industry standard. It turns api calls into an interface and we all want to "program to an interface not the implementation" so there is no need to wrap it in another class.
 
```groovy
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1'
implementation 'com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0'
```
Compile time generation of json serialization reduce runtime load and an excellent choice for a Kotlin fan boy.

```groovy
implementation "org.koin:koin-android:2.2.0"
implementation "org.koin:koin-androidx-viewmodel:2.2.0"
```
Even through `Dagger` is recommended by the official doc but it doesn't scale, on a large project it would take forever to compile.
Koin is fast and easy. It is not really a dependency injection but rather a service locator, nevertheless, it serves the purpose.

```groovy
implementation 'co.omise:omise-android:3.1.+'
``` 
DRY - Dont Repeat Yourself. Mainly used for Credit Card Form.