module ru.geekbrains.velvetbox {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires logback.classic;
    requires io.netty.codec;
    requires VelvetBoxGlobal;
    requires lombok;
    requires java.sql;

    opens ru.geekbrains.velvetbox to javafx.fxml;
    exports ru.geekbrains.velvetbox;
    exports ru.geekbrains.velvetbox.controller;
    opens ru.geekbrains.velvetbox.controller to javafx.fxml;
}