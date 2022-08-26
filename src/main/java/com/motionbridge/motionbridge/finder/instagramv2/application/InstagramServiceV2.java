package com.motionbridge.motionbridge.finder.instagramv2.application;

import com.beust.ah.A;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.CarouselItem;
import com.github.instagram4j.instagram4j.models.media.timeline.ImageCarouselItem;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineCarouselMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import com.motionbridge.motionbridge.finder.Instagram.model.Album;
import com.motionbridge.motionbridge.finder.Instagram.model.Album.Photo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.OkHttpClient;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

//import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstagramServiceV2 {

    @SneakyThrows
    public Album loginInsta(String profile) {
//        OkHttpClient okHttpClient = new OkHttpClient();
        String login2 = "effectivemotion.application@gmail.com";
        String password2 = "qweqweqwe";

        IGClient client = IGClient.builder()
                .username(login2)
                .password(password2)
                .login();

//        String email = "hello.motionbridge@gmail.com";
//        String login = "hellomotionbridge8";
//        String password = "evelnejiutacqbvk";

        final Album list = getList(profile);

        final UsersSearchResponse usersSearchResponse = client.actions().search().searchUser(profile).get();
        final String page_token = usersSearchResponse.getPage_token();

        return list;
    }

//    @SneakyThrows
//    private Profile switchUser(String userName) {
//        String login2 = "effectivemotion.application@gmail.com";
//        String password2 = "qweqweqwe";
//        IGClient client = IGClient.builder()
//                .username(login2)
//                .password(password2)
//                .login();
//
////        IGClient client = login.getClient().getIgClient();
//        CompletableFuture<UserAction> action = client.actions().users().findByUsername(userName);
//
//        Profile profile = null;
//
//        try {
//            if(action.get() != null)  {
//                System.out.println(action.get().getUser());
//                profile = action.get().getUser();
//                Long userId = profile.getPk();
//                URL profilePictureUrl = new URL(profile.getProfile_pic_url());
//                if(profilePictureUrl.toString().length() > 0) {
//                    BufferedImage image = ImageIO.read(profilePictureUrl);
//                    image = Resizer.PROGRESSIVE_BILINEAR.resize(image, 150, 150);
//                    ImageIO.write(image, "jpg", new File(PATH_AVATAR));
//                }
//
//                if(!profile.is_private()) {
//                    FeedUserRequest req = new FeedUserRequest(userId);
//                    CompletableFuture<FeedUserResponse> response = client.sendRequest(req);
//
//                    if(response.get() != null) {
//                        FeedUserResponse feedUserResponse = response.get();
//
//                        for(TimelineMedia timelineMedia : feedUserResponse.getItems()) {
//                            int likes = timelineMedia.getLike_count();
//                            int comments = timelineMedia.getComment_count();
//
//                            if(timelineMedia instanceof TimelineImageMedia) {
//                                TimelineImageMedia timelineImageMedia = (TimelineImageMedia) timelineMedia;
//                                String url = timelineImageMedia.getImage_versions2().getCandidates().get(0).getUrl();
//
//                            } else if(timelineMedia instanceof TimelineCarouselMedia) {
//
//                                TimelineCarouselMedia timelineCarouselMedia = (TimelineCarouselMedia) timelineMedia;
//                                int index = 0;
//                                for(CarouselItem caraouselItem : timelineCarouselMedia.getCarousel_media()) {
//                                    index++;
//                                    if(caraouselItem.getMedia_type().equals("1")) {
//                                        ImageCarouselItem imageCaraouselItem = (ImageCarouselItem) caraouselItem;
//                                        String url = imageCaraouselItem.getImage_versions2().getCandidates().get(index).getUrl();
//                                        break;
//                                    }
//                                }
//
//                            }
//
//                            if(this.guiPanelContent.getPanelPictures().getLoadedPhotos().size() == 6) {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch(InterruptedException | IOException i) {
//            i.printStackTrace();
//        }
//
//        return profile;
//    }

    @SneakyThrows
    private Album getList(String userName) {
        List<Photo> imgs = new ArrayList<>();
        String login2 = "effectivemotion.application@gmail.com";
        String password2 = "qweqweqwe";
        IGClient client = IGClient.builder()
                .username(login2)
                .password(password2)
                .login();

//        IGClient client = login.getClient().getIgClient();
        CompletableFuture<UserAction> action = client.actions().users().findByUsername(userName);

        Profile profile = null;

        try {
            if(action.get() != null)  {
                System.out.println(action.get().getUser());
                profile = action.get().getUser();
                Long userId = profile.getPk();
                URL profilePictureUrl = new URL(profile.getProfile_pic_url());
                if(profilePictureUrl.toString().length() > 0) {
                    imgs.add(new Photo(profilePictureUrl.toString()));
                }

                if(!profile.is_private()) {
                    FeedUserRequest req = new FeedUserRequest(userId);
                    CompletableFuture<FeedUserResponse> response = client.sendRequest(req);

                    if(response.get() != null) {
                        FeedUserResponse feedUserResponse = response.get();

                        for(TimelineMedia timelineMedia : feedUserResponse.getItems()) {
                            int likes = timelineMedia.getLike_count();
                            int comments = timelineMedia.getComment_count();

                            if(timelineMedia instanceof TimelineImageMedia) {
                                TimelineImageMedia timelineImageMedia = (TimelineImageMedia) timelineMedia;
                                String url = timelineImageMedia.getImage_versions2().getCandidates().get(0).getUrl();
                                imgs.add(new Photo(url, likes, comments));

                            } else if(timelineMedia instanceof TimelineCarouselMedia) {

                                TimelineCarouselMedia timelineCarouselMedia = (TimelineCarouselMedia) timelineMedia;
                                int index = 0;
                                for(CarouselItem caraouselItem : timelineCarouselMedia.getCarousel_media()) {
                                    index++;
                                    if(caraouselItem.getMedia_type().equals("1")) {
                                        ImageCarouselItem imageCaraouselItem = (ImageCarouselItem) caraouselItem;
                                        String url = imageCaraouselItem.getImage_versions2().getCandidates().get(index).getUrl();
                                        imgs.add(new Photo(url, likes, comments));
                                        break;
                                    }
                                }

                            }

//                            if(this.guiPanelContent.getPanelPictures().getLoadedPhotos().size() == 6) {
//                                break;
//                            }
                        }
                    }
                }
            }

        } catch(InterruptedException | IOException i) {
            i.printStackTrace();
        }
        return new Album(imgs);
    }
}
