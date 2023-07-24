package com.xwj.Controller;

import com.xwj.util.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {/*创建一个MultipartFile的类*/
    @Value("${reggie.path}")  /*指定配置文件的一个参数名*/
    private String BasePath;

    @PostMapping("/upload")/*必须是post的方式*/ /*这个参数名必须是要与前端参数的名一样所以这里为file*/
    public R<String> upload(MultipartFile file){
                         /*这时file是一个临时文件  file代表一个文件 需要自行转存到一个自己指定的位置*/
        /*获取原始文件名*/
        String originalFilename = file.getOriginalFilename();
        /*从原始名后面截取出后缀名*/                              /*截取删除掉的最后一位以得到.jpg*/
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        /*进行随机起名 以防止出现重名覆盖 用UUID  记得加上suffix后缀名*/
        String filename= UUID.randomUUID().toString()+suffix;

        /*创建目录，假如目录不存在就创建*/
        File dir=new File(BasePath);
        if (!dir.exists()){
            /*目录不存在 创建*/
            dir.mkdirs();
        }

        try {
            /*将临时文件转存到指定位置 并起名为01.jpg*/
            file.transferTo(new File(BasePath+filename));
        } catch (IOException e) {              /*需要把文件转存位置改为动态位置 可以通过配置文件来做 applicatlion.yml*/
            e.printStackTrace();
        }
        log.info(file.toString());
        return R.success(filename);
    }
    @GetMapping("/download")  /*下载文件将上传后的文件重新回显到浏览器中去*/
    public void download(String name, HttpServletResponse response){
        /*利用文件输入流和输出流*/
        try {  /*创建一个输入流*/                                               /*用输入流读取上传的文件*/
            FileInputStream fileInputStream = new FileInputStream(new File(BasePath+name));

             /*创建输出流 这里可以不用new因为需要用到response  用来将已读取好的文件进行编写回显到浏览器中去*/
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");    /*设置文件类型*/

            int len=0;
            byte[] bytes=new byte[1024];  /*创建一个byte数组存放数据*/
            while ((len=fileInputStream.read(bytes))!=-1){
             /*在条件里读取数据并把每一次读取数据的赋值给len  当有一次读取len为-1代表读取完毕 退出循环*/
                    outputStream.write(bytes,0,len); /*每次循环写bytes 从0开始 到len长度结束*/
                    outputStream.flush();/*刷新*/

            }
            outputStream.close();
            fileInputStream.close(); /*注意关闭顺序 先打开后关闭 后打开的先关*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
