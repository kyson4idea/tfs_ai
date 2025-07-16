package com.smy.tfs.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public class NotificationDto implements Serializable{
    private static final long serialVersionUID = -4608614658326874291L;

    @Data
    public static class CreateChatGroup implements Serializable {
        public CreateChatGroup(){

        }
        public CreateChatGroup(List<String> userList, String groupName, String owner, String chatId, String hello) {
            this.userList = userList;
            this.groupName = groupName;
            this.owner = owner;
            this.chatId = chatId;
            this.hello = hello;
        }
        @NotBlank()
        private List<String> userList;

        @NotBlank()
        private String groupName;

        private String owner;

        private String chatId;

        @NotBlank()
        private String hello;
    }
}
