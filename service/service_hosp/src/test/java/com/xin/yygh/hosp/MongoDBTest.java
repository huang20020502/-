package com.xin.yygh.hosp;

import com.mongodb.client.result.DeleteResult;
import com.xin.yygh.hosp.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
public class MongoDBTest {

    // 注入mongoTemplate
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询分页
     */

    @Test
    public void pageTest() {
        int pageNum = 1;
        int pageSize = 2;
        Query query = new Query(Criteria.where("age").gte(18));

        List<User> users = mongoTemplate.find(query.skip((pageNum - 1) * pageSize).limit(pageSize), User.class);
        for (User user : users) {
            System.out.println(user);
        }
    }

    /**
     * 查询
     *      findById
     *      findAll
     */
    @Test
    public void queryTest() {
        // 构建模糊查询
        Pattern pattern = Pattern.compile(".*t.*", Pattern.CASE_INSENSITIVE); // Pattern.CASE_INSENSITIVE 不区分大小写
        new Query(Criteria.where("name").regex(pattern));

        // 构建and关系的查询条件
        Query query = new Query(Criteria.where("_id").is("1").andOperator(Criteria.where("name").is("王五")));

        // 构建or关系的查询条件
        Criteria criteria2 = new Criteria("name").is("赵六");
        Criteria criteria1 = new Criteria("name").is("王五");

        Criteria or = new Criteria();

        Query orQuery = new Query(or.orOperator(criteria1,criteria2));


        List<User> users = mongoTemplate.find(orQuery, User.class);
        for (User user : users) {
            System.out.println(user);
        }

        User user = mongoTemplate.findById("1", User.class);

//        List<User> users = mongoTemplate.findAll(User.class);
//        for (User user : users) {
//            System.out.println(user);
//        }
    }

    /**
     * 修改操作
     *      updateFirst  :  修改查询出来的第一个文档
     *      updateMulti  :  修改所有查询出来的文档
     *      upsert : 当集合中有是修改操作，没有将update中的数据作为文档添加到集合中
     */
    @Test
    public void modifyTest() {



        // 1. 构造查询条件
//        Query query = new Query(Criteria.where("_id").is("4"));
        Query query = new Query(Criteria.where("age").is(25));
        // 2. 设置修改字段
        Update update = new Update();
        update.set("name","张三");
        update.set("age",18);
        // 3. 执行
//        mongoTemplate.upsert(query,update,User.class);
        mongoTemplate.updateFirst(query,update,User.class);
    }


    /**
     * 删除操作
     * remove(query, clazz)
     *   query : 查询条件
     *   clazz : 指定类
     */
    @Test
    public void deleteTest()  {
        Query query = new Query(Criteria.where("_id").is("4"));
        DeleteResult result = mongoTemplate.remove(query, User.class);
        System.out.println(result.getDeletedCount());
    }


    /**
     * insert : 添加操作
     * save   : 当对象不存在的时候，添加操作。存在是修改操作
     */
    @Test
    public void insertTest() {
//        mongoTemplate.insert(new User("王五",22,true));

        mongoTemplate.save(new User("1","王五",20,true));
    }

}
