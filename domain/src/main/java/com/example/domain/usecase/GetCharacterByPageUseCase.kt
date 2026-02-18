package com.example.domain.usecase

import com.example.domain.models.CharacterResponse
import com.example.domain.repo.CharacterRepo

class GetCharacterByPageUseCase(private val  characterRepo: CharacterRepo) {

        suspend operator fun invoke(page: Int): CharacterResponse {
        // الـ UseCase يجب أن يستدعي المستودع ويعيد النتيجة مباشرة
        // المستودع هو المسؤول عن إرجاع CharacterResponse
        return characterRepo.getCharacterByPage(page)
    }
}