# Currency Exchanger

### Challenge description

List all currencies you get from the endpoint. Each row has an input where you can enter any 
amount of money. When you tap on currency row it should slide to top and its input becomes first 
responder. When you’re changing the amount the app must simultaneously update the corresponding 
value for other currencies.

Use any libraries and languages(java/kotlin) you want. UI does not have to be exactly the same, 
it’s up to you.


### Architecture & Design Pattern

This project was built respecting SOLID concepts working with MVP framework, Dagger2-Android, 
ReactiveJava2, Retrofit, and Junit4.

**Language : Kotlin**

### Bonus for a better UX

For a better UX experience I used the Revolut App as an example to implement some extra features:

* Beautiful interface
* Cache of the last value inputted
* Cache of list order priority
* Multi language support - (ptBr and enUS)

To make the local storage simple I've chosen work with SharedPreferences but respecting the 
separation of concerns it's possible to connect any other kind of persistence just implementing 
the `LocalStore` interface.

### QA

* Unit test to ensure the quality and resilience of the code
* A lot of user interactions to search and destroy bugs






