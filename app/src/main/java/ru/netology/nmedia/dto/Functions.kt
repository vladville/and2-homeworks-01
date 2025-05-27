package ru.netology.nmedia.dto

fun numbersToThousands(num: Int = 0): String {
    var result: String = ""
    var int: Int = 0
    var fractional: Int = 0

    if (num >= 1000 && num < 10_000) {
        fractional = num % 1_000
        if (fractional > 100) {
            result = ((num / 1000).toInt()).toString() + "." + (fractional / 100).toString() + "K"
        } else {
            result = ((num / 1000).toInt()).toString() + "K"
        }
    } else if (num >= 10_000 && num < 1_000_000) {
        result = ((num / 1000).toInt()).toString() + "K"
    } else if (num >= 1_000_000) {
        fractional = num % 1_000_000
        if (fractional > 100_000) {
            result =
                ((num / 1_000_000).toInt()).toString() + "." + (fractional / 100_000).toString() + "M"
        } else {
            result = ((num / 1_000_000).toInt()).toString() + "M"
        }
    } else {
        result = num.toString()
    }

    return result
}