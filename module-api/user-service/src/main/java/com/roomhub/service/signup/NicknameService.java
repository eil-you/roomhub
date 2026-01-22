package com.roomhub.service.signup;

import com.roomhub.model.NicknameResponse;
import com.roomhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor()
public class NicknameService {

    private final UserRepository userRepository;
    private static final String[] ADJECTIVE = { "빠른", "신난", "아름다운", "기쁜", "졸린", "용감한", "귀여운", "멋진", "행복한" };
    private static final String[] NOUNS = { "토끼", "호랑이", "사자", "펭귄", "코끼리" };
    private final Random random = new Random();

    public NicknameResponse generateNickname() {

        String nickname;
        do {
            String adj = ADJECTIVE[random.nextInt(ADJECTIVE.length)];
            String noun = NOUNS[random.nextInt(NOUNS.length)];
            int number = random.nextInt(100);
            nickname = adj + noun + number;
        } while (!isNicknameExist(nickname));

        NicknameResponse nicknameResponse = new NicknameResponse(nickname);

        return nicknameResponse;
    }

    public boolean isNicknameExist(String nickname) {
        return userRepository.findByNickname(nickname) == null;

    }
}
