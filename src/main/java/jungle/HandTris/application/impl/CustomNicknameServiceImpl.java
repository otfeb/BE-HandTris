package jungle.HandTris.application.impl;

import jakarta.transaction.Transactional;
import jungle.HandTris.application.service.CustomNicknameService;
import jungle.HandTris.domain.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CustomNicknameServiceImpl implements CustomNicknameService {
    private static final List<String> ADJECTIVES = List.of(
            "밝은", "푸른", "따뜻한", "시원한", "귀여운", "멋진", "예쁜", "친절한", "다정한", "활발한"
    );

    private static final List<String> NOUNS = List.of(
            "햇살", "바람", "구름", "별", "달", "꽃", "나무", "강", "바다", "하늘",
            "고양이", "강아지", "새", "물고기", "나비", "사슴", "토끼", "다람쥐", "곰", "여우",
            "책", "음악", "영화", "그림", "사진", "커피", "차", "빵", "과일", "채소",
            "꿈", "희망", "사랑", "행복", "웃음", "눈물", "기쁨", "슬픔", "분노", "용기",
            "친구", "가족", "연인", "선생님", "동료", "이웃", "낯선", "오래된", "새로운", "신비한"
    );

    private final Random random = new Random();

    private final MemberRepository memberRepository; // 닉네임 중복 검사를 위한 Repository

    private String generateRandomNickname() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        int number = random.nextInt(9999) + 1;
        String formattedNumber = String.format("%04d", number);
        return adjective + noun + formattedNumber;
    }

    @Transactional
    @Override
    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = generateRandomNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }
}
