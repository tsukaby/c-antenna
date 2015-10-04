package com.tsukaby.c_antenna.entity

class AnalyzeRequest(var text: String) {
  def getText:String = {
    text
  }
  def setText(text: String):Unit = {
    this.text = text
  }
}
