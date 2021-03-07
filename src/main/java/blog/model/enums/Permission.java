package blog.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    USER("user:write"),
    MODERATE("user:moderate");


    @Getter
    private final String permission;
}
