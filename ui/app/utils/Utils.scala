package utils

import play.api.libs.json.JsValue

object Utils {
  
  def field (msg: JsValue, fieldName: String): String = {
    (msg \ fieldName).as[String]
  }
  
  def longField (msg: JsValue, fieldName: String): Long = {
    (msg \ fieldName).as[Long]
  }
  
  def intField (msg: JsValue, fieldName: String): Int = {
    longField (msg, fieldName).toInt
  }
}