package ru.geekbrains.handler;

import ru.geekbrains.velvetbox.global.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class VelvetFileHandler extends SimpleChannelInboundHandler<GlobalMessagingService> {

    private Path currentDir;
    private Path rootDir;

    public VelvetFileHandler() {
        currentDir = Path.of("ServerUsers");
        rootDir = currentDir;
        File serverDir = new File(String.valueOf(currentDir));
        if (!serverDir.exists()){
            serverDir.mkdir();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListFiles(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GlobalMessagingService cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            if(fileRequest.isClicked()) {
                String fileName = fileRequest.getName();
                if (fileName.equals("..")) {
                    currentDir = currentDir.getParent();
                    ListFiles list = new ListFiles(currentDir);
                    if(currentDir.equals(rootDir)) {
                        ctx.writeAndFlush(list);
                        return;
                    }
                    list.getFiles().add(0, "..");
                    ctx.writeAndFlush(list);
                    return;
                }
                ListFiles listFiles = new ListFiles(currentDir);
                List<String> arrFiles = listFiles.getFiles();
                Optional<String> lst = arrFiles.stream()
                        .filter(x -> x.equals(fileName)).findFirst();

                if(lst.isPresent() && new File(String.valueOf(currentDir.resolve(lst.get()))).isDirectory()) {
                    currentDir = currentDir.resolve(Path.of(lst.get()));
                    ListFiles list = new ListFiles(currentDir);
                    list.getFiles().add(0, "..");
                    ctx.writeAndFlush(list);
                    return;
                }
                return;
            }
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));
        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ListFiles listFiles = new ListFiles(currentDir);
            if(!currentDir.equals(rootDir))
                listFiles.getFiles().add(0, "..");
            ctx.writeAndFlush(listFiles);
        }

    }
}
