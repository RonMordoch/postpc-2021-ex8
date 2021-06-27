# postpc-2021-ex8: FindRootsWorkManager

An android app for learning WorkManager.
Find roots using a simple loop in a worker, or classify number as prime if no roots were found.

## Project Structure

In the main package:
* MainActivity.kt : the main ui component of the app, displays the recycler-view with the calculations and lets the user input numbers to start new calculations.
* CalculationsAdapter : an adapter for the recycler-view located in MainActivity.
* FindRootsApp : a custom Application class that saves the calculation's database to SharedPreferences.
* CalculationDeleteClickListener : an interface which defines a callback from recycler-view to the containing parent (MainActivity)

The project has 2 sub-packages:
* models : Contains a data class Calculation that represents a single calculation, and the CalculationsDatabase class which holds all calculations started (in-progress and finished, but not aborted).
* workers : Contains a CalculationWorker class which extends from CoroutineWorker (WorkManager's support for Kotlin's Coroutines) that performs a single calculation.

The user's database will be saved upon device rotations and closing the app thanks to the custom Application class and SharedPreferences.
WorkManager takes care of resuming the workers after a few moments when the app is killed and re-launched.

A short demo of the app working:

<img src="demo_short_gif.gif" width="50%" height="50%">


## Android components and libraries used

* RecyclerView
* WorkManager
* LiveData
* Material Design Components


## Academic Integrity
I pledge the highest level of ethical principles in support of academic excellence.  
I ensure that all of my work reflects my own abilities and not those of someone else.
