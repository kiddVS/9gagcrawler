package com.kidd.gagcrawler.mapper;

import com.kidd.gagcrawler.model.GagPo;

import java.time.LocalDateTime;
import java.util.List;

public interface GagPoMapper {
    int deleteByPrimaryKey(Integer globalid);

    int insert(GagPo record);

    int insertSelective(GagPo record);

    GagPo selectByPrimaryKey(Integer globalid);

    int updateByPrimaryKeySelective(GagPo record);

    int updateByPrimaryKey(GagPo record);

    GagPo selectById(String id);

    List<GagPo> selectPhotoGagsGtDateTime(LocalDateTime dateTime);

    List<GagPo> selectVideoGagsGtDateTime(LocalDateTime dateTime);
}