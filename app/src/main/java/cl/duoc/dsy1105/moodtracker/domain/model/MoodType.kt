package cl.duoc.dsy1105.moodtracker.domain.model

enum class MoodType(
    val emoji: String,
    val displayName: String,
    val color: Long
) {
    HAPPY("😊", "Feliz", 0xFFFFD700),
    SAD("😢", "Triste", 0xFF4682B4),
    ANXIOUS("😰", "Ansioso", 0xFFFF6347),
    CALM("😌", "Tranquilo", 0xFF98FB98),
    ANGRY("😠", "Enojado", 0xFFDC143C),
    EXCITED("🤩", "Emocionado", 0xFFFF1493),
    TIRED("😴", "Cansado", 0xFF9370DB)
}
