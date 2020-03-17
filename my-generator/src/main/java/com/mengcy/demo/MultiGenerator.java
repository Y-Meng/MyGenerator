package com.mengcy.demo;

import com.mengcy.generator.tpl2code.config.CodeGenConfig;
import com.mengcy.generator.tpl2code.model.Table;
import com.mengcy.generator.tpl2code.handler.CodeHandler;
import com.mengcy.generator.tpl2code.handler.MapperHandler;
import com.mengcy.generator.tpl2code.handler.TableLoader;

import java.util.List;

/**
 * 代码生成类
 */
public class MultiGenerator {

	/**
	 * 自动代码生成
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		CodeGenConfig config = CodeGenConfig.init();

		List<Table> tables = TableLoader.loadTables(config);
		if(tables != null && tables.size() > 0){
			// mapper代码生成
			if(config.getEnableMapper()){
				MapperHandler.generator(config, tables);
			}
			// 模板代码生成
			CodeHandler.generate(config, tables);
		}else{
			System.out.println("未查询到目标数据表");
		}
	}

}
