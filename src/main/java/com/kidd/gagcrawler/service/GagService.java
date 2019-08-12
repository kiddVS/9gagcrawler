package com.kidd.gagcrawler.service;

import com.kidd.gagcrawler.mapper.GagPoMapper;
import com.kidd.gagcrawler.model.Gag;
import com.kidd.gagcrawler.model.GagPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GagService {

    @Autowired
    private GagPoMapper gagPoMapper;

    // 如果没有这条gag记录时插入
    public void insertIfIdNE(GagPo gagPo){
        if(gagPo!=null){
            GagPo gagPo1 = gagPoMapper.selectById(gagPo.getId());
            if(gagPo1!=null){
                return;
            }
            gagPoMapper.insert(gagPo);
        }
    }

    public List<GagPo> selectPhotoGagsGtDateTime(LocalDateTime dateTime){
        return gagPoMapper.selectPhotoGagsGtDateTime(dateTime);
    }
}
