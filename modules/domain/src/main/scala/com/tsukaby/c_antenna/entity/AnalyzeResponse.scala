package com.tsukaby.c_antenna.entity

class AnalyzeResponse() {
  var tags: Array[String] = Array()

  def getTags: Array[String] = {
    tags
  }

  def setTags(tags: Array[String]): Unit = {
    this.tags = tags
  }
}