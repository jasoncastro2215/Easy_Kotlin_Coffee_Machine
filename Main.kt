/*
What can be better than a cup of coffee during a break? A coffee that you don’t have to make yourself.
It’s enough to press a couple of buttons on the machine and you get a cup of energy; but first,
we should teach the machine how to do it. In this project, you will work on programming a coffee machine simulator.
The machine works with typical products: coffee, milk, sugar, and plastic cups;
if it runs out of something, it shows a notification.
You can get three types of coffee: espresso, cappuccino, and latte. Since nothing’s for free, it also collects the money.
 */
enum class CoffeeVariant(
    private val water: Int,
    private val milk: Int,
    private val coffeeBean: Int,
    private val price: Int
) {
    ESPRESSO(250, 0, 16, 4),
    LATTE(350, 75, 20, 7),
    CAPUCCINO(200, 100, 12, 6);

    fun getWater(): Int {
        return water
    }

    fun getMilk(): Int {
        return milk
    }

    fun getCoffeeBean(): Int {
        return coffeeBean
    }

    fun getPrice(): Int {
        return price
    }
}

enum class Fill(private val promptText: String) {
    WATER("Write how many ml of water you want to add:"),
    MILK("Write how many ml of milk you want to add:"),
    COFFEEBEAN("Write how many grams of coffee beans you want to add:"),
    CUP("Write how many disposable cups you want to add:");

    fun getPromptText(): String {
        return promptText
    }
}

enum class Status {
    PROMPT, CHOOSE
}

val CHOOSE = Status.CHOOSE
val PROMPT = Status.PROMPT

class CoffeeMachine() {
    private var money = 550
    private var water = 400
    private var milk = 540
    private var coffeeBean = 120
    private var cup = 9

    private var status = PROMPT
    private var wantToExit = false
    private var buying = false
    private var filling = false
    private var fillOrder = 0

    init {
        chooseAction()
    }

    fun input(text: String = "") {
        when (status) {
            PROMPT -> {
                when (text) {
                    "buy" -> buy()
                    "fill" -> fill()
                    "take" -> take()
                    "remaining" -> showMachineState()
                    "exit" -> wantToExit = true
                    else -> {
                        if (buying) buy()
                        else if (filling) fill()
                    }
                }
            }

            else -> {
                if (buying) buy(text)
                else if (filling) fill(text.toInt())
            }
        }
        if (!(buying || filling)) chooseAction()

        if ((buying || filling) && !wantToExit && status == PROMPT) input()
        else if (!wantToExit) input(readln())
    }

    private fun chooseAction() {
        println("Write action (buy, fill, take, remaining, exit):")
    }

    private fun fill(fillQuantity: Int = 0) {
        when (status) {
            PROMPT -> {
                if (fillOrder == 0) println()
                println(Fill.values()[fillOrder].getPromptText())
                status = CHOOSE
                filling = true
            }

            else -> {
                when (fillOrder) {
                    Fill.WATER.ordinal -> water += fillQuantity
                    Fill.MILK.ordinal -> milk += fillQuantity
                    Fill.COFFEEBEAN.ordinal -> coffeeBean += fillQuantity
                    Fill.CUP.ordinal -> cup += fillQuantity
                }

                if (fillOrder == Fill.values().last().ordinal) {
                    fillOrder = 0
                    filling = false
                    println()
                } else fillOrder++

                status = PROMPT
            }
        }
    }

    private fun buy(userChoice: String = "") {
        when (status) {
            PROMPT -> {
                println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
                buying = true
                status = CHOOSE
            }

            else -> {
                try {
                    for (coffeeVariant in CoffeeVariant.values()) {
                        val uChoice = userChoice.toInt()
                        if (coffeeVariant.ordinal == uChoice - 1) {
                            makeCoffee(CoffeeVariant.values()[uChoice - 1])
                        }
                    }
                } catch (e: NumberFormatException) {}

                println()
                buying = false
                status = PROMPT
            }
        }
    }

    private fun makeCoffee(coffeeVariant: CoffeeVariant) {
        var resource = ""
        if (water - coffeeVariant.getWater() < 0) resource = "water"
        else if (milk - coffeeVariant.getMilk() < 0) resource = "milk"
        else if (coffeeBean - coffeeVariant.getCoffeeBean() < 0) resource = "coffee bean"
        else if (cup == 0) resource = "cup"

        if (resource == "") {
            println("I have enough resources, making you a coffee!")
            water -= coffeeVariant.getWater()
            milk -= coffeeVariant.getMilk()
            coffeeBean -= coffeeVariant.getCoffeeBean()
            cup--
            money += coffeeVariant.getPrice()
        } else println("Sorry, not enough $resource!")
    }

    private fun take() {
        println("I gave you \$$money\n")
        money = 0
    }

    private fun showMachineState() {
        print(
            "\nThe coffee machine has:\n" +
                    "$water ml of water\n" +
                    "$milk ml of milk\n" +
                    "$coffeeBean g of coffee beans\n" +
                    "$cup disposable cups\n" +
                    "\$$money of money\n\n"
        )
    }
}

fun main() {
    CoffeeMachine().input(readln())
}