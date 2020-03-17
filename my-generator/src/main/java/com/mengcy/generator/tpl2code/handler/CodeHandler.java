package com.mengcy.generator.tpl2code.handler;

import com.mengcy.generator.tpl2code.config.CodeGenConfig;
import com.mengcy.generator.tpl2code.model.Table;
import com.mengcy.generator.util.StringHelper;
import com.mengcy.generator.util.VelocityUtil;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zkzc-mcy on 2017/11/7.
 */
public class CodeHandler {


    public static void  generate(CodeGenConfig config, List<Table> tables) {

        // 生成代码目录
        String codePath = config.getExportRoot() + "/" + config.getModulePackageName().replaceAll("\\.", "/");

        File codeDir = new File(codePath);
        if(!codeDir.exists()){
            codeDir.mkdirs();
        }

        String templateRootPath = CodeHandler.class.getResource(config.getTemplateRoot()).getPath();
        File templateDir = new File(templateRootPath);
        if(!templateDir.exists()){
            System.out.println("模板不存在");
            return;
        }

        System.out.println("========== 开始生成代码文件 ==========");
        for (int i = 0; i < tables.size(); i++) {

            Table table = tables.get(i);

            String model = table.getModelName();
            try {
                generateTemplateCode(templateDir, codePath, config.getModulePackageName(), model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("========== 结束生成代码文件 ==========");
    }

    /**
     * 递归生成模板目录
     * @param template 模板目录或文件对象
     * @param targetPath 代码生成根目录
     */
    private static void generateTemplateCode(File template, String targetPath, String packageName, String modelName) throws Exception {
        if(template.isDirectory()){
            File[] files = template.listFiles();
            File targetDir = new File(targetPath);
            if(!targetDir.exists()){
                targetDir.mkdirs();
            }
            for(File file : files){
                String folderPath = targetPath + "/" + file.getName();
                generateTemplateCode(file, folderPath, packageName, modelName);
            }
        }else{
            if(!template.getName().contains("generatorConfig")){
                String targetFileName = modelName + template.getName().replace(".vm",".java");
                String targetFilePath = targetPath.replace(template.getName(),targetFileName);
                File targetFile = new File(targetFilePath);
                if(!targetFile.exists()){
                    String ctime = new SimpleDateFormat("yyyy/M/d").format(new Date());
                    VelocityContext context = new VelocityContext();

                    context.put(ContextKey.PACKAGE_NAME, packageName);
                    context.put(ContextKey.MODEL_NAME, modelName);
                    context.put(ContextKey.MODEL_INSTANCE, StringHelper.toLowerCaseFirstOne(modelName));
                    context.put(ContextKey.CREATE_TIME, ctime);

                    VelocityUtil.generate(template.getCanonicalPath(), targetFilePath, context);
                    System.out.println("生成文件：" + targetFilePath);
                }else{
                    System.out.println("文件已存在 ："+ targetFilePath);
                }
            }
        }
    }

    public class ContextKey{
        public static final String PACKAGE_NAME = "package";
        public static final String MODEL_NAME = "Model";
        public static final String MODEL_INSTANCE = "model";
        public static final String CREATE_TIME = "ctime";
    }
}
