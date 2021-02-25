package com.example.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chei1
 */
@Service
public class OssFileUtil {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    public Mono<String> uploadAliyun(MultipartFile file, String fileName) throws IOException {
        //外面获取文件输入流，最后方便关闭
        InputStream in = file.getInputStream();
        try {
            //2 创建OssClient对象
            OSS ossClient =new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
            //3 获取文件信息，为了上传
            // meta设置请求头
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("image/jpg");
            //4 设置知道文件夹
            ossClient.putObject(bucketName,fileName,in, meta);
            //5 关闭ossClient
            ossClient.shutdown();
            //6 返回上传之后地址，拼接地址
            String uploadUrl = "https://"+bucketName+"."+endpoint+"/"+fileName;
            return Mono.just(uploadUrl);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            in.close();
        }
    }
}
