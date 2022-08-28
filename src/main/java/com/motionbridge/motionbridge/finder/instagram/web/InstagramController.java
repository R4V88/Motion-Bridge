package com.motionbridge.motionbridge.finder.instagram.web;

import com.motionbridge.motionbridge.finder.instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.finder.instagram.model.Album;
import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "/api/finder", description = "Instagram integration")
@RequestMapping("/api/finder")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstagramController {

    final InstagramUseCase instagramService;
    final CurrentlyLoggedUserProvider currentlyLoggedUserProvider;

    @SneakyThrows
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, pobranie zdjec z instagrama")
    @GetMapping("/instagram")
    @ApiResponse(description = "OK", responseCode = "200")
    @ApiResponse(description = "BadRequest, when Subscription is not Active", responseCode = "404")
    @ApiResponse(description = "BadRequest, when given wrong subscription id", responseCode = "404")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Album> getContent(@RequestBody RequestAlbumCommand requestAlbumCommand) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        final Album album = instagramService.getList(requestAlbumCommand, currentLoggedUsername);
        return ResponseEntity.ok(album);
    }

    @Value
    public static class RequestAlbumCommand {
        String profile;
        long subscriptionId;
    }
}
