package org.zchzh.zrpcstarter.compress;

/**
 * @author zengchzh
 * @date 2021/9/26
 */
public interface Compress {

    /**
     * 压缩字节数组
     * @param bytes 压缩内容
     * @return 返回压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压字节数组
     * @param bytes 解压内容
     * @return 返回解压后的字节数组
     */
    byte[] decompress(byte[] bytes);
}
