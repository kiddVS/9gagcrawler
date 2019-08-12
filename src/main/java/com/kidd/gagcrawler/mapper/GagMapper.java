package com.kidd.gagcrawler.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GagMapper {
    @Select({"select count(1) from Gag"})
    Integer testCount();
}
