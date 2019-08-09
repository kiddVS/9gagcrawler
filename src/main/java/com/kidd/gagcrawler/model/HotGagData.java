package com.kidd.gagcrawler.model;

import lombok.Data;

@Data
public class HotGagData {

    private String nextCursor;

    private Gag[] posts;

    private Tag[] tags;

}
