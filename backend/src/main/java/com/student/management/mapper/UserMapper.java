package com.student.management.mapper;

import com.student.management.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findList(@Param("username") String username,
                       @Param("realName") String realName,
                       @Param("role") String role,
                       @Param("offset") Integer offset,
                       @Param("limit") Integer limit);
    
    int findCount(@Param("username") String username,
                 @Param("realName") String realName,
                 @Param("role") String role);
    
    User findByUsername(String username);
    
    User findById(Long id);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updateById(@Param("id") Long id, @Param("password") String password);

    int updateLastToken(@Param("id") Long id, @Param("token") String token);

    User findByToken(String token);
} 