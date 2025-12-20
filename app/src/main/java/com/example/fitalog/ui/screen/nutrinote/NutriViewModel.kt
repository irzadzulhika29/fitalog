package com.example.fitalog.ui.screen.nutrinote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitalog.api.RetrofitClient
import com.example.fitalog.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NutriViewModel : ViewModel() {


    val targetCalories = 1850

    private val _dayIndex = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)
    val dayIndex: StateFlow<Int> = _dayIndex.asStateFlow()

    fun changeDay(delta: Int) {
        _dayIndex.value = (_dayIndex.value + delta + 7) % 7
        // Load meals for new day
        loadMealsForCurrentDay()
    }

    fun resetCurrentDay() {
        val currentDay = dayIndex.value
        _dayMeals.update { old ->
            old.toMutableMap().apply {
                this[currentDay] = emptyMap()
            }
        }
    }

    private val _isLoadingMeals = MutableStateFlow(false)
    val isLoadingMeals: StateFlow<Boolean> = _isLoadingMeals.asStateFlow()

    private fun emptyMeals(): Map<MealType, List<FoodItem>> =
        mapOf(
            MealType.SARAPAN to emptyList(),
            MealType.MAKAN_SIANG to emptyList(),
            MealType.MAKAN_MALAM to emptyList(),
            MealType.KUDAPAN to emptyList()
        )

    private val _dayMeals =
        MutableStateFlow(
            (0..6).associateWith { emptyMeals() }
        )

    val dayMeals: StateFlow<Map<Int, Map<MealType, List<FoodItem>>>> =
        _dayMeals.asStateFlow()

    init {
        loadMealsForCurrentDay()
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingMeals.value = true
                delay(200) // Delay untuk mencegah crash
                loadMealsForCurrentDay()
                fetchFoodsFromApi()
            } catch (e: Exception) {
                println("DEBUG: Error in refreshData: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoadingMeals.value = false
            }
        }
    }

    private fun getDateForDayIndex(dayIndex: Int): Calendar {
        val calendar = Calendar.getInstance()
        // Get current day of week (0 = Sunday, 1 = Monday, etc.)
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        // Calculate offset to target day
        val offset = dayIndex - currentDayOfWeek
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        return calendar
    }

    private fun getIsoDateRange(calendar: Calendar): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val startOfDay = calendar.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)

        val endOfDay = calendar.clone() as Calendar
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        return dateFormat.format(startOfDay.time) to dateFormat.format(endOfDay.time)
    }

    fun loadMealsForCurrentDay() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingMeals.value = true
                val api = RetrofitClient.apiService

                val calendar = getDateForDayIndex(_dayIndex.value)
                val (startDate, endDate) = getIsoDateRange(calendar)

                println("DEBUG: Loading meals for day index: ${_dayIndex.value}")
                println("DEBUG: Date range: $startDate to $endDate")

                val historyList: List<FoodHistoryWithDetails> = try {
                    api.getFoodHistoryWithDetails()
                } catch (e: Exception) {
                    println("DEBUG: Error fetching history from API: ${e.message}")
                    e.printStackTrace()
                    emptyList()
                }

                println("DEBUG: Loaded ${historyList.size} history records from API")

                // Filter by date on client side
                val dateFormatUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val dateFormatWithZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val dateFormatWithTZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)

                val startTime = try {
                    dateFormatUTC.parse(startDate)?.time ?: 0
                } catch (e: Exception) {
                    println("DEBUG: Error parsing start date: ${e.message}")
                    0
                }

                val endTime = try {
                    dateFormatUTC.parse(endDate)?.time ?: Long.MAX_VALUE
                } catch (e: Exception) {
                    println("DEBUG: Error parsing end date: ${e.message}")
                    Long.MAX_VALUE
                }

                val filteredHistory = historyList.filter { history ->
                    try {
                        val eatenTime = try {
                            dateFormatWithTZ.parse(history.eatenAt)?.time
                        } catch (e: Exception) {
                            try {
                                dateFormatWithZ.parse(history.eatenAt)?.time
                            } catch (e2: Exception) {
                                try {
                                    dateFormatUTC.parse(history.eatenAt)?.time
                                } catch (e3: Exception) {
                                    null
                                }
                            }
                        } ?: 0

                        val inRange = eatenTime >= startTime && eatenTime < endTime
                        println("DEBUG: ${history.eatenAt} -> $eatenTime, range: $startTime-$endTime, inRange: $inRange")
                        inRange
                    } catch (e: Exception) {
                        println("DEBUG: Error parsing date ${history.eatenAt}: ${e.message}")
                        e.printStackTrace()
                        false
                    }
                }

                println("DEBUG: Filtered to ${filteredHistory.size} records for current day")

                val mealsMap = emptyMeals().toMutableMap()

                filteredHistory.forEach { history ->
                    try {
                        println("DEBUG: Processing history: meal=${history.mealType}, food=${history.nutrinote_input?.name}")

                        val mealType = try {
                            MealType.valueOf(history.mealType)
                        } catch (e: IllegalArgumentException) {
                            println("DEBUG: Invalid meal type: ${history.mealType}")
                            null
                        }

                        if (mealType != null && history.nutrinote_input != null) {
                            val food = history.nutrinote_input
                            val foodItem = FoodItem(
                                id = food.id,
                                name = food.name,
                                calories = food.calories * history.servingCount,
                                servingText = if (history.servingCount > 1)
                                    "${history.servingCount} x ${food.servingText}"
                                else food.servingText,
                                imageUrl = food.imageUrl,
                                historyId = history.id,
                                servingCount = history.servingCount
                            )

                            mealsMap[mealType] = mealsMap[mealType]!! + foodItem
                            println("DEBUG: Added ${food.name} to ${mealType}")
                        }
                    } catch (e: Exception) {
                        println("DEBUG: Error processing history item: ${e.message}")
                        e.printStackTrace()
                    }
                }

                println("DEBUG: Final meals map: ${mealsMap.mapValues { it.value.size }}")

                val updatedDayMeals = _dayMeals.value.toMutableMap()
                updatedDayMeals[_dayIndex.value] = mealsMap
                _dayMeals.value = updatedDayMeals

            } catch (e: Exception) {
                e.printStackTrace()
                println("DEBUG: Error loading meals: ${e.message}")
                val updatedDayMeals = _dayMeals.value.toMutableMap()
                updatedDayMeals[_dayIndex.value] = emptyMeals()
                _dayMeals.value = updatedDayMeals
            } finally {
                _isLoadingMeals.value = false
            }
        }
    }

    fun addFoodToCurrentDay(
        meal: MealType,
        food: FoodItem,
        servingCount: Int = 1,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.apiService

                val calendar = try {
                    getDateForDayIndex(_dayIndex.value)
                } catch (e: Exception) {
                    println("DEBUG: Error getting date for day index: ${e.message}")
                    Calendar.getInstance()
                }

                val now = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, now.get(Calendar.SECOND))

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).apply {
                    timeZone = TimeZone.getDefault()
                }
                val timestamp = try {
                    dateFormat.format(calendar.time)
                } catch (e: Exception) {
                    println("DEBUG: Error formatting timestamp: ${e.message}")
                    dateFormat.format(Date())
                }

                val totalCalories = try {
                    (food.calories * servingCount).toLong()
                } catch (e: Exception) {
                    println("DEBUG: Error calculating calories: ${e.message}")
                    0L
                }

                val request = FoodHistoryInsertRequest(
                    foodId = food.id,
                    servingCount = servingCount,
                    calories = totalCalories,
                    mealType = meal.name,
                    eatenAt = timestamp
                )

                println("DEBUG: Inserting food history: $request")

                val result = try {
                    api.insertFoodHistory(request)
                } catch (e: Exception) {
                    println("DEBUG: API insert error: ${e.message}")
                    e.printStackTrace()
                    throw e
                }

                println("DEBUG: Insert result: $result")

                delay(300)

                try {
                    loadMealsForCurrentDay()
                } catch (e: Exception) {
                    println("DEBUG: Error reloading meals after insert: ${e.message}")
                    e.printStackTrace()
                }

                launch(Dispatchers.Main) {
                    try {
                        onSuccess()
                    } catch (e: Exception) {
                        println("DEBUG: Error in onSuccess callback: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("DEBUG: Error adding food: ${e.message}")
                launch(Dispatchers.Main) {
                    try {
                        onError(e.message ?: "Gagal menambahkan makanan")
                    } catch (callbackError: Exception) {
                        println("DEBUG: Error in onError callback: ${callbackError.message}")
                        callbackError.printStackTrace()
                    }
                }
            }
        }
    }

    fun removeFoodFromCurrentDay(
        meal: MealType,
        historyId: Long,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.apiService

                println("DEBUG: Deleting food history with id: $historyId")

                try {
                    api.deleteFoodHistory("eq.$historyId")
                    println("DEBUG: Successfully deleted history id: $historyId")
                } catch (e: Exception) {
                    println("DEBUG: API delete error: ${e.message}")
                    e.printStackTrace()
                    throw e
                }

                delay(300)

                try {
                    loadMealsForCurrentDay()
                } catch (e: Exception) {
                    println("DEBUG: Error reloading meals after delete: ${e.message}")
                    e.printStackTrace()
                }

                launch(Dispatchers.Main) {
                    try {
                        onSuccess()
                    } catch (e: Exception) {
                        println("DEBUG: Error in onSuccess callback: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("DEBUG: Error removing food: ${e.message}")
                launch(Dispatchers.Main) {
                    try {
                        onError(e.message ?: "Gagal menghapus makanan")
                    } catch (callbackError: Exception) {
                        println("DEBUG: Error in onError callback: ${callbackError.message}")
                        callbackError.printStackTrace()
                    }
                }
            }
        }
    }

    fun totalCaloriesForCurrentDay(): Int =
        _dayMeals.value[_dayIndex.value]
            ?.values
            ?.flatten()
            ?.sumOf { it.calories } ?: 0


    private val _foodsFromApi = MutableStateFlow<List<FoodItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun fetchFoodsFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.apiService
                val foods: List<FoodResponse> = try {
                    api.getFoods()
                } catch (e: Exception) {
                    println("DEBUG: Error fetching foods from API: ${e.message}")
                    e.printStackTrace()
                    emptyList()
                }

                _foodsFromApi.value = foods.mapNotNull { food ->
                    try {
                        FoodItem(
                            id = food.id,
                            name = food.name,
                            calories = food.calories,
                            servingText = food.servingText,
                            imageUrl = food.imageUrl
                        )
                    } catch (e: Exception) {
                        println("DEBUG: Error mapping food item: ${e.message}")
                        e.printStackTrace()
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("DEBUG: Error in fetchFoodsFromApi: ${e.message}")
                _foodsFromApi.value = emptyList()
            }
        }
    }

    fun insertFoodToApi(
        name: String,
        calories: Int,
        servingText: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.apiService
                val request = FoodInsertRequest(
                    name = name,
                    calories = calories,
                    servingText = servingText,
                    imageUrl = imageUrl
                )

                try {
                    api.insertFood(request)
                    println("DEBUG: Successfully inserted food: $name")
                } catch (e: Exception) {
                    println("DEBUG: API insert food error: ${e.message}")
                    e.printStackTrace()
                    throw e
                }

                // Refresh food list after insert
                try {
                    fetchFoodsFromApi()
                } catch (e: Exception) {
                    println("DEBUG: Error refreshing foods after insert: ${e.message}")
                    e.printStackTrace()
                }

                launch(Dispatchers.Main) {
                    try {
                        onSuccess()
                    } catch (e: Exception) {
                        println("DEBUG: Error in onSuccess callback: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("DEBUG: Error in insertFoodToApi: ${e.message}")
                launch(Dispatchers.Main) {
                    try {
                        onError(e.message ?: "Gagal menyimpan makanan")
                    } catch (callbackError: Exception) {
                        println("DEBUG: Error in onError callback: ${callbackError.message}")
                        callbackError.printStackTrace()
                    }
                }
            }
        }
    }

    val allFoods: StateFlow<List<FoodItem>> = _foodsFromApi.asStateFlow()
    val uiFoodList: StateFlow<List<FoodItem>> =
        combine(_foodsFromApi, _searchQuery) { foods, q ->
            if (q.isBlank()) emptyList()
            else foods.filter { it.name.contains(q, ignoreCase = true) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}