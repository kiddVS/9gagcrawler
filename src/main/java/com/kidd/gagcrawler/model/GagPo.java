package com.kidd.gagcrawler.model;

import java.time.LocalDateTime;
import java.util.Date;

public class GagPo {
    private Integer globalid;

    private String id;

    private String title;

    private String images;

    private String url;

    private Integer commentscount;

    private String creationts;

    private String descriptionhtml;

    private Integer downvotecount;

    private Integer haslongpostcover;

    private Integer isvotemasked;

    private Integer nsfw;

    private Integer promoted;

    private String sourcedomain;

    private String sourceurl;

    private String type;

    private Integer upvotecount;

    private LocalDateTime crawlerdate;

    public Integer getGlobalid() {
        return globalid;
    }

    public void setGlobalid(Integer globalid) {
        this.globalid = globalid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images == null ? null : images.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getCommentscount() {
        return commentscount;
    }

    public void setCommentscount(Integer commentscount) {
        this.commentscount = commentscount;
    }

    public String getCreationts() {
        return creationts;
    }

    public void setCreationts(String creationts) {
        this.creationts = creationts == null ? null : creationts.trim();
    }

    public String getDescriptionhtml() {
        return descriptionhtml;
    }

    public void setDescriptionhtml(String descriptionhtml) {
        this.descriptionhtml = descriptionhtml == null ? null : descriptionhtml.trim();
    }

    public Integer getDownvotecount() {
        return downvotecount;
    }

    public void setDownvotecount(Integer downvotecount) {
        this.downvotecount = downvotecount;
    }

    public Integer getHaslongpostcover() {
        return haslongpostcover;
    }

    public void setHaslongpostcover(Integer haslongpostcover) {
        this.haslongpostcover = haslongpostcover;
    }

    public Integer getIsvotemasked() {
        return isvotemasked;
    }

    public void setIsvotemasked(Integer isvotemasked) {
        this.isvotemasked = isvotemasked;
    }

    public Integer getNsfw() {
        return nsfw;
    }

    public void setNsfw(Integer nsfw) {
        this.nsfw = nsfw;
    }

    public Integer getPromoted() {
        return promoted;
    }

    public void setPromoted(Integer promoted) {
        this.promoted = promoted;
    }

    public String getSourcedomain() {
        return sourcedomain;
    }

    public void setSourcedomain(String sourcedomain) {
        this.sourcedomain = sourcedomain == null ? null : sourcedomain.trim();
    }

    public String getSourceurl() {
        return sourceurl;
    }

    public void setSourceurl(String sourceurl) {
        this.sourceurl = sourceurl == null ? null : sourceurl.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getUpvotecount() {
        return upvotecount;
    }

    public void setUpvotecount(Integer upvotecount) {
        this.upvotecount = upvotecount;
    }

    public LocalDateTime getCrawlerdate() {
        return crawlerdate;
    }

    public void setCrawlerdate(LocalDateTime crawlerdate) {
        this.crawlerdate = crawlerdate;
    }
}