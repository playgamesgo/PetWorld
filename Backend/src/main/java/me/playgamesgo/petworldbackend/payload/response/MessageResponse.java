package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
