import kotlin.random.Random

fun getRandomString(length: Int = 10): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomInt(min: Int = 1, max: Int = 1000) = Random.nextInt(max - min + 1) + min
