package org.zchzh.zrpcstarter.exception;

/**
 * @author zengchzh
 * @date 2021/5/20
 */
public class CommonException extends RuntimeException {

    private static final long serialVersionUID = -1249719013881196148L;

    public CommonException(String msg) {
        super(msg);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
