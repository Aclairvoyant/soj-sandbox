package com.sjdddd.sojsandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

import java.util.List;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/12 19:33
 **/
public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        // 获取默认的Docker客户端
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
//        PingCmd pingCmd = build.pingCmd();
//        pingCmd.exec();

        String image = "nginx:latest";
//        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback();
//        pullImageCmd.exec(pullImageResultCallback)
//                .awaitCompletion();
//        System.out.println("Pull image " + image + " success");

        // 创建容器
//        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(image)
//                .withCmd("echo", "hello world")
//                .exec();
//
//        System.out.println(createContainerResponse);

        // 查看容器
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containerList = listContainersCmd.withShowAll(true).exec();
        for (Container container : containerList) {
            System.out.println(container);
        }

        // 启动容器
        String containerId = "2fe8e1b2d7123f2b43fac38dc3354651e0875fde83d22ea17e247d841243095f";
//        dockerClient.startContainerCmd(containerId).exec();

        // 查看日志

        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };

        dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback)
                .awaitCompletion();


        // 删除容器
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
