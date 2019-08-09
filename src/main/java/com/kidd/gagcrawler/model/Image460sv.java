package com.kidd.gagcrawler.model;

import lombok.Data;

@Data
public class Image460sv extends ImageBase {

    private Integer duration;

    private String h265Url;

    private Integer hasAudio;

    private String vp9Url;

}
