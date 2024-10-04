package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListAdResponse {
    private List<AdResponse> items;
    private Integer totalCount;

    public ListAdResponse(List<AdResponse> items) {
        this.items = items;
        this.totalCount = items.size();
    }
}
