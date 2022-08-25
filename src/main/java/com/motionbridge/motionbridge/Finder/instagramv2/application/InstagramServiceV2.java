package com.motionbridge.motionbridge.Finder.instagramv2.application;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstagramServiceV2 {

    @SneakyThrows
    public void loginInsta(String profile) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String login2 = "effectivemotion.application@gmail.com";
        String password2 = "qweqweqwe";

        IGClient client = IGClient.builder()
                .username(login2)
                .password(password2)
                .login().setHttpClient(okHttpClient);

//        String email = "hello.motionbridge@gmail.com";
//        String login = "hellomotionbridge8";
//        String password = "evelnejiutacqbvk";

        final UsersSearchResponse usersSearchResponse = client.actions().search().searchUser(profile).get();
        final String page_token = usersSearchResponse.getPage_token();
    }

}
