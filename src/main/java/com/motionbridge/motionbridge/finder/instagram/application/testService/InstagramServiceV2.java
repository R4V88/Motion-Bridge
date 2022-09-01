package com.motionbridge.motionbridge.finder.instagram.application.testService;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.CarouselItem;
import com.github.instagram4j.instagram4j.models.media.timeline.ImageCarouselItem;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineCarouselMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.motionbridge.motionbridge.finder.instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.finder.instagram.model.Album;
import com.motionbridge.motionbridge.finder.instagram.web.InstagramController.RequestAlbumCommand;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//@Slf4j
//@AllArgsConstructor
//@Service
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class InstagramServiceV2 implements InstagramUseCase {
//
//
//    ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;
//
//    @Override
//    @SneakyThrows
//    public Album getList(RequestAlbumCommand requestAlbumCommand, String username) {
//        Subscription subscription = manipulateSubscriptionUseCase.findAllByUserEmail(username)
//                .stream()
//                .filter(sub -> sub.getId() == requestAlbumCommand.getSubscriptionId())
//                .findFirst()
//                .orElseThrow(() ->
//                        new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                                "Subscription with id " + requestAlbumCommand.getSubscriptionId() +
//                                        ", for user " + username + " does not exist"));
//
//        if (subscription.getIsActive()) {
//            ParametrizedClient currentClient = generateRandomParametrizedClient();
//            log.info("Generated user with id: " + currentClient.getId());
//            return getAlbumFromInstagram(currentClient, requestAlbumCommand);
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your subscription is not Active");
//        }
//    }
//
////    private IGClient getIgClient() throws IGLoginException {
//////        String login2 = "effectivemotion.application@gmail.com";
////        String login2 = "motionplus.application@gmail.com";
//////        String password2 = "qweqweqwe";
////        String password2 = "QweQweQwe-22";
////        return IGClient.builder()
////                .username(login2)
////                .password(password2)
////                .login();
////    }
//
//    private ParametrizedClient getFirstClient() throws IGLoginException {
//        String login1 = "motionplus.application@gmail.com";
//        String password1 = "QweQweQwe-22";
//        return ParametrizedClient.builder()
//                .id(1)
//                .client(IGClient.builder()
//                        .username(login1)
//                        .password(password1)
//                        .login())
//                .build();
//    }
//
//    private ParametrizedClient getSecondClient() throws IGLoginException {
//        String login2 = "effectivemotion.application@gmail.com";
//        String password2 = "qweqweqwe";
//
//        return ParametrizedClient.builder()
//                .id(2)
//                .client(IGClient.builder()
//                        .username(login2)
//                        .password(password2)
//                        .login())
//                .build();
//    }
//
//    ParametrizedClient switchClient(ParametrizedClient client) throws IGLoginException {
//        ParametrizedClient parametrizedClient = null;
//        switch (client.getId()) {
//            case 1 -> parametrizedClient = getSecondClient();
//            case 2 -> parametrizedClient = getFirstClient();
//        }
//        return parametrizedClient;
//    }
//
//    ParametrizedClient generateRandomParametrizedClient() throws IGLoginException {
//        ParametrizedClient parametrizedClient = null;
//        int random = new Random().nextInt(3 - 1) + 1;
//        switch (random) {
//            case 1 -> parametrizedClient = getSecondClient();
//            case 2 -> parametrizedClient = getFirstClient();
//        }
//        return parametrizedClient;
//    }
//
//    @Value
//    @Builder
//    static class ParametrizedClient {
//        int id;
//        IGClient client;
//    }
//
//    Album getAlbumFromInstagram(ParametrizedClient currentClient, RequestAlbumCommand requestAlbumCommand) throws IGLoginException {
//        List<Album.Photo> imgs = new ArrayList<>();
//        String profileUsername = "";
//        boolean profileIsPrivate = false;
//        String profileFullName = "";
//        boolean profilesVerified = false;
//
//        CompletableFuture<UserAction> action = currentClient.getClient().actions().users().findByUsername(requestAlbumCommand.getProfile());
//
//        Profile profile;
//
//        try {
//            if (action.get() != null) {
//                System.out.println(action.get().getUser());
//                profile = action.get().getUser();
//                Long userId = profile.getPk();
//                profileUsername = profile.getUsername();
//                profileIsPrivate = profile.is_private();
//                profileFullName = profile.getFull_name();
//                profilesVerified = profile.is_verified();
//                URL profilePictureUrl = new URL(profile.getProfile_pic_url());
//                if (profilePictureUrl.toString().length() > 0) {
//                    imgs.add(new Album.Photo(profilePictureUrl.toString()));
//                }
//                if (!profile.is_private()) {
//                    FeedUserRequest req = new FeedUserRequest(userId);
//                    CompletableFuture<FeedUserResponse> response = currentClient.getClient().sendRequest(req);
//
//                    if (response.get() != null) {
//                        FeedUserResponse feedUserResponse = response.get();
//
//                        for (TimelineMedia timelineMedia : feedUserResponse.getItems()) {
//                            int likes = timelineMedia.getLike_count();
//                            int comments = timelineMedia.getComment_count();
//
//                            if (timelineMedia instanceof TimelineImageMedia) {
//                                TimelineImageMedia timelineImageMedia = (TimelineImageMedia) timelineMedia;
//                                String url = timelineImageMedia.getImage_versions2().getCandidates().get(0).getUrl();
//                                imgs.add(new Album.Photo(url, likes, comments));
//
//                            } else if (timelineMedia instanceof TimelineCarouselMedia) {
//
//                                TimelineCarouselMedia timelineCarouselMedia = (TimelineCarouselMedia) timelineMedia;
//                                int index = 0;
//                                for (CarouselItem caraouselItem : timelineCarouselMedia.getCarousel_media()) {
//                                    index++;
//                                    if (caraouselItem.getMedia_type().equals("1")) {
//                                        ImageCarouselItem imageCaraouselItem = (ImageCarouselItem) caraouselItem;
//                                        String url = imageCaraouselItem.getImage_versions2().getCandidates().get(index).getUrl();
//                                        imgs.add(new Album.Photo(url, likes, comments));
//                                        break;
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (InterruptedException | IOException | ExecutionException i) {
////            i.printStackTrace();
//            currentClient = switchClient(currentClient);
//            log.info("Switched to user: " + currentClient.getId());
//            getAlbumFromInstagram(currentClient, requestAlbumCommand);
//        }
//        return new Album(profileUsername, profileFullName, profileIsPrivate, profilesVerified, imgs);
//    }

}
