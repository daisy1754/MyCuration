package com.pluea.filfeed.rss;



public class Feed {
	private int id_;
	private String title_;
	private String url_;
	private String iconPath;
	private int unreadAriticlesCount_;
	private String siteUrl;
	
	public static final String DEDAULT_HATENA_POINT = "-1";
	public static final String DEDAULT_ICON_PATH = "defaultIconPath";
	
	public Feed(int id,String title,String url, String iconPath, String siteUrl) {
		id_    = id;
		title_ = title;
		url_   = url;
		this.iconPath = iconPath;
		this.siteUrl = siteUrl;
	}
	
	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public void setTitle(String title) {
		title_ = title;
	}
	
	public void setUrl(String url) {
		url_ = url;
	}
	
	public void setId(Integer id) {
		id_ = id;
	}
	
	public void setUnreadArticlesCount(int unreadArticlesCount) {
		unreadAriticlesCount_ = unreadArticlesCount;
	}
	
	public int getUnreadAriticlesCount() {
		return unreadAriticlesCount_;
	}
	
	public Integer getId() {
		return id_;
	}
	
	public String getTitle() {
		return title_;
	}
	
	public String getUrl() {
		return url_;
	}

	public String getIconPath() {
		return iconPath;
	}
	
	
}