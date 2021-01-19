package com.snakyhy.snakymail.member.exception;

public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户姓名存在");
    }
}
