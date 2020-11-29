package be.encelade.ouistiti

enum class ViewMode {

    TOP_VIEW, SIDE_VIEW, ISO_VIEW;

    fun next(): ViewMode {
        val idx = if (ordinal == values().size - 1) 0 else ordinal + 1
        return values()[idx]
    }
}
