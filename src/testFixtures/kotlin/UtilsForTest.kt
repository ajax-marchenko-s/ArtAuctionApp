fun getRandomString(length: Int = 10): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomEmail(): String {
    return getRandomString(10) + "@email.com"
}
