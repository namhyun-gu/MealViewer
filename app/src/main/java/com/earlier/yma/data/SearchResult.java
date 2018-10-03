package com.earlier.yma.data;

import com.google.gson.annotations.SerializedName;

public class SearchResult {

  public static SearchResult empty() {
    return new SearchResult(null);
  }

  private Detail[] details;

  public SearchResult(Detail[] result) {
    this.details = result;
  }

  public Detail[] details() {
    return details;
  }

  public static class Detail {

    @SerializedName("atptOfcdcNm")
    private String pathName;

    @SerializedName("kraOrgNm")
    private String schoolName;

    @SerializedName("orgCode")
    private String schulCode;

    private String schulCrseScCode;

    private String schulKndScCode;

    @SerializedName("zipAdres")
    private String zipAddress;

    public String getPathName() {
      return pathName;
    }

    public String getSchoolName() {
      return schoolName;
    }

    public String getSchulCode() {
      return schulCode;
    }

    public String getSchulCrseScCode() {
      return schulCrseScCode;
    }

    public String getSchulKndScCode() {
      return schulKndScCode;
    }

    public String getZipAddress() {
      return zipAddress;
    }

  }
}
