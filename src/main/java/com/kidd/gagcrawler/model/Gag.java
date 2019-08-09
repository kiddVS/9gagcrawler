package com.kidd.gagcrawler.model;

import lombok.Data;


@Data
public class Gag {

    private Integer commentsCount;

    private String creationTs;

    private String descriptionHtml;

    private Integer downVoteCount;

    private Integer hasLongPostCover;

    private String id;

    private Images images;

    private Integer isVoteMasked;

    private Integer nsfw;

    private Integer promoted;

    private String sourceDomain;

    private String sourceUrl;

    private String title;

    private String type;

    private String url;

    private Integer upVoteCount;

}
