/**
 * モデルのモジュール。
 */
module Model {
  "use strict";

  /**
   * Webサイト
   */
  export class Site {
    id:number;
    name:string;
    url:string;
    thumbnail:string;
  }

  /**
   * 記事
   */
  export class Article {
    url:string;
    title:string;
    thumbnail:string;
    tags:string;
    siteName:string;
    createdAt:Date;
  }

  /**
   * 簡単な検索条件。ページング用。
   */
  export class SimpleSearchCondition {
    page:number;
    count:number;
  }

  /**
   * ページングによる検索の結果
   */
  export class Page<T> {
    items:Array<T>; //取得したオブジェクト
    total:number; //ページングしない場合の全体件数
  }
}
