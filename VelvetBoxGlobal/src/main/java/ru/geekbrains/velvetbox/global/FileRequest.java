package ru.geekbrains.velvetbox.global;

import lombok.Data;

@Data
public class FileRequest implements GlobalMessagingService {

    private final boolean isClicked;
    private final String name;

}
