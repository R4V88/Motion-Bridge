package com.motionbridge.motionbridge.email.application.port;


public interface EmailSender {

    void send(String to, String email);
}
