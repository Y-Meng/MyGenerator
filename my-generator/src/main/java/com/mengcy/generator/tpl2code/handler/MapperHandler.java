package com.mengcy.generator.tpl2code.handler;

import com.mengcy.generator.tpl2code.config.CodeGenConfig;
import com.mengcy.generator.tpl2code.model.Table;
import com.mengcy.generator.util.VelocityUtil;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis代码生成类
 */
public class MapperHandler {

	/**
	 * generatorConfig模板路径
	 */
	private static final String GENERATOR_CONFIG_VM = "/generatorConfig.vm";

	/**
	 * 根据模板生成generatorConfig.xml文件
	 */
	public static void generator(CodeGenConfig config, List<Table> tables) throws Exception{

		String generatorConfigTemplatePath = MapperHandler.class.getResource(config.getTemplateRoot() + GENERATOR_CONFIG_VM).getPath();

		String generatorConfigPath = config.getExportRoot();
		File dir = new File(generatorConfigPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String generatorConfigXml = generatorConfigPath + "generatorConfig.xml";


		try {
			VelocityContext context = new VelocityContext();

			String packageName = config.getModulePackageName();
			context.put("tables", tables);
			context.put("generator_javaModelGenerator_targetPackage", packageName + ".entity");
			context.put("generator_sqlMapGenerator_targetPackage", packageName + ".mapper");
			context.put("generator_javaClientGenerator_targetPackage", packageName + ".mapper");
			context.put("targetProject", config.getExportRoot());
			context.put("targetProject_sqlMap", config.getExportRoot());
			context.put("last_insert_id_tables", config.getLastInsertIdTables());

			context.put("generator_jdbc_driver", config.getJdbcDriver());
			context.put("generator_jdbc_url", config.getJdbcUrl());
			context.put("generator_jdbc_username", config.getJdbcUser());
			context.put("generator_jdbc_password", config.getJdbcPassword());

			// 生成generatorConfig.xml
			VelocityUtil.generate(generatorConfigTemplatePath, generatorConfigXml, context);

			// 删除旧代码
			deleteDir(new File(config.getExportRoot() + "/" + config.getModulePackageName().replaceAll("\\.", "/") + "/entity"));
			deleteDir(new File(config.getExportRoot() + "/" + config.getModulePackageName().replaceAll("\\.", "/") + "/mapper"));
			deleteDir(new File(config.getExportRoot() + "/" + config.getModulePackageName().replaceAll("\\.", "/") + "/mapper"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("生成generatorConfig.xml文件...");


		List<String> warnings = new ArrayList<>();
		File configFile = new File(generatorConfigXml);
		if(configFile.exists()){
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration parseConfiguration = cp.parseConfiguration(configFile);
			DefaultShellCallback callback = new DefaultShellCallback(true);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(parseConfiguration, callback, warnings);
			myBatisGenerator.generate(null);
			for (String warning : warnings) {
				System.out.println(warning);
			}
			System.out.println("MapperGenerator生成完成...");
		}else{
			System.out.println("生成generatorConfig失败...");
		}
	}

	// 递归删除非空文件夹
	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
		}
		dir.delete();
	}

}
