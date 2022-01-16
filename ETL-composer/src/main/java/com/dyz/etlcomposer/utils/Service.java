package com.dyz.etlcomposer.utils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.beans.IntrospectionException;
import java.util.List;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 */
public interface Service<T> {
  void save(T model);//持久化

  void save(List<T> models);//批量持久化

  void deleteById(Integer id);//通过主鍵刪除

  void deleteByIds(String ids);//批量刪除 eg：ids -> “1,2,3,4”

  void update(T model);//更新

  T findById(Integer id);//通过ID查找

  T findByStringId(String id); // id为String类型的时候

  public void deleteByStringId(String id); // id为String类型的时候

  boolean judgeById(Integer id);//通过ID查找该id的数据是否存在

  T findBy(String fieldName, Object value) throws TooManyResultsException; //通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束

  List<T> findByIds(String ids);//通过多个ID查找//eg：ids -> “1,2,3,4”

  List<T> findByCondition(Condition condition);//根据条件查找

  List<T> findAll();//获取所有

  List<T> select(T record);//根据实体中的属性值进行查询，查询条件使用等号

  List<T> selectAll();//查询全部结果，select(null)方法能达到同样的效果

  T selectOne(T record);//根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号

  int selectCount(T record);//根据实体中的属性查询总数，查询条件使用等号

  int delete(T record);//根据实体属性作为条件进行删除，查询条件使用等号

  List<T> selectByCondition(Object condition);//根据Condition条件进行查询

  int updateByConditionSelective(@Param("record") T record, @Param("example") Object condition);//根据Condition条件更新实体record包含的不是null的属性值

  int deleteByCondition(Object condition);//根据Condition条件删除数据

  int deleteByCSql(String sql);//根据Condition条件删除数据

  List<T> selectByCSql(String sql);//根据Condition 规则的sql进行查询

  void saveAndUpdate(T t) throws IntrospectionException, IllegalAccessException;


}

