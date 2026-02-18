package com.example.domain.models

sealed class CharacterStatus(val displayName: String, val colorHex: Long) {
    object Alive : CharacterStatus("Alive", 0xFF4CAF50)     // أخضر
    object Dead : CharacterStatus("Dead", 0xFFF44336)       // أحمر
    object Unknown : CharacterStatus("Unknown", 0xFFFFEB3B) // أصفر
}
