package ${basePackage}.controller;
import ${basePackage}.utils.Result;
import ${basePackage}.utils.ResultGenerator;
import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import ${basePackage}.utils.PageBean;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by ${author} on ${date}.
*/
@RestController
@RequestMapping("${baseRequestMapping}")
public class ${modelNameUpperCamel}Controller {
    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @PostMapping("/add")
    public Result add(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.save(${modelNameLowerCamel});
        return ResultGenerator.successResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Integer id) {
        ${modelNameLowerCamel}Service.deleteById(id);
        return ResultGenerator.successResult();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.update(${modelNameLowerCamel});
        return ResultGenerator.successResult();
    }

	@GetMapping("/detail")
	public Result detail(@RequestParam Object id) {
	    	 ${modelNameUpperCamel} ${modelNameLowerCamel}  = null;
	    	if (id instanceof String) {
	    		${modelNameLowerCamel} = ${modelNameLowerCamel}Service.findByStringId((String)id);
	    	}
	    	else {
	    		${modelNameLowerCamel} = ${modelNameLowerCamel}Service.findById((Integer)id);
	    	}
	        
	        return ResultGenerator.successResult(${modelNameLowerCamel});
	 }

    @GetMapping("/list")
    public Result list(PageBean<${modelNameUpperCamel}> page) {
        PageHelper.startPage(page.getPageNum(),page.getSize());
        List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Service.findAll();
        page.setList(list);
        return ResultGenerator.successResult(page);
    }
}
