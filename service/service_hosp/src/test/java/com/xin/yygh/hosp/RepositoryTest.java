package com.xin.yygh.hosp;

import com.xin.yygh.hosp.entities.User;
import com.xin.yygh.hosp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.List;

@SpringBootTest
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * 排序
     */
    @Test
    public void sortTest() {
        int pageNum = 1;
        int pageSize = 2;
        Sort sort = Sort.by("age").descending();
        sort.and(Sort.by("_id").ascending());

        Pageable pageAble = PageRequest.of(1,2,sort);
        Page<User> all = userRepository.findAll(pageAble);
        for (User user : all) {
            System.out.println(user);
        }
    }

    /**
     * 查询
     *      findById
     */
    @Test
    public void queryTest() {
        // findById : 根据id 来查询
        User user = userRepository.findById("1").get();
        System.out.println("********************");
        // findAll查询所有
        List<User> list = userRepository.findAll();

        for (User user1 : list) {
            System.out.println(user1);
        }
        System.out.println("********************");

        // 条件查询
        // 1. 构建查询条件
        Example<User> userExample = Example.of(user);
        List<User> all = userRepository.findAll(userExample);
        for (User user1 : all) {
            System.out.println(user1);
        }
        System.out.println("********************");

        // 模糊查询
        // 1. 构建查询条件
        User user1 = new User();
        user1.setName("i");

        // 构建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：包含
//                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING) //改变默认字符串匹配方式：开头
//                .withStringMatcher(ExampleMatcher.StringMatcher.ENDING) //改变默认字符串匹配方式：结尾
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<User> example = Example.of(user1, matcher);
        List<User> users = userRepository.findAll(example);
        for (User user2 : users) {
            System.out.println(user2);
        }

    }

    /**
     * 修改
     *      save
     */
    @Test
    public void updateTest() {
        // 1. 先查询
        User user = userRepository.findById("3").get();
        // 2. 修改
        user.setGender(true);
        // 3. 执行
        userRepository.save(user);
    }

    /**
     * 删除操作
     *      deleteById
     *      deleteAll
     */
    @Test
    public void deleteTest() {
        // deleteById :  根据id来删除
//        userRepository.deleteById("2");

        // deleteAll : 删除全部 或 指定集合里的元素

    }


    /**
     * 添加操作
     *      insert
     *      save
     */
    @Test
    public void insertTest() {
        // save方法可以添加和修改，当存在是修改不存在是太添加
        // 注意 : 修改建议先根据id查询再修改
//        userRepository.save(new User("10","Smith",30,false));

        // insert方法只能添加操作
        userRepository.insert(new User("11","mike",25,true));
    }
}
