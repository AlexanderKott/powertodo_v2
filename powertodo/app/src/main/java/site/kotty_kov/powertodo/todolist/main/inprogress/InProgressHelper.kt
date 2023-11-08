package site.kotty_kov.powertodo.todolist.main.inprogress




val timeValues  = arrayOf<Int>(
    60 * 15,
    60 * 30,
    60 * 60,
    60 * 60 * 3,
    60 * 60 * 4,
    60 * 60 * 8, //5

    60 * 5,
    60 * 15,
    60 * 60,
    60 * 60 * 3,
    60 * 60 * 8
)


fun timeToReadableFormat(digit: Int): String {
    val hours = digit  / 3600
    var remainder = digit - hours * 3600
    val mins = remainder / 60
    return "$hours h, $mins min"
}